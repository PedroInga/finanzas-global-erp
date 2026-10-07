package pe.finanzasglobal.operacionescambio.domain.model;

/** Monedas con las que opera la casa de cambio. PEN es la moneda local. */
public enum Moneda {
    PEN, USD, EUR;

    public boolean esExtranjera() {
        return this != PEN;
    }
}
