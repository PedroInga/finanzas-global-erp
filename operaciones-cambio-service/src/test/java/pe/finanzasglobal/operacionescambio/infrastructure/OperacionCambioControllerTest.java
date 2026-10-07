package pe.finanzasglobal.operacionescambio.infrastructure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pe.finanzasglobal.operacionescambio.application.exception.RecursoNoEncontradoException;
import pe.finanzasglobal.operacionescambio.application.port.in.AnularOperacionUseCase;
import pe.finanzasglobal.operacionescambio.application.port.in.ConsultarOperacionUseCase;
import pe.finanzasglobal.operacionescambio.application.port.in.RegistrarOperacionUseCase;
import pe.finanzasglobal.operacionescambio.domain.exception.ReglaNegocioException;
import pe.finanzasglobal.operacionescambio.domain.model.*;
import pe.finanzasglobal.operacionescambio.infrastructure.adapter.in.web.GlobalExceptionHandler;
import pe.finanzasglobal.operacionescambio.infrastructure.adapter.in.web.OperacionCambioController;
import pe.finanzasglobal.operacionescambio.infrastructure.config.SecurityConfig;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Prueba del adaptador REST + reglas de seguridad por rol (Spring Boot Test, slice web). */
@WebMvcTest(OperacionCambioController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class OperacionCambioControllerTest {

    @Autowired MockMvc mvc;
    @MockBean RegistrarOperacionUseCase registrar;
    @MockBean ConsultarOperacionUseCase consultar;
    @MockBean AnularOperacionUseCase anular;

    static final String BODY = "{\"clienteId\":1,\"tipo\":\"COMPRA\",\"moneda\":\"USD\",\"monto\":100}";

    OperacionCambio operacion() {
        return OperacionCambio.registrar(1L, TipoOperacion.COMPRA, Moneda.USD, new BigDecimal("100"),
                new TipoCambio(Moneda.USD, new BigDecimal("3.70"), new BigDecimal("3.75")), null, "cajero", "P");
    }

    @Test
    @DisplayName("Sin credenciales -> 401")
    void sinAutenticacion() throws Exception {
        mvc.perform(post("/api/v1/operaciones").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(registrar);
    }

    @Test
    @WithMockUser(username = "cajero", roles = "CAJERO")
    @DisplayName("CAJERO registra operación -> 201 con Location")
    void cajeroRegistra() throws Exception {
        when(registrar.registrar(any())).thenReturn(operacion());

        mvc.perform(post("/api/v1/operaciones").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.importeLiquidado").value(368.89))
                .andExpect(jsonPath("$.usuario").value("cajero"));
    }

    @Test
    @WithMockUser(username = "auditor", roles = "AUDITOR")
    @DisplayName("AUDITOR no puede registrar -> 403")
    void auditorNoRegistra() throws Exception {
        mvc.perform(post("/api/v1/operaciones").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isForbidden());
        verifyNoInteractions(registrar);
    }

    @Test
    @WithMockUser(roles = "CAJERO")
    @DisplayName("CAJERO no puede anular -> 403")
    void cajeroNoAnula() throws Exception {
        mvc.perform(delete("/api/v1/operaciones/" + UUID.randomUUID())).andExpect(status().isForbidden());
        verifyNoInteractions(anular);
    }

    @Test
    @WithMockUser(username = "supervisor", roles = "SUPERVISOR")
    void supervisorAnula() throws Exception {
        OperacionCambio op = operacion();
        op.anular("supervisor");
        when(anular.anular(any(), any())).thenReturn(op);

        mvc.perform(delete("/api/v1/operaciones/" + op.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ANULADA"));
    }

    @Test
    @WithMockUser(roles = "AUDITOR")
    void auditorConsulta() throws Exception {
        OperacionCambio op = operacion();
        when(consultar.porId(op.getId())).thenReturn(op);

        mvc.perform(get("/api/v1/operaciones/" + op.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value(1));
    }

    @Test
    @WithMockUser(roles = "CAJERO")
    void validacionDeEntrada() throws Exception {
        mvc.perform(post("/api/v1/operaciones").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clienteId\":1,\"tipo\":\"COMPRA\",\"moneda\":\"USD\",\"monto\":-5}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(registrar);
    }

    @Test
    @WithMockUser(roles = "CAJERO")
    void reglaDeNegocioDevuelve422() throws Exception {
        when(registrar.registrar(any())).thenThrow(new ReglaNegocioException("El monto excede el límite"));

        mvc.perform(post("/api/v1/operaciones").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(roles = "CAJERO")
    void inexistenteDevuelve404() throws Exception {
        UUID id = UUID.randomUUID();
        when(consultar.porId(id)).thenThrow(new RecursoNoEncontradoException("no existe"));

        mvc.perform(get("/api/v1/operaciones/" + id)).andExpect(status().isNotFound());
    }
}
