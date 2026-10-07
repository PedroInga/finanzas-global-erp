package pe.finanzasglobal.operacionescambio.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;
import pe.finanzasglobal.operacionescambio.domain.model.EstadoOperacion;
import pe.finanzasglobal.operacionescambio.domain.model.Moneda;
import pe.finanzasglobal.operacionescambio.domain.model.TipoOperacion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/** Entidad JPA: detalle de persistencia, separada del modelo de dominio. */
@Entity
@Table(name = "operacion_cambio")
public class OperacionEntity {

    @Id
    private UUID id;
    @Column(nullable = false)
    private Long clienteId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoOperacion tipo;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private Moneda moneda;
    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal monto;
    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal tipoCambioAplicado;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal importeBruto;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal comision;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal importeLiquidado;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private EstadoOperacion estado;
    @Column(nullable = false)
    private String usuario;
    @Column(nullable = false)
    private String oficina;
    @Column(nullable = false)
    private LocalDateTime fechaRegistro;
    private String usuarioAnulacion;

    protected OperacionEntity() {
    }

    public OperacionEntity(UUID id, Long clienteId, TipoOperacion tipo, Moneda moneda, BigDecimal monto,
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
