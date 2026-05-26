package senac.tsi.physique.versioning;

import java.util.Set;

public final class ApiVersionConstants {

    public static final String HEADER_NAME = "X-API-Version";
    public static final String V1 = "1";
    public static final String V2 = "2";
    public static final Set<String> SUPPORTED_VERSIONS = Set.of(V1, V2);

    private ApiVersionConstants() {
    }
}
