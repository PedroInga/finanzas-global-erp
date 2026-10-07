package pe.finanzasglobal.operacionescambio.domain.model;

import pe.finanzasglobal.operacionescambio.domain.exception.ReglaNegocioException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Agregado raíz del dominio. Contiene las reglas de negocio de una operación de cambio de divisas
 * y NO depende de ningún framework (ni Spring ni JPA).
 */
public class OperacionCambio {

    private final UUID id;
    private final Long clienteId;
    private final TipoOperacion tipo;
    private final Moneda moneda;
    private final BigDecimal monto;
    private final BigDecimal tipoCambioAplicado;
    private final BigDecimal importeBruto;
    private final BigDecimal comision;
    private final BigDecimal importeLiquidado;
    private EstadoOperacion estado;
    private final String usuario;
    private final String oficina;
    private final LocalDateTime fechaRegistro;
    private String usuarioAnulacion;

    private OperacionCambio(UUID id, Long clienteId, TipoOperacion tipo, Moneda moneda, BigDecimal monto,
                            BigDecimal tipoCambioAplicado, BigDecimal importeBruto, BigDecimal comision,
                            BigDecimal importeLiquidado, EstadoOperacion estado, String usuario, String oficina,
                            LocalDateTime fechaRegistro, String usuarioAnulacion) {
        this.id = id;
        this.clienteId = clienteId;
        this.tipo = tipo;
        this.moneda = moneda;
        this.monto = monto;
        this.tipoCambioAplicado = tipoCambioAplicado;
        this.importeBruto = importeBruto;
        this.comision = comision;
        this.importeLiquidado = importeLiquidado;
        this.estado = estado;
        this.usuario = usuario;
        this.oficina = oficina;
        this.fechaRegistro = fechaRegistro;
        this.usuarioAnulacion = usuarioAnulacion;
    }

    /**
     * Registra una nueva operación aplicando las reglas del negocio:
     * moneda extranjera, monto positivo, límite del cliente, tipo de cambio coherente y cálculo de comisión.
     * COMPRA: la casa paga al cliente (importe bruto - comisión).
     * VENTA: el cliente paga a la casa (importe bruto + comisión).
     */
    public static OperacionCambio registrar(Long clienteId, TipoOperacion tipo, Moneda moneda, BigDecimal monto,
                                            TipoCambio tipoCambio, BigDecimal limiteOperacion,
                                            String usuario, String oficina) {
        if (clienteId == null || tipo == null || moneda == null || monto == null || tipoCambio == null) {
            throw new ReglaNegocioException("Datos obligatorios incompletos para registrar la operación");
        }
        if (!moneda.esExtranjera()) {
            throw new ReglaNegocioException("La moneda a operar debe ser extranjera (USD, EUR)");
        }
        if (monto.signum() <= 0) {
            throw new ReglaNegocioException("El monto debe ser mayor a cero");
        }
        if (tipoCambio.moneda() != moneda) {
            throw new ReglaNegocioException("El tipo de cambio no corresponde a la moneda de la operación");
        }
        if (limiteOperacion != null && monto.compareTo(limiteOperacion) > 0) {
            throw new ReglaNegocioException("El monto excede el límite de operación del cliente");
        }

        BigDecimal tc = tipo == TipoOperacion.COMPRA ? tipoCambio.compra() : tipoCambio.venta();
        BigDecimal bruto = monto.multiply(tc).setScale(2, RoundingMode.HALF_UP);
        BigDecimal comision = PoliticaComision.calcular(bruto);
        BigDecimal liquidado = tipo == TipoOperacion.COMPRA ? bruto.subtract(comision) : bruto.add(comision);

        return new OperacionCambio(UUID.randomUUID(), clienteId, tipo, moneda, monto, tc, bruto, comision,
                liquidado, EstadoOperacion.REGISTRADA, usuario, oficina, LocalDateTime.now(), null);
    }

    /** Reconstruye una operación ya persistida (usado por el adaptador de persistencia). */
    public static OperacionCambio reconstruir(UUID id, Long clienteId, TipoOperacion tipo, Moneda moneda,
                                              BigDecimal monto, BigDecimal tipoCambioAplicado,
                                              BigDecimal importeBruto, BigDecimal comision,
                                              BigDecimal importeLiquidado, EstadoOperacion estado,
                                              String usuario, String oficina, LocalDateTime fechaRegistro,
                                              String usuarioAnulacion) {
        return new OperacionCambio(id, clienteId, tipo, moneda, monto, tipoCambioAplicado, importeBruto, comision,
                importeLiquidado, estado, usuario, oficina, fechaRegistro, usuarioAnulacion);
    }

    public void anular(String usuarioResponsable) {
        if (estado == EstadoOperacion.ANULADA) {
            throw new ReglaNegocioException("La operación ya se encuentra anulada");
        }
        this.estado = EstadoOperacion.ANULADA;
        this.usuarioAnulacion = usuarioResponsable;
    }

    public UUID getId() { return id; }
    public Long getClienteId() { return clienteId; }
    public TipoOperacion getTipo() { return tipo; }
    public Moneda getMoneda() { return moneda; }
    public BigDecimal getMonto() { return monto; }
    public BigDecimal getTipoCambioAplicado() { return tipoCambioAplicado; }
    public BigDecimal getImporteBruto() { return importeBruto; }
    public BigDecimal getComision() { return comision; }
    public BigDecimal getImporteLiquidado() { return importeLiquidado; }
    public EstadoOperacion getEstado() { return estado; }
    public String getUsuario() { return usuario; }
    public String getOficina() { return oficina; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public String getUsuarioAnulacion() { return usuarioAnulacion; }
}
