package pe.finanzasglobal.operacionescambio.application.service;

import pe.finanzasglobal.operacionescambio.application.exception.RecursoNoEncontradoException;
import pe.finanzasglobal.operacionescambio.application.port.in.RegistrarOperacionUseCase;
import pe.finanzasglobal.operacionescambio.application.port.out.ClientePort;
import pe.finanzasglobal.operacionescambio.application.port.out.EventPublisherPort;
import pe.finanzasglobal.operacionescambio.application.port.out.OperacionRepositoryPort;
import pe.finanzasglobal.operacionescambio.application.port.out.TipoCambioPort;
import pe.finanzasglobal.operacionescambio.domain.event.OperacionRegistradaEvent;
import pe.finanzasglobal.operacionescambio.domain.exception.ReglaNegocioException;
import pe.finanzasglobal.operacionescambio.domain.model.OperacionCambio;
import pe.finanzasglobal.operacionescambio.domain.model.TipoCambio;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Caso de uso: orquesta el flujo (cliente -> tipo de cambio -> dominio -> persistencia -> evento).
 * No contiene reglas de cálculo (están en el dominio) ni detalles tecnológicos (están en los adaptadores).
 */
public class RegistrarOperacionService implements RegistrarOperacionUseCase {

    private final OperacionRepositoryPort repositorio;
    private final TipoCambioPort tipoCambioPort;
    private final ClientePort clientePort;
    private final EventPublisherPort eventos;

    public RegistrarOperacionService(OperacionRepositoryPort repositorio, TipoCambioPort tipoCambioPort,
                                     ClientePort clientePort, EventPublisherPort eventos) {
        this.repositorio = repositorio;
        this.tipoCambioPort = tipoCambioPort;
        this.clientePort = clientePort;
        this.eventos = eventos;
    }

    @Override
    public OperacionCambio registrar(RegistrarOperacionCommand c) {
        ClientePort.ClienteInfo cliente = clientePort.buscar(c.clienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado: " + c.clienteId()));
        if (!cliente.activo()) {
            throw new ReglaNegocioException("El cliente no se encuentra activo");
        }

        TipoCambio tipoCambio = tipoCambioPort.vigente(c.moneda())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe tipo de cambio vigente para " + c.moneda()));

        OperacionCambio operacion = OperacionCambio.registrar(c.clienteId(), c.tipo(), c.moneda(), c.monto(),
                tipoCambio, cliente.limiteOperacion(), c.usuario(), c.oficina());

        OperacionCambio guardada = repositorio.guardar(operacion);

        eventos.publicar(new OperacionRegistradaEvent(UUID.randomUUID(), guardada.getId(), guardada.getClienteId(),
                guardada.getTipo(), guardada.getMoneda(), guardada.getMonto(), guardada.getImporteLiquidado(),
                guardada.getUsuario(), guardada.getOficina(), LocalDateTime.now()));

        return guardada;
    }
}
