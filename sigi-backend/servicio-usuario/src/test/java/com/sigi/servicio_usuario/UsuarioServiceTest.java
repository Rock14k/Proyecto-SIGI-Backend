package com.sigi.servicio_usuario;


// JUnit 5 - framework de pruebas
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

// Mockito - para crear objetos "falsos" (mocks) en las pruebas
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.sigi.servicio_usuario.dto.UsuarioRegistroDTO;
import com.sigi.servicio_usuario.dto.UsuarioResponseDTO;
import com.sigi.servicio_usuario.model.Usuario;
import com.sigi.servicio_usuario.repository.UsuarioRepository;
import com.sigi.servicio_usuario.service.UsuarioService;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

// Clase de prueba para el UsuarioService
// Las pruebas verifican que la lógica de negocio funciona correctamente
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    // @Mock crea un objeto falso de UsuarioRepository
    // No queremos conectarnos a la base de datos real en las pruebas
    @Mock
    private UsuarioRepository usuarioRepository;

    // @Mock para el encoder de contraseñas
    @Mock
    private PasswordEncoder passwordEncoder;

    // @InjectMocks crea el servicio real e inyecta los mocks en él
    // Así probamos el servicio sin sus dependencias reales
    @InjectMocks
    private UsuarioService usuarioService;

    // @Test marca este método como una prueba
    @Test
    // @DisplayName da un nombre descriptivo a la prueba
    @DisplayName("Debe registrar un usuario exitosamente")
    void debeRegistrarUsuarioExitosamente() {
        
        // ARRANGE (preparar): configuramos los datos de prueba
        UsuarioRegistroDTO dto = new UsuarioRegistroDTO();
        dto.setNombre("Rock");
        dto.setApellido("Durant");
        dto.setRut("28754250k");
        dto.setEmail("durant@test.com");
        dto.setPassword("Rock56");
        dto.setCertificadoResidenciaMediaId(99L);

        when(usuarioRepository.existsByEmail("durant@test.com")).thenReturn(false);
        when(usuarioRepository.existsByRut("28754250k")).thenReturn(false);
        when(passwordEncoder.encode("Rock56")).thenReturn("$2a$12$hasheado");

        // Creamos el usuario que "guardaríamos" en la base de datos
        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setId(1L);
        usuarioGuardado.setNombre("Rock");
        usuarioGuardado.setApellido("Durant");
        usuarioGuardado.setRut("28754250-k");
        usuarioGuardado.setEmail("durant@test.com");
        usuarioGuardado.setPassword("$2a$12$hasheado");
        usuarioGuardado.setRol(Usuario.Rol.CIUDADANO);
        usuarioGuardado.setActivo(true);

        // Simulamos que save() retorna el usuario guardado
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);

        // ACT (actuar): ejecutamos el método que queremos probar
        UsuarioResponseDTO resultado = usuarioService.registrar(dto);

        // ASSERT (verificar): comprobamos que el resultado es el esperado
        assertNotNull(resultado, "El resultado no debe ser null");
        assertEquals("Rock", resultado.getNombre(), "El nombre debe ser Rock");
        assertEquals("durant@test.com", resultado.getEmail(), "El email debe coincidir");
        
        // Verificamos que save() fue llamado exactamente una vez
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el email ya existe")
    void debeLanzarExcepcionSiEmailDuplicado() {
        
        // ARRANGE
        UsuarioRegistroDTO dto = new UsuarioRegistroDTO();
        dto.setEmail("existente@test.com");
        dto.setPassword("123456");
        dto.setNombre("Test");
        dto.setApellido("User");
        dto.setRut("11111111-1");
        dto.setCertificadoResidenciaMediaId(1L);

        when(usuarioRepository.existsByEmail("existente@test.com")).thenReturn(true);

        // ACT & ASSERT: verificamos que lanza una excepción
        // assertThrows verifica que se lanza la excepción esperada
        RuntimeException excepcion = assertThrows(
            RuntimeException.class,
            () -> usuarioService.registrar(dto),  // Esta línea debe lanzar la excepción
            "Debe lanzar RuntimeException si el email ya existe"
        );

        // Verificamos el mensaje de la excepción
        assertTrue(excepcion.getMessage().contains("Ya existe un usuario"));
        
        // Verificamos que save() NUNCA fue llamado (no se guardó nada)
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe encontrar usuario por ID")
    void debeEncontrarUsuarioPorId() {
        
        // ARRANGE
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("María");
        usuario.setEmail("maria@test.com");
        usuario.setRol(Usuario.Rol.OPERADOR_MUNICIPAL);

        // Optional.of() simula que SÍ se encontró el usuario
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // ACT
        UsuarioResponseDTO resultado = usuarioService.obtenerPorId(1L);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("María", resultado.getNombre());
    }

    @Test
    @DisplayName("Debe lanzar excepción si usuario no existe")
    void debeLanzarExcepcionSiUsuarioNoExiste() {
        
        // Optional.empty() simula que NO se encontró el usuario
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(
            RuntimeException.class,
            () -> usuarioService.obtenerPorId(999L)
        );
        assertNotNull(excepcion.getMessage());
    }
}