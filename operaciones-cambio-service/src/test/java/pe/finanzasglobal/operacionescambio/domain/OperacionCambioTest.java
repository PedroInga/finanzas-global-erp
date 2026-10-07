package pe.finanzasglobal.operacionescambio.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.finanzasglobal.operacionescambio.domain.exception.ReglaNegocioException;
import pe.finanzasglobal.operacionescambio.domain.model.*;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Pruebas unitarias PURAS del dominio: sin Spring, sin base de datos, ejecutan en milisegundos. */
class OperacionCambioTest {

    private static final TipoCambio USD = new TipoCambio(Moneda.USD, new BigDecimal("3.70"), new BigDecimal("3.75"));
    private static final BigDecimal LIMITE = new BigDecimal("10000");

    @Test
    @DisplayName("COMPRA de 1000 USD: se paga 3.70, comisión 0.30% se descuenta al cliente")
    void compra() {
        OperacionCambio op = OperacionCambio.registrar(1L, TipoOperacion.COMPRA, Moneda.USD,
                new BigDecimal("1000"), USD, LIMITE, "cajero", "MIRAFLORES");

        assertThat(op.getImporteBruto()).isEqualByComparingTo("3700.00");
        assertThat(op.getComision()).isEqualByComparingTo("11.10");
        assertThat(op.getImporteLiquidado()).isEqualByComparingTo("3688.90");
        assertThat(op.getEstado()).isEqualTo(EstadoOperacion.REGISTRADA);
        assertThat(op.getUsuario()).isEqualTo("cajero");
        assertThat(op.getOficina()).isEqualTo("MIRAFLORES");
    }

    @Test
    @DisplayName("VENTA de 1000 USD: se cobra 3.75, comisión 0.30% se suma al cliente")
    void venta() {
        OperacionCambio op = OperacionCambio.registrar(1L, TipoOperacion.VENTA, Moneda.USD,
                new BigDecimal("1000"), USD, LIMITE, "cajero", "MIRAFLORES");

        assertThat(op.getTipoCambioAplicado()).isEqualByComparingTo("3.75");
        assertThat(op.getImporteBruto()).isEqualByComparingTo("3750.00");
        assertThat(op.getComision()).isEqualByComparingTo("11.25");
        assertThat(op.getImporteLiquidado()).isEqualByComparingTo("3761.25");
    }

    @Test
    void rechazaMontoNoPositivo() {
        assertThatThrownBy(() -> OperacionCambio.registrar(1L, TipoOperacion.COMPRA, Moneda.USD,
                BigDecimal.ZERO, USD, LIMITE, "u", "o"))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("mayor a cero");
    }

    @Test
    void rechazaMonedaLocal() {
        assertThatThrownBy(() -> OperacionCambio.registrar(1L, TipoOperacion.COMPRA, Moneda.PEN,
                BigDecimal.TEN, USD, LIMITE, "u", "o"))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("extranjera");
    }

    @Test
    void rechazaMontoQueExcedeLimiteDelCliente() {
        assertThatThrownBy(() -> OperacionCambio.registrar(1L, TipoOperacion.COMPRA, Moneda.USD,
                new BigDecimal("10000.01"), USD, LIMITE, "u", "o"))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("límite");
    }

    @Test
    void rechazaTipoCambioDeOtraMoneda() {
        assertThatThrownBy(() -> OperacionCambio.registrar(1L, TipoOperacion.COMPRA, Moneda.EUR,
                BigDecimal.TEN, USD, LIMITE, "u", "o"))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("no corresponde");
    }

    @Test
    void rechazaDatosIncompletos() {
        assertThatThrownBy(() -> OperacionCambio.registrar(null, TipoOperacion.COMPRA, Moneda.USD,
                BigDecimal.TEN, USD, LIMITE, "u", "o"))
                .isInstanceOf(ReglaNegocioException.class);
    }

    @Test
    void anularCambiaEstadoYRegistraResponsable() {
        OperacionCambio op = OperacionCambio.registrar(1L, TipoOperacion.COMPRA, Moneda.USD,
                BigDecimal.TEN, USD, LIMITE, "cajero", "o");
        op.anular("supervisor");

        assertThat(op.getEstado()).isEqualTo(EstadoOperacion.ANULADA);
        assertThat(op.getUsuarioAnulacion()).isEqualTo("supervisor");
    }

    @Test
    void noSePuedeAnularDosVeces() {
        OperacionCambio op = OperacionCambio.registrar(1L, TipoOperacion.COMPRA, Moneda.USD,
                BigDecimal.TEN, USD, LIMITE, "cajero", "o");
        op.anular("supervisor");

        assertThatThrownBy(() -> op.anular("supervisor"))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("ya se encuentra anulada");
    }
}
