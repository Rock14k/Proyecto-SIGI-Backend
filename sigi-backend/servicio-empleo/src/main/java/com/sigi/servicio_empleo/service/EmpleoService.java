package com.sigi.servicio_empleo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sigi.servicio_empleo.dto.EmpleoRequest;
import com.sigi.servicio_empleo.dto.EmpleoResponse;
import com.sigi.servicio_empleo.dto.PostulacionResponse;
import com.sigi.servicio_empleo.model.Empleo;
import com.sigi.servicio_empleo.model.Postulacion;
import com.sigi.servicio_empleo.repository.EmpleoRepository;
import com.sigi.servicio_empleo.repository.PostulacionRepository;
import com.sigi.servicio_empleo.service.UsuarioConsultaService.DatosPostulante;

@Service
public class EmpleoService {

    private final EmpleoRepository empleoRepository;
    private final PostulacionRepository postulacionRepository;
    private final UsuarioConsultaService usuarioConsultaService;

    public EmpleoService(
            EmpleoRepository empleoRepository,
            PostulacionRepository postulacionRepository,
            UsuarioConsultaService usuarioConsultaService) {
        this.empleoRepository = empleoRepository;
        this.postulacionRepository = postulacionRepository;
        this.usuarioConsultaService = usuarioConsultaService;
    }

    public List<EmpleoResponse> listarActivos() {
        return empleoRepository.findByActivoTrueOrderByFechaCierreAsc().stream()
                .map(EmpleoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<EmpleoResponse> listarTodosAdmin() {
        return empleoRepository.findAll().stream()
                .map(EmpleoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public EmpleoResponse crear(EmpleoRequest req) {
        Empleo e = new Empleo();
        e.setTitulo(req.getTitulo());
        e.setDepartamento(req.getDepartamento());
        e.setPlazas(req.getPlazas());
        e.setDescripcion(req.getDescripcion());
        e.setFechaCierre(req.getFechaCierre());
        e.setActivo(true);
        return EmpleoResponse.fromEntity(empleoRepository.save(e));
    }

    @Transactional
    public EmpleoResponse actualizar(Long id, EmpleoRequest req) {
        Empleo e = empleoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleo no encontrado"));
        e.setTitulo(req.getTitulo());
        e.setDepartamento(req.getDepartamento());
        e.setPlazas(req.getPlazas());
        e.setDescripcion(req.getDescripcion());
        e.setFechaCierre(req.getFechaCierre());
        return EmpleoResponse.fromEntity(empleoRepository.save(e));
    }

    @Transactional
    public void eliminar(Long id) {
        Empleo e = empleoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleo no disponible"));
        e.setActivo(false);
        empleoRepository.save(e);
    }

    @Transactional
    public PostulacionResponse postular(Long empleoId, Long usuarioId) {
        Empleo empleo = empleoRepository.findById(empleoId)
                .orElseThrow(() -> new RuntimeException("Empleo no encontrado"));
        if (!empleo.isActivo()) {
            throw new RuntimeException("Empleo no disponible");
        }
        if (postulacionRepository.findByEmpleoIdAndUsuarioId(empleoId, usuarioId).isPresent()) {
            throw new RuntimeException("Ya postulaste a este empleo");
        }

        DatosPostulante datos = usuarioConsultaService.obtenerDatos(usuarioId);

        Postulacion p = new Postulacion();
        p.setEmpleoId(empleoId);
        p.setUsuarioId(usuarioId);
        p.setFechaPostulacion(LocalDateTime.now());
        p.setPostulanteNombre(datos.nombre());
        p.setPostulanteApellido(datos.apellido());
        p.setPostulanteEmail(datos.email());
        p.setPostulanteRut(datos.rut());
        return PostulacionResponse.fromEntity(postulacionRepository.save(p), empleo.getTitulo());
    }

    public List<PostulacionResponse> misPostulaciones(Long usuarioId) {
        return postulacionRepository.findByUsuarioIdOrderByFechaPostulacionDesc(usuarioId).stream()
                .map(p -> {
                    String titulo = empleoRepository.findById(p.getEmpleoId())
                            .map(Empleo::getTitulo)
                            .orElse(null);
                    return PostulacionResponse.fromEntity(p, titulo);
                })
                .collect(Collectors.toList());
    }

    public List<PostulacionResponse> todasPostulaciones() {
        return postulacionRepository.findAllByOrderByFechaPostulacionDesc().stream()
                .map(p -> {
                    String titulo = empleoRepository.findById(p.getEmpleoId())
                            .map(Empleo::getTitulo)
                            .orElse("Empleo #" + p.getEmpleoId());
                    return PostulacionResponse.fromEntity(p, titulo);
                })
                .collect(Collectors.toList());
    }
}
