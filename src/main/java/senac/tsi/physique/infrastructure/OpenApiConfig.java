package senac.tsi.physique.infrastructure;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import senac.tsi.physique.apikey.RequireApiKey;
import senac.tsi.physique.idempotency.RequireIdempotency;
import senac.tsi.physique.versioning.ApiVersionConstants;

@Configuration
@SecurityScheme(
        name = "ApiKeyAuth",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        paramName = "X-API-Key"
)
@OpenAPIDefinition(
        info = @Info(
                title = "Physique API",
                version = "1.2.0",
                description = """
                        API REST para gestão de treino, exercícios e acompanhamento de performance física.
                        Permite cadastrar usuários, grupos musculares, músculos, exercícios e treinos,
                        além de registrar resultados de sessões de treino.

                        Autenticação:
                        - endpoints protegidos exigem o header X-API-Key.
                        - a chave de API pertence a um plano de acesso.

                        Planos e rate limit:
                        - ALUNO: 60 requisições por minuto.
                        - PROFESSOR: 300 requisições por minuto.
                        - ADMIN: 1000 requisições por minuto.

                        Headers retornados em endpoints protegidos:
                        - X-Rate-Limit-Plan
                        - X-Rate-Limit-Remaining
                        - X-Rate-Limit-Retry-After-Seconds, quando o limite for excedido
                        - RateLimit-Policy
                        - RateLimit

                        Respostas possíveis em endpoints protegidos:
                        - 401: chave ausente, inválida, expirada ou revogada.
                        - 403: chave válida, mas plano insuficiente.
                        - 429: limite de requisições excedido.

                        Versionamento:
                        - endpoints versionados usam o header X-API-Version.
                        - GET /treinos/{id} possui versões 1 e 2 no mesmo path.
                        - ausência de X-API-Version mantém compatibilidade e usa V1.
                        - versões não suportadas retornam 400 com mensagem clara.

                        Validação e erros:
                        - DTOs de entrada usam Bean Validation.
                        - erros seguem o formato ApiErrorResponse com timestamp, status, error, message, path e fieldErrors.
                        - JSON inválido, parâmetros inválidos, recursos inexistentes e conflitos retornam códigos HTTP apropriados.

                        CORS:
                        - a API aceita requisições cross-origin apenas das origens configuradas.
                        - origens de desenvolvimento permitidas: http://localhost:3000, http://localhost:5173, http://localhost:4200, http://127.0.0.1:3000, http://127.0.0.1:5173 e http://127.0.0.1:4200.
                        - métodos permitidos: GET, POST, PUT, PATCH, DELETE e OPTIONS.
                        - headers permitidos: Content-Type, Accept, Authorization, X-API-Key, Idempotency-Key e X-Idempotency-Key.
                        - headers expostos ao frontend: X-Rate-Limit-Plan, X-Rate-Limit-Remaining, X-Rate-Limit-Retry-After-Seconds, RateLimit, RateLimit-Policy e Location.
                        - preflight OPTIONS é liberado sem X-API-Key, sem Idempotency-Key e sem consumir rate limit.

                        Regras automáticas:
                        - ao registrar um resultado de treino, a API calcula o peso recomendado com base no peso anterior
                        - ao registrar uma série calculada, a API calcula 1RM estimado, carga sugerida da próxima série e repetições-alvo

                        Fluxo recomendado de uso:
                        1. cadastrar grupos musculares
                        2. cadastrar músculos
                        3. cadastrar exercícios
                        4. montar treinos
                        5. cadastrar usuários
                        6. registrar resultados de treino e consultar progressões

                        Idempotência:
                        - POSTs críticos exigem Idempotency-Key.
                        - X-Idempotency-Key é aceito como fallback legado.
                        - retries com o mesmo payload retornam a resposta salva.

                        Headers importantes:
                        - X-API-Key: autenticação por chave de API.
                        - X-API-Version: versionamento por cabeçalho.
                        - Idempotency-Key: idempotência em POSTs críticos.
                        - X-Idempotency-Key: fallback legado de idempotência.

                        Convenções:
                        - endpoints de listagem aceitam paginação com page, size e sort
                        - filtros opcionais são enviados via query string
                        - POST cria recurso e retorna 201
                        - PUT atualiza recurso existente e retorna 200
                        - DELETE remove recurso e retorna 204
                        - ids inexistentes retornam 404
                        """,
                contact = @Contact(
                        name = "Arthur Yukio",
                        email = "arthuryukio@gmail.com"
                ),
                license = @License(
                        name = "Licença MIT",
                        url = "https://opensource.org/licenses/MIT"
                )
        )
)
public class OpenApiConfig {

    @Bean
    public OperationCustomizer apiKeyOperationCustomizer() {
        return (Operation operation, org.springframework.web.method.HandlerMethod handlerMethod) -> {
            RequireApiKey requireApiKey = handlerMethod.getMethodAnnotation(RequireApiKey.class);
            if (requireApiKey == null) {
                requireApiKey = handlerMethod.getBeanType().getAnnotation(RequireApiKey.class);
            }

            if (requireApiKey != null) {
                operation.addSecurityItem(new SecurityRequirement().addList("ApiKeyAuth"));
                operation.addParametersItem(new Parameter()
                        .name("X-API-Key")
                        .in("header")
                        .required(true)
                        .description("Chave de API obrigatória. Plano mínimo: " + requireApiKey.minPlan().name() + ". Rate limit aplicado conforme o plano da chave.")
                        .schema(new StringSchema()));

                String currentDescription = operation.getDescription() == null ? "" : operation.getDescription() + "\n\n";
                operation.setDescription(currentDescription + "Requer X-API-Key. Plano mínimo: "
                        + requireApiKey.minPlan().name()
                        + ". Rate limit aplicado conforme plano da chave.");

                ApiResponses responses = operation.getResponses();
                responses.addApiResponse("401", new ApiResponse().description("X-API-Key ausente, inválida, expirada ou revogada"));
                responses.addApiResponse("403", new ApiResponse().description("Plano da API key insuficiente"));
                responses.addApiResponse("429", new ApiResponse().description("Rate limit excedido"));
            }

            RequireIdempotency requireIdempotency = handlerMethod.getMethodAnnotation(RequireIdempotency.class);
            if (requireIdempotency != null) {
                operation.addParametersItem(new Parameter()
                        .name("Idempotency-Key")
                        .in("header")
                        .required(true)
                        .description("Chave única por operação para evitar duplicidade em retries do POST. X-Idempotency-Key é aceito como fallback legado.")
                        .schema(new StringSchema()));
                operation.getResponses().addApiResponse("409", new ApiResponse().description("Requisição com a mesma chave ainda está em processamento"));
                operation.getResponses().addApiResponse("422", new ApiResponse().description("Idempotency-Key reutilizada com payload diferente"));
            }

            boolean versionedTreinoEndpoint = handlerMethod.getBeanType().getSimpleName().equals("TreinoController")
                    && handlerMethod.getMethod().getName().startsWith("getTreinoById");
            if (versionedTreinoEndpoint) {
                operation.addParametersItem(new Parameter()
                        .name(ApiVersionConstants.HEADER_NAME)
                        .in("header")
                        .required(false)
                        .description("Versão da API. Valores suportados: 1, 2. Ausente = V1.")
                        .schema(new StringSchema()));
                operation.getResponses().addApiResponse("400", new ApiResponse().description("Versão de API não suportada ou entrada inválida"));
            }

            return operation;
        };
    }
}
