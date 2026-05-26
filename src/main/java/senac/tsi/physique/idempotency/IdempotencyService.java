package senac.tsi.physique.idempotency;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import senac.tsi.physique.entities.IdempotencyRecord;
import senac.tsi.physique.repositories.IdempotencyRecordRepository;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class IdempotencyService {

    private static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";
    private static final String LEGACY_IDEMPOTENCY_KEY_HEADER = "X-Idempotency-Key";
    private static final int EXPIRATION_HOURS = 24;

    private final IdempotencyRecordRepository idempotencyRecordRepository;
    private final ConcurrentHashMap<String, ReentrantLock> localLocks = new ConcurrentHashMap<>();

    public IdempotencyService(IdempotencyRecordRepository idempotencyRecordRepository) {
        this.idempotencyRecordRepository = idempotencyRecordRepository;
    }

    public String extractIdempotencyKey(HttpServletRequest request) {
        String primaryHeader = request.getHeader(IDEMPOTENCY_KEY_HEADER);
        if (StringUtils.hasText(primaryHeader)) {
            return primaryHeader.trim();
        }

        String legacyHeader = request.getHeader(LEGACY_IDEMPOTENCY_KEY_HEADER);
        if (StringUtils.hasText(legacyHeader)) {
            return legacyHeader.trim();
        }

        return null;
    }

    @Transactional
    public IdempotencyDecision evaluate(String idempotencyKey,
                                        String httpMethod,
                                        String requestPath,
                                        String userIdentifier,
                                        String requestHash) {
        String scopeKey = buildScopeKey(idempotencyKey, httpMethod, requestPath, userIdentifier);
        ReentrantLock lock = localLocks.computeIfAbsent(scopeKey, ignored -> new ReentrantLock());
        lock.lock();
        try {
            Optional<IdempotencyRecord> existingRecord = idempotencyRecordRepository
                    .findScopedRecordForUpdate(idempotencyKey, httpMethod, requestPath, userIdentifier);

            if (existingRecord.isEmpty()) {
                IdempotencyRecord record = new IdempotencyRecord();
                record.setIdempotencyKey(idempotencyKey);
                record.setHttpMethod(httpMethod);
                record.setRequestPath(requestPath);
                record.setUserIdentifier(userIdentifier);
                record.setRequestHash(requestHash);
                record.setStatus(IdempotencyStatus.PROCESSING);
                record.setExpiresAt(LocalDateTime.now().plusHours(EXPIRATION_HOURS));
                record = idempotencyRecordRepository.saveAndFlush(record);
                return IdempotencyDecision.proceed(record.getId());
            }

            IdempotencyRecord record = existingRecord.get();
            if (!record.getRequestHash().equals(requestHash)) {
                return IdempotencyDecision.payloadMismatch();
            }

            if (record.getStatus() == IdempotencyStatus.PROCESSING) {
                return IdempotencyDecision.processing();
            }

            if (record.getStatus() == IdempotencyStatus.FAILED) {
                return IdempotencyDecision.failed();
            }

            return IdempotencyDecision.replay(record.getResponseStatus(), record.getResponseBody());
        } finally {
            lock.unlock();
        }
    }

    @Transactional
    public void markCompleted(Long recordId, int responseStatus, String responseBody) {
        IdempotencyRecord record = idempotencyRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalStateException("Idempotency record not found: " + recordId));

        record.setResponseStatus(responseStatus);
        record.setResponseBody(responseBody);
        record.setStatus(IdempotencyStatus.COMPLETED);
        idempotencyRecordRepository.save(record);
    }

    @Transactional
    public void markFailed(Long recordId) {
        IdempotencyRecord record = idempotencyRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalStateException("Idempotency record not found: " + recordId));

        record.setStatus(IdempotencyStatus.FAILED);
        idempotencyRecordRepository.save(record);
    }

    private String buildScopeKey(String idempotencyKey, String httpMethod, String requestPath, String userIdentifier) {
        return idempotencyKey + "|" + httpMethod + "|" + requestPath + "|" + userIdentifier;
    }
}
