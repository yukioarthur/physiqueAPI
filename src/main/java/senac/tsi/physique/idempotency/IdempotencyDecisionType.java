package senac.tsi.physique.idempotency;

public enum IdempotencyDecisionType {
    PROCEED,
    REPLAY,
    PAYLOAD_MISMATCH,
    PROCESSING,
    FAILED
}
