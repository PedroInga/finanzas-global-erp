package pe.finanzasglobal.operacionescambio.application.port.in;

import pe.finanzasglobal.operacionescambio.domain.model.OperacionCambio;

import java.util.UUID;

/** Puerto de entrada: consultar una operación registrada. */
public interface ConsultarOperacionUseCase {

    OperacionCambio porId(UUID id);
}
