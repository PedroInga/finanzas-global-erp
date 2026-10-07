package pe.finanzasglobal.operacionescambio.domain.event;

import pe.finanzasglobal.operacionescambio.domain.model.Moneda;
import pe.finanzasglobal.operacionescambio.domain.model.TipoOperacion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/** Evento de dominio que se publica (RabbitMQ) cuando una operación fue registrada. */
public record OperacionRegistradaEvent(
        UUID eventId,
        UUID operacionId,
        Long clienteId,
        TipoOperacion tipo,
        Moneda moneda,
        BigDecimal monto,
        BigDecimal importeLiquidado,
        String usuario,
        String oficina,
        LocalDateTime fecha) {
}
