package pe.finanzasglobal.operacionescambio.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import pe.finanzasglobal.operacionescambio.domain.model.Moneda;
import pe.finanzasglobal.operacionescambio.domain.model.TipoOperacion;

import java.math.BigDecimal;

public record RegistrarOperacionRequest(
        @NotNull Long clienteId,
        @NotNull TipoOperacion tipo,
        @NotNull Moneda moneda,
        @NotNull @Positive BigDecimal monto) {
}
