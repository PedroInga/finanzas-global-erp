package pe.finanzasglobal.operacionescambio.application.service;

import pe.finanzasglobal.operacionescambio.application.exception.RecursoNoEncontradoException;
import pe.finanzasglobal.operacionescambio.application.port.in.AnularOperacionUseCase;
import pe.finanzasglobal.operacionescambio.application.port.out.OperacionRepositoryPort;
import pe.finanzasglobal.operacionescambio.domain.model.OperacionCambio;

import java.util.UUID;

public class AnularOperacionService implements AnularOperacionUseCase {

    private final OperacionRepositoryPort repositorio;

    public AnularOperacionService(OperacionRepositoryPort repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public OperacionCambio anular(UUID id, String usuario) {
        OperacionCambio operacion = repositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Operación no encontrada: " + id));
        operacion.anular(usuario);
        return repositorio.guardar(operacion);
    }
}
