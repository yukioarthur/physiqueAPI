package senac.tsi.physique.apikey;

public class RateLimitResult {

    private final boolean allowed;
    private final long remainingTokens;
    private final long retryAfterSeconds;

    public RateLimitResult(boolean allowed, long remainingTokens, long retryAfterSeconds) {
        this.allowed = allowed;
        this.remainingTokens = remainingTokens;
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public long getRemainingTokens() {
        return remainingTokens;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
