package pe.finanzasglobal.operacionescambio.infrastructure.adapter.out.external;

import org.springframework.stereotype.Component;
import pe.finanzasglobal.operacionescambio.application.port.out.ClientePort;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Adaptador de salida (simulado) de ms-clientes. Reglas de la demo:
 * id 404 = no existe, id 999 = cliente inactivo, cualquier otro = activo con límite de 10,000.
 * En producción se reemplaza por un cliente OpenFeign.
 */
@Component
public class ClienteStubAdapter implements ClientePort {

    @Override
    public Optional<ClienteInfo> buscar(Long clienteId) {
        if (clienteId == 404L) {
            return Optional.empty();
        }
        return Optional.of(new ClienteInfo(clienteId, clienteId != 999L, new BigDecimal("10000")));
    }
}
