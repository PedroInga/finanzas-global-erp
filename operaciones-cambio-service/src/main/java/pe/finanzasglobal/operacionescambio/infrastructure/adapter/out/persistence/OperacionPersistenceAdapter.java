package pe.finanzasglobal.operacionescambio.infrastructure.adapter.out.persistence;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pe.finanzasglobal.operacionescambio.application.port.out.OperacionRepositoryPort;
import pe.finanzasglobal.operacionescambio.domain.model.OperacionCambio;

import java.util.Optional;
import java.util.UUID;

/** Adaptador de salida: implementa el puerto de repositorio usando Spring Data JPA. */
@Component
public class OperacionPersistenceAdapter implements OperacionRepositoryPort {

    private final SpringDataOperacionRepository jpa;

    public OperacionPersistenceAdapter(SpringDataOperacionRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional
    public OperacionCambio guardar(OperacionCambio o) {
        return aDominio(jpa.save(aEntidad(o)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OperacionCambio> buscarPorId(UUID id) {
        return jpa.findById(id).map(this::aDominio);
    }

    private OperacionEntity aEntidad(OperacionCambio o) {
        return new OperacionEntity(o.getId(), o.getClienteId(), o.getTipo(), o.getMoneda(), o.getMonto(),
                o.getTipoCambioAplicado(), o.getImporteBruto(), o.getComision(), o.getImporteLiquidado(),
                o.getEstado(), o.getUsuario(), o.getOficina(), o.getFechaRegistro(), o.getUsuarioAnulacion());
    }

    private OperacionCambio aDominio(OperacionEntity e) {
        return OperacionCambio.reconstruir(e.getId(), e.getClienteId(), e.getTipo(), e.getMoneda(), e.getMonto(),
                e.getTipoCambioAplicado(), e.getImporteBruto(), e.getComision(), e.getImporteLiquidado(),
                e.getEstado(), e.getUsuario(), e.getOficina(), e.getFechaRegistro(), e.getUsuarioAnulacion());
    }
}
