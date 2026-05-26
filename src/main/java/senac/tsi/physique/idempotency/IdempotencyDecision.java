package senac.tsi.physique.idempotency;

public class IdempotencyDecision {

    private final IdempotencyDecisionType type;
    private final Long recordId;
    private final Integer responseStatus;
    private final String responseBody;
    private final String errorMessage;

    private IdempotencyDecision(IdempotencyDecisionType type, Long recordId, Integer responseStatus, String responseBody, String errorMessage) {
        this.type = type;
        this.recordId = recordId;
        this.responseStatus = responseStatus;
        this.responseBody = responseBody;
        this.errorMessage = errorMessage;
    }

    public static IdempotencyDecision proceed(Long recordId) {
        return new IdempotencyDecision(IdempotencyDecisionType.PROCEED, recordId, null, null, null);
    }

    public static IdempotencyDecision replay(Integer responseStatus, String responseBody) {
        return new IdempotencyDecision(IdempotencyDecisionType.REPLAY, null, responseStatus, responseBody, null);
    }

    public static IdempotencyDecision payloadMismatch() {
        return new IdempotencyDecision(
                IdempotencyDecisionType.PAYLOAD_MISMATCH,
                null,
                422,
                null,
                "Idempotency-Key reused with different request payload"
        );
    }

    public static IdempotencyDecision processing() {
        return new IdempotencyDecision(
                IdempotencyDecisionType.PROCESSING,
                null,
                409,
                null,
                "Request with this Idempotency-Key is still processing"
        );
    }

    public static IdempotencyDecision failed() {
        return new IdempotencyDecision(
                IdempotencyDecisionType.FAILED,
                null,
                409,
                null,
                "Previous request with this Idempotency-Key failed; verify the operation state before retrying with a new key"
        );
    }

    public IdempotencyDecisionType getType() {
        return type;
    }

    public Long getRecordId() {
        return recordId;
    }

    public Integer getResponseStatus() {
        return responseStatus;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
