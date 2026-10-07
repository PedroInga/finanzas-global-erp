package pe.finanzasglobal.operacionescambio.application.port.out;

import pe.finanzasglobal.operacionescambio.domain.model.OperacionCambio;

import java.util.Optional;
import java.util.UUID;

/** Puerto de salida: persistencia de operaciones. */
public interface OperacionRepositoryPort {

    OperacionCambio guardar(OperacionCambio operacion);

    Optional<OperacionCambio> buscarPorId(UUID id);
}
