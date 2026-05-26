package senac.tsi.physique.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import senac.tsi.physique.dto.error.ApiErrorResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class ApiErrorWriter {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().findAndRegisterModules();

    private ApiErrorWriter() {
    }

    public static void write(HttpServletRequest request,
                             HttpServletResponse response,
                             int status,
                             String error,
                             String message) throws IOException {
        if (response.isCommitted()) {
            return;
        }
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        ApiErrorResponse body = ApiErrorResponse.of(
                status,
                error,
                message,
                request == null ? null : request.getRequestURI()
        );
        response.getWriter().write(OBJECT_MAPPER.writeValueAsString(body));
    }
}
