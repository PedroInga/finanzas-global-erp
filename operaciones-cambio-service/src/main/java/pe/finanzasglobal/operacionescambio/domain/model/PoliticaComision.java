package pe.finanzasglobal.operacionescambio.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Política de comisión de la casa de cambio: 0.30% del importe bruto en PEN, redondeo HALF_UP a 2 decimales. */
public final class PoliticaComision {

    public static final BigDecimal TASA = new BigDecimal("0.003");

    private PoliticaComision() {
    }

    public static BigDecimal calcular(BigDecimal importeBrutoPen) {
        return importeBrutoPen.multiply(TASA).setScale(2, RoundingMode.HALF_UP);
    }
}
