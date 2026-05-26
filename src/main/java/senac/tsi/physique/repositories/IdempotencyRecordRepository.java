package senac.tsi.physique.repositories;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import senac.tsi.physique.entities.IdempotencyRecord;

import java.util.Optional;

public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecord, Long> {

    Optional<IdempotencyRecord> findByIdempotencyKeyAndHttpMethodAndRequestPathAndUserIdentifier(
            String idempotencyKey,
            String httpMethod,
            String requestPath,
            String userIdentifier
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select record from IdempotencyRecord record
            where record.idempotencyKey = :idempotencyKey
              and record.httpMethod = :httpMethod
              and record.requestPath = :requestPath
              and record.userIdentifier = :userIdentifier
            """)
    Optional<IdempotencyRecord> findScopedRecordForUpdate(
            @Param("idempotencyKey") String idempotencyKey,
            @Param("httpMethod") String httpMethod,
            @Param("requestPath") String requestPath,
            @Param("userIdentifier") String userIdentifier
    );
}
