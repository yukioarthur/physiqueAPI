package senac.tsi.physique.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ApiFieldError", description = "Erro de validação em um campo específico da requisição")
public class ApiFieldError {

    @Schema(description = "Nome do campo inválido", example = "nome")
    private String field;

    @Schema(description = "Mensagem clara sobre o erro do campo", example = "O nome do treino é obrigatório")
    private String message;

    @Schema(description = "Valor rejeitado, quando não for sensível", example = "")
    private Object rejectedValue;

    public ApiFieldError() {
    }

    public ApiFieldError(String field, String message, Object rejectedValue) {
        this.field = field;
        this.message = message;
        this.rejectedValue = rejectedValue;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }

    public void setRejectedValue(Object rejectedValue) {
        this.rejectedValue = rejectedValue;
    }
}
