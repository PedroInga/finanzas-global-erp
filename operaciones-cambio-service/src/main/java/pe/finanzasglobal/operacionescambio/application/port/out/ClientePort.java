package pe.finanzasglobal.operacionescambio.application.port.out;

import java.math.BigDecimal;
import java.util.Optional;

/** Puerto de salida: consulta de clientes (ms-clientes vía OpenFeign). */
public interface ClientePort {

    Optional<ClienteInfo> buscar(Long clienteId);

    record ClienteInfo(Long id, boolean activo, BigDecimal limiteOperacion) {
    }
}
