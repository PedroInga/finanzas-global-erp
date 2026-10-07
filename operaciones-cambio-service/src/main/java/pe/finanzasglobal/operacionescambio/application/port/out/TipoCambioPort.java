package pe.finanzasglobal.operacionescambio.application.port.out;

import pe.finanzasglobal.operacionescambio.domain.model.Moneda;
import pe.finanzasglobal.operacionescambio.domain.model.TipoCambio;

import java.util.Optional;

/** Puerto de salida: consulta del tipo de cambio vigente (ms-divisas vía OpenFeign). */
public interface TipoCambioPort {

    Optional<TipoCambio> vigente(Moneda moneda);
}
