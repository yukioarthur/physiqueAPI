package senac.tsi.physique.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema(name = "ApiErrorResponse", description = "Formato padrão de erro da Physique API")
public class ApiErrorResponse {

    @Schema(description = "Data e hora do erro", example = "2026-05-25T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Código HTTP", example = "400")
    private int status;

    @Schema(description = "Resumo do erro", example = "Validation failed")
    private String error;

    @Schema(description = "Mensagem clara para o cliente", example = "Existem campos inválidos na requisição")
    private String message;

    @Schema(description = "Path que gerou o erro", example = "/treinos")
    private String path;

    @Schema(description = "Lista de erros por campo")
    private List<ApiFieldError> fieldErrors = new ArrayList<>();

    public ApiErrorResponse() {
    }

    public ApiErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public static ApiErrorResponse of(int status, String error, String message, String path) {
        return new ApiErrorResponse(status, error, message, path);
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<ApiFieldError> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(List<ApiFieldError> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
}
