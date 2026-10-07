package pe.finanzasglobal.operacionescambio.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Prueba de integración de extremo a extremo (REST -> caso de uso -> dominio -> JPA/H2) con seguridad real. */
@SpringBootTest
@AutoConfigureMockMvc
class RegistrarOperacionIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    String body(long clienteId, String tipo, String monto) {
        return "{\"clienteId\":" + clienteId + ",\"tipo\":\"" + tipo + "\",\"moneda\":\"USD\",\"monto\":" + monto + "}";
    }

    @Test
    @DisplayName("Flujo completo: cajero registra, auditor consulta, supervisor anula")
    void flujoCompleto() throws Exception {
        String respuesta = mvc.perform(post("/api/v1/operaciones")
                        .with(httpBasic("cajero", "cajero123")).header("X-Office", "MIRAFLORES")
                        .contentType(MediaType.APPLICATION_JSON).content(body(1, "COMPRA", "1000")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.importeLiquidado").value(3688.90))
                .andExpect(jsonPath("$.usuario").value("cajero"))
                .andExpect(jsonPath("$.oficina").value("MIRAFLORES"))
                .andReturn().getResponse().getContentAsString();
        JsonNode node = json.readTree(respuesta);
        String id = node.get("id").asText();
        assertThat(id).isNotBlank();

        mvc.perform(get("/api/v1/operaciones/" + id).with(httpBasic("auditor", "audit123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("REGISTRADA"));

        mvc.perform(delete("/api/v1/operaciones/" + id).with(httpBasic("supervisor", "super123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ANULADA"));

        mvc.perform(delete("/api/v1/operaciones/" + id).with(httpBasic("supervisor", "super123")))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void sinCredencialesDevuelve401() throws Exception {
        mvc.perform(get("/api/v1/operaciones/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void credencialesInvalidasDevuelve401() throws Exception {
        mvc.perform(get("/api/v1/operaciones/00000000-0000-0000-0000-000000000000")
                        .with(httpBasic("cajero", "mala")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void auditorNoPuedeRegistrar() throws Exception {
        mvc.perform(post("/api/v1/operaciones").with(httpBasic("auditor", "audit123"))
                        .contentType(MediaType.APPLICATION_JSON).content(body(1, "COMPRA", "10")))
                .andExpect(status().isForbidden());
    }

    @Test
    void cajeroNoPuedeAnular() throws Exception {
        mvc.perform(delete("/api/v1/operaciones/00000000-0000-0000-0000-000000000000")
                        .with(httpBasic("cajero", "cajero123")))
                .andExpect(status().isForbidden());
    }

    @Test
    void montoSobreElLimiteDelClienteDevuelve422() throws Exception {
        mvc.perform(post("/api/v1/operaciones").with(httpBasic("cajero", "cajero123"))
                        .contentType(MediaType.APPLICATION_JSON).content(body(1, "VENTA", "20000")))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void clienteInexistenteDevuelve404() throws Exception {
        mvc.perform(post("/api/v1/operaciones").with(httpBasic("cajero", "cajero123"))
                        .contentType(MediaType.APPLICATION_JSON).content(body(404, "VENTA", "100")))
                .andExpect(status().isNotFound());
    }

    @Test
    void clienteInactivoDevuelve422() throws Exception {
        mvc.perform(post("/api/v1/operaciones").with(httpBasic("cajero", "cajero123"))
                        .contentType(MediaType.APPLICATION_JSON).content(body(999, "VENTA", "100")))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void healthEsPublico() throws Exception {
        mvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }
}
