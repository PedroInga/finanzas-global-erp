package pe.finanzasglobal.operacionescambio.domain.model;

import pe.finanzasglobal.operacionescambio.domain.exception.ReglaNegocioException;

import java.math.BigDecimal;

/** Tipo de cambio vigente de una moneda extranjera frente al PEN (valor inmutable). */
public record TipoCambio(Moneda moneda, BigDecimal compra, BigDecimal venta) {

    public TipoCambio {
        if (moneda == null || !moneda.esExtranjera()) {
            throw new ReglaNegocioException("El tipo de cambio debe corresponder a una moneda extranjera");
        }
        if (compra == null || venta == null
                || compra.signum() <= 0 || venta.signum() <= 0) {
            throw new ReglaNegocioException("Los precios de compra y venta deben ser positivos");
        }
        if (venta.compareTo(compra) < 0) {
            throw new ReglaNegocioException("El precio de venta no puede ser menor al de compra");
        }
    }
}
