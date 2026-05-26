package senac.tsi.physique.apikey;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final Map<Long, Bucket> bucketsByApiKeyId = new ConcurrentHashMap<>();

    public RateLimitResult consume(Long apiKeyId, ApiAccessPlan accessPlan) {
        Bucket bucket = bucketsByApiKeyId.computeIfAbsent(apiKeyId, ignored -> createBucket(accessPlan));
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        long retryAfterSeconds = probe.isConsumed()
                ? 0
                : Math.max(1, Duration.ofNanos(probe.getNanosToWaitForRefill()).toSeconds());

        return new RateLimitResult(
                probe.isConsumed(),
                probe.getRemainingTokens(),
                retryAfterSeconds
        );
    }

    private Bucket createBucket(ApiAccessPlan accessPlan) {
        long requestsPerMinute = accessPlan.getRequestsPerMinute();
        Refill refill = Refill.greedy(requestsPerMinute, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(requestsPerMinute, refill);
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
