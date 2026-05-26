package senac.tsi.physique.idempotency;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public final class IdempotencyHashUtil {

    private IdempotencyHashUtil() {
    }

    public static String sha256(String httpMethod, String requestPath, String userIdentifier, byte[] body) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(nullSafe(httpMethod).getBytes(StandardCharsets.UTF_8));
            digest.update((byte) '|');
            digest.update(nullSafe(requestPath).getBytes(StandardCharsets.UTF_8));
            digest.update((byte) '|');
            digest.update(nullSafe(userIdentifier).getBytes(StandardCharsets.UTF_8));
            digest.update((byte) '|');
            if (body != null) {
                digest.update(body);
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 algorithm is not available", exception);
        }
    }

    private static String nullSafe(String value) {
        return value == null ? "" : value;
    }
}
