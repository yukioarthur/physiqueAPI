package senac.tsi.physique.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import senac.tsi.physique.apikey.ApiAccessPlan;
import senac.tsi.physique.apikey.ApiKeyStatus;
import senac.tsi.physique.entities.ApiKey;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    Optional<ApiKey> findByKeyHash(String keyHash);
    List<ApiKey> findByUsuarioId(Long usuarioId);
    List<ApiKey> findByStatus(ApiKeyStatus status);
    boolean existsByAccessPlanAndStatus(ApiAccessPlan accessPlan, ApiKeyStatus status);
}
