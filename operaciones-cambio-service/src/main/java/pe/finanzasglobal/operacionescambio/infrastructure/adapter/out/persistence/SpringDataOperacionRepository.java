package pe.finanzasglobal.operacionescambio.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataOperacionRepository extends JpaRepository<OperacionEntity, UUID> {
}
