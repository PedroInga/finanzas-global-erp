package pe.finanzasglobal.operacionescambio.application.port.in;

import pe.finanzasglobal.operacionescambio.domain.model.Moneda;
import pe.finanzasglobal.operacionescambio.domain.model.OperacionCambio;
import pe.finanzasglobal.operacionescambio.domain.model.TipoOperacion;

import java.math.BigDecimal;

/** Puerto de entrada: registrar una operación de compra/venta de divisas. */
public interface RegistrarOperacionUseCase {

    OperacionCambio registrar(RegistrarOperacionCommand command);

    record RegistrarOperacionCommand(Long clienteId, TipoOperacion tipo, Moneda moneda, BigDecimal monto,
                                     String usuario, String oficina) {
    }
}
