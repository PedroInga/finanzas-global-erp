package pe.finanzasglobal.operacionescambio.domain;

import org.junit.jupiter.api.Test;
import pe.finanzasglobal.operacionescambio.domain.exception.ReglaNegocioException;
import pe.finanzasglobal.operacionescambio.domain.model.Moneda;
import pe.finanzasglobal.operacionescambio.domain.model.PoliticaComision;
import pe.finanzasglobal.operacionescambio.domain.model.TipoCambio;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TipoCambioTest {

    @Test
    void ventaNoPuedeSerMenorQueCompra() {
        assertThatThrownBy(() -> new TipoCambio(Moneda.USD, new BigDecimal("3.80"), new BigDecimal("3.70")))
                .isInstanceOf(ReglaNegocioException.class);
    }

    @Test
    void rechazaPreciosNoPositivos() {
        assertThatThrownBy(() -> new TipoCambio(Moneda.USD, BigDecimal.ZERO, new BigDecimal("3.70")))
                .isInstanceOf(ReglaNegocioException.class);
    }

    @Test
    void rechazaMonedaLocal() {
        assertThatThrownBy(() -> new TipoCambio(Moneda.PEN, BigDecimal.ONE, BigDecimal.ONE))
                .isInstanceOf(ReglaNegocioException.class);
    }

    @Test
    void comisionSeRedondeaHalfUpADosDecimales() {
        // 0.30% de 1.50 = 0.0045 -> 0.00 ; 0.30% de 1.70 = 0.0051 -> 0.01
        assertThat(PoliticaComision.calcular(new BigDecimal("1.50"))).isEqualByComparingTo("0.00");
        assertThat(PoliticaComision.calcular(new BigDecimal("1.70"))).isEqualByComparingTo("0.01");
    }
}
