package pe.finanzasglobal.operacionescambio.infrastructure.adapter.in.web.dto;

import pe.finanzasglobal.operacionescambio.domain.model.EstadoOperacion;
import pe.finanzasglobal.operacionescambio.domain.model.Moneda;
import pe.finanzasglobal.operacionescambio.domain.model.OperacionCambio;
import pe.finanzasglobal.operacionescambio.domain.model.TipoOperacion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OperacionResponse(
        UUID id,
        Long clienteId,
        TipoOperacion tipo,
        Moneda moneda,
        BigDecimal monto,
        BigDecimal tipoCambioAplicado,
        BigDecimal importeBruto,
        BigDecimal comision,
        BigDecimal importeLiquidado,
        EstadoOperacion estado,
        String usuario,
        String oficina,
        LocalDateTime fechaRegistro) {

    public static OperacionResponse desde(OperacionCambio o) {
        return new OperacionResponse(o.getId(), o.getClienteId(), o.getTipo(), o.getMoneda(), o.getMonto(),
                o.getTipoCambioAplicado(), o.getImporteBruto(), o.getComision(), o.getImporteLiquidado(),
                o.getEstado(), o.getUsuario(), o.getOficina(), o.getFechaRegistro());
    }
}
