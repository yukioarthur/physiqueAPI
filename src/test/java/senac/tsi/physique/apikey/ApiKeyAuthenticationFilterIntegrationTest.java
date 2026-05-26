package senac.tsi.physique.apikey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import senac.tsi.physique.dto.apikey.ApiKeyCreateRequest;
import senac.tsi.physique.repositories.ApiKeyRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class ApiKeyAuthenticationFilterIntegrationTest {

    private static final String EXERCICIO_BODY = """
            {
              "nome": "Teste API Key",
              "repeticoes": 10,
              "quantidadeSeries": 3,
              "grupoMuscularId": 1,
              "musculoId": 1,
              "descricao": "Teste",
              "video": ""
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApiKeyService apiKeyService;

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @BeforeEach
    void cleanApiKeys() {
        apiKeyRepository.deleteAll();
    }

    @Test
    void endpointProtegidoSemApiKeyRetorna401() throws Exception {
        mockMvc.perform(get("/dashboard/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Missing X-API-Key header"));
    }

    @Test
    void endpointProtegidoComChaveInvalidaRetorna401() throws Exception {
        mockMvc.perform(get("/dashboard/1")
                        .header("X-API-Key", "phy_invalida"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid API key"));
    }

    @Test
    void endpointProtegidoComChaveRevogadaRetorna401() throws Exception {
        String apiKey = createApiKey(ApiAccessPlan.ALUNO);
        Long apiKeyId = apiKeyRepository.findAll().get(0).getId();
        apiKeyService.revoke(apiKeyId);

        mockMvc.perform(get("/dashboard/1")
                        .header("X-API-Key", apiKey))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Inactive API key"));
    }

    @Test
    void endpointAlunoComChaveAlunoFuncionaERetornaHeadersDeRateLimit() throws Exception {
        String apiKey = createApiKey(ApiAccessPlan.ALUNO);

        mockMvc.perform(get("/dashboard/1")
                        .header("X-API-Key", apiKey))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Rate-Limit-Plan", "ALUNO"))
                .andExpect(header().exists("X-Rate-Limit-Remaining"));
    }

    @Test
    void endpointProfessorComChaveAlunoRetorna403() throws Exception {
        String apiKey = createApiKey(ApiAccessPlan.ALUNO);

        mockMvc.perform(post("/exercicios")
                        .header("X-API-Key", apiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(EXERCICIO_BODY))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Insufficient API access plan"));
    }

    @Test
    void endpointProfessorComChaveProfessorFunciona() throws Exception {
        String apiKey = createApiKey(ApiAccessPlan.PROFESSOR);

        mockMvc.perform(post("/exercicios")
                        .header("X-API-Key", apiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(EXERCICIO_BODY))
                .andExpect(status().isCreated());
    }

    @Test
    void endpointAdminComChaveProfessorRetorna403() throws Exception {
        String apiKey = createApiKey(ApiAccessPlan.PROFESSOR);

        mockMvc.perform(get("/api-keys/usuarios/1")
                        .header("X-API-Key", apiKey))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Insufficient API access plan"));
    }

    @Test
    void endpointAdminComChaveAdminFunciona() throws Exception {
        String apiKey = createApiKey(ApiAccessPlan.ADMIN);

        mockMvc.perform(get("/api-keys/usuarios/1")
                        .header("X-API-Key", apiKey))
                .andExpect(status().isOk());
    }

    @Test
    void rateLimitExcedidoRetorna429() throws Exception {
        String apiKey = createApiKey(ApiAccessPlan.ALUNO);

        for (int i = 0; i < 60; i++) {
            mockMvc.perform(get("/dashboard/1")
                            .header("X-API-Key", apiKey))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get("/dashboard/1")
                        .header("X-API-Key", apiKey))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists("X-Rate-Limit-Retry-After-Seconds"))
                .andExpect(jsonPath("$.error").value("Too Many Requests"))
                .andExpect(jsonPath("$.message").value("API rate limit exceeded"));
    }

    @Test
    void swaggerMostraApiKey() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.securitySchemes.ApiKeyAuth.name").value("X-API-Key"));
    }

    @Test
    void endpointSemRequireApiKeyContinuaFuncionando() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"jorge@email.com\",\"senha\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login realizado com sucesso"));
    }

    @Test
    void apiKeyExpiradaRetorna401() throws Exception {
        String apiKey = createApiKey(ApiAccessPlan.ALUNO, LocalDateTime.now().minusDays(1));

        mockMvc.perform(get("/dashboard/1")
                        .header("X-API-Key", apiKey))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Inactive API key"));
    }

    @Test
    void alunoNaoAcessaDadosDeOutroUsuario() throws Exception {
        String apiKey = createApiKey(ApiAccessPlan.ALUNO);

        mockMvc.perform(get("/dashboard/999")
                        .header("X-API-Key", apiKey))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Insufficient API access plan"));
    }

    @Test
    void descricaoDoOpenApiMencionaRateLimit() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.description", containsString("ALUNO: 60 requisições por minuto")));
    }

    private String createApiKey(ApiAccessPlan plan) {
        return createApiKey(plan, null);
    }

    private String createApiKey(ApiAccessPlan plan, LocalDateTime expiresAt) {
        ApiKeyCreateRequest request = new ApiKeyCreateRequest();
        request.setUsuarioId(1L);
        request.setName("Teste " + plan.name());
        request.setAccessPlan(plan);
        request.setExpiresAt(expiresAt);
        return apiKeyService.createApiKey(request).getApiKey();
    }
}
