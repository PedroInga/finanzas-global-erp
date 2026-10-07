package pe.finanzasglobal.operacionescambio.application.port.out;

import pe.finanzasglobal.operacionescambio.domain.event.OperacionRegistradaEvent;

/** Puerto de salida: publicación de eventos de dominio (RabbitMQ). */
public interface EventPublisherPort {

    void publicar(OperacionRegistradaEvent evento);
}
