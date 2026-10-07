package pe.finanzasglobal.operacionescambio.infrastructure.adapter.out.external;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pe.finanzasglobal.operacionescambio.application.port.out.EventPublisherPort;
import pe.finanzasglobal.operacionescambio.domain.event.OperacionRegistradaEvent;

/**
 * Adaptador de salida (simulado) del broker. En el ERP real se reemplaza por un RabbitEventPublisherAdapter
 * que usa RabbitTemplate y publica en el exchange "finanzas.eventos" con routing key "operacion.registrada".
 */
@Component
public class LoggingEventPublisherAdapter implements EventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(LoggingEventPublisherAdapter.class);

    @Override
    public void publicar(OperacionRegistradaEvent evento) {
        log.info("EVENTO operacion.registrada eventId={} operacionId={} usuario={} oficina={} importe={}",
                evento.eventId(), evento.operacionId(), evento.usuario(), evento.oficina(),
                evento.importeLiquidado());
    }
}
