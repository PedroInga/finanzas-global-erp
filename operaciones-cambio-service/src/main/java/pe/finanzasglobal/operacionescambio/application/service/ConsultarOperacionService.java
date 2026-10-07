package pe.finanzasglobal.operacionescambio.application.service;

import pe.finanzasglobal.operacionescambio.application.exception.RecursoNoEncontradoException;
import pe.finanzasglobal.operacionescambio.application.port.in.ConsultarOperacionUseCase;
import pe.finanzasglobal.operacionescambio.application.port.out.OperacionRepositoryPort;
import pe.finanzasglobal.operacionescambio.domain.model.OperacionCambio;

import java.util.UUID;

public class ConsultarOperacionService implements ConsultarOperacionUseCase {

    private final OperacionRepositoryPort repositorio;

    public ConsultarOperacionService(OperacionRepositoryPort repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public OperacionCambio porId(UUID id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Operación no encontrada: " + id));
    }
}
