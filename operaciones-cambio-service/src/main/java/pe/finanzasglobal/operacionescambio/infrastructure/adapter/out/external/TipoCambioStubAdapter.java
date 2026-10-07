package pe.finanzasglobal.operacionescambio.infrastructure.adapter.out.external;

import org.springframework.stereotype.Component;
import pe.finanzasglobal.operacionescambio.application.port.out.TipoCambioPort;
import pe.finanzasglobal.operacionescambio.domain.model.Moneda;
import pe.finanzasglobal.operacionescambio.domain.model.TipoCambio;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

/**
 * Adaptador de salida (simulado). En el ERP real se reemplaza por un FeignTipoCambioAdapter que invoca
 * a ms-divisas con @FeignClient + Resilience4j; el núcleo no cambia porque solo conoce TipoCambioPort.
 */
@Component
public class TipoCambioStubAdapter implements TipoCambioPort {

    private static final Map<Moneda, TipoCambio> TABLA = Map.of(
            Moneda.USD, new TipoCambio(Moneda.USD, new BigDecimal("3.700"), new BigDecimal("3.750")),
            Moneda.EUR, new TipoCambio(Moneda.EUR, new BigDecimal("4.000"), new BigDecimal("4.100")));

    @Override
    public Optional<TipoCambio> vigente(Moneda moneda) {
        return Optional.ofNullable(TABLA.get(moneda));
    }
}
