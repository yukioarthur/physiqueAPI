package senac.tsi.physique.apikey;

public enum ApiAccessPlan {
    ALUNO(1, 3),
    PROFESSOR(2, 300),
    ADMIN(3, 1000);

    private final int level;
    private final long requestsPerMinute;

    ApiAccessPlan(int level, long requestsPerMinute) {
        this.level = level;
        this.requestsPerMinute = requestsPerMinute;
    }

    public long getRequestsPerMinute() {
        return requestsPerMinute;
    }

    public boolean hasAtLeast(ApiAccessPlan requiredPlan) {
        return this.level >= requiredPlan.level;
    }

    public String getRateLimitPolicy() {
        return requestsPerMinute + ";w=60";
    }
}
