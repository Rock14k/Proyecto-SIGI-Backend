package com.sigi.servicio_notificacion.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.sigi.servicio_notificacion.dto.AlertaEmergenciaRequest;
import com.sigi.servicio_notificacion.repository.NotificacionRepository;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @InjectMocks
    private NotificacionService notificacionService;

    @Test
    @DisplayName("Crea una notificación por destinatario distinto")
    void alertaConExtra() {
        ReflectionTestUtils.setField(notificacionService, "usuariosExtraCsv", "5, 7");

        AlertaEmergenciaRequest req = new AlertaEmergenciaRequest();
        req.setEmergenciaId(10L);
        req.setDireccion("Los Aromos 100");
        req.setPrioridad("ALTA");
        req.setUsuarioReportanteId(3L);

        int n = notificacionService.crearAlertaPorEmergencia(req);

        assertEquals(3, n);
        verify(notificacionRepository, times(3)).save(any());
    }
}
