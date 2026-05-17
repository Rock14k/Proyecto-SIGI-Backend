package com.sigi.servicio_reporte.service;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.sigi.servicio_reporte.model.Reporte.PrioridadReporte;
import com.sigi.servicio_reporte.repository.ReporteRepository;

@Service
public class PrioridadMetricasService {

    private static final Pattern TIPO_PATTERN = Pattern.compile("^\\[([A-Z_]+)\\]");
    private static final int HORAS_VENTANA = 24;

    private final ReporteRepository reporteRepository;

    public PrioridadMetricasService(ReporteRepository reporteRepository) {
        this.reporteRepository = reporteRepository;
    }

    public ResultadoPrioridad calcular(String descripcion, String direccion) {
        String tipo = extraerTipo(descripcion);
        LocalDateTime desde = LocalDateTime.now().minusHours(HORAS_VENTANA);

        long porTipo = reporteRepository.countByDescripcionStartingWithAndFechaReporteAfter(
                "[" + tipo + "]", desde);
        long porUbicacion = reporteRepository.countByDireccionIgnoreCaseAndFechaReporteAfter(
                direccion.trim(), desde);
        long similares = Math.max(porTipo, porUbicacion);

        PrioridadReporte base = prioridadBasePorTipo(tipo);
        PrioridadReporte finalP = elevarPorConsenso(base, similares);

        String motivo = construirMotivo(tipo, similares, porTipo, porUbicacion, finalP);
        return new ResultadoPrioridad(finalP, (int) similares, motivo);
    }

    private static String extraerTipo(String descripcion) {
        if (descripcion == null) {
            return "OTRO";
        }
        Matcher m = TIPO_PATTERN.matcher(descripcion.trim());
        return m.find() ? m.group(1) : "OTRO";
    }

    private static PrioridadReporte prioridadBasePorTipo(String tipo) {
        return switch (tipo) {
            case "INCENDIO", "FUGA_GAS" -> PrioridadReporte.ALTA;
            case "AGUA", "RUTA" -> PrioridadReporte.MEDIA;
            default -> PrioridadReporte.BAJA;
        };
    }

    private static PrioridadReporte elevarPorConsenso(PrioridadReporte base, long similares) {
        if (similares >= 10) {
            return PrioridadReporte.CRITICA;
        }
        if (similares >= 5) {
            return max(base, PrioridadReporte.ALTA);
        }
        if (similares >= 3) {
            return max(base, PrioridadReporte.MEDIA);
        }
        return base;
    }

    private static PrioridadReporte max(PrioridadReporte a, PrioridadReporte b) {
        return a.ordinal() >= b.ordinal() ? a : b;
    }

    private static String construirMotivo(
            String tipo, long similares, long porTipo, long porUbicacion, PrioridadReporte p) {
        if (similares >= 10) {
            return String.format(
                    "Prioridad %s: %d reportes similares en 24h (umbral crítico ≥10)", p.name(), similares);
        }
        if (similares >= 5) {
            return String.format(
                    "Prioridad %s: %d reportes similares en 24h (umbral alto ≥5)", p.name(), similares);
        }
        if (similares >= 3) {
            return String.format(
                    "Prioridad %s: %d reportes similares en 24h (umbral medio ≥3)", p.name(), similares);
        }
        return String.format(
                "Prioridad %s: gravedad base por tipo [%s] (%d por tipo, %d por ubicación)",
                p.name(), tipo, porTipo, porUbicacion);
    }

    public record ResultadoPrioridad(PrioridadReporte prioridad, int reportesSimilares, String motivoPrioridad) {}
}
