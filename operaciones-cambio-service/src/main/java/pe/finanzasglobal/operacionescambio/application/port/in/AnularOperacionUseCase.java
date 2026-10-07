package pe.finanzasglobal.operacionescambio.application.port.in;

import pe.finanzasglobal.operacionescambio.domain.model.OperacionCambio;

import java.util.UUID;

/** Puerto de entrada: anular una operación (acción restringida a supervisores). */
public interface AnularOperacionUseCase {

    OperacionCambio anular(UUID id, String usuario);
}
