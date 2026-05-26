package senac.tsi.physique.finalfeatures;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import senac.tsi.physique.apikey.ApiAccessPlan;
import senac.tsi.physique.apikey.ApiKeyService;
import senac.tsi.physique.dto.apikey.ApiKeyCreateRequest;
import senac.tsi.physique.repositories.ApiKeyRepository;
import senac.tsi.physique.repositories.IdempotencyRecordRepository;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class VersionValidationErrorOpenApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApiKeyService apiKeyService;

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @Autowired
    private IdempotencyRecordRepository idempotencyRecordRepository;

    private String professorApiKey;

    @BeforeEach
    void setup() {
        idempotencyRecordRepository.deleteAll();
        apiKeyRepository.deleteAll();
        professorApiKey = createApiKey(ApiAccessPlan.PROFESSOR);
    }

    @Test
    void treinoComApiVersion1RetornaV1NoMesmoPath() throws Exception {
        mockMvc.perform(get("/treinos/1")
                        .header("X-API-Version", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$.nome").value("Treino A"));
    }

    @Test
    void treinoComApiVersion2RetornaV2NoMesmoPath() throws Exception {
        mockMvc.perform(get("/treinos/1")
                        .header("X-API-Version", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apiVersion").value("2"))
                .andExpect(jsonPath("$.quantidadeExercicios").value(2))
                .andExpect(jsonPath("$.links.self").value("/treinos/1"));
    }

    @Test
    void treinoSemApiVersionUsaV1PorCompatibilidade() throws Exception {
        mockMvc.perform(get("/treinos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$.nome").value("Treino A"));
    }

    @Test
    void apiVersionInvalidaRetorna400ComMensagemClara() throws Exception {
        mockMvc.perform(get("/treinos/1")
                        .header("X-API-Version", "99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Unsupported API version"))
                .andExpect(jsonPath("$.message").value("Supported versions are: 1, 2"));
    }

    @Test
    void requestInvalidoRetorna400ComFieldErrors() throws Exception {
        mockMvc.perform(post("/treinos")
                        .header("X-API-Key", professorApiKey)
                        .header("Idempotency-Key", "validacao-" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("nome")))
                .andExpect(jsonPath("$.fieldErrors[*].message", hasItem("O nome do treino é obrigatório")));
    }

    @Test
    void numeroNegativoOndeNaoPodeRetorna400() throws Exception {
        mockMvc.perform(post("/series-calculadas")
                        .header("X-API-Key", professorApiKey)
                        .header("Idempotency-Key", "negativo-" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"treino\":\"Supino\",\"peso\":-1,\"reps\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("peso")))
                .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("reps")));
    }

    @Test
    void pathVariableInvalidoRetorna400() throws Exception {
        mockMvc.perform(get("/treinos/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));
    }

    @Test
    void recursoInexistenteRetorna404ComFormatoPadrao() throws Exception {
        mockMvc.perform(get("/treinos/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void jsonMalformadoRetorna400() throws Exception {
        mockMvc.perform(post("/series-calculadas")
                        .header("X-API-Key", professorApiKey)
                        .header("Idempotency-Key", "json-" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"treino\":\"Supino\",\"peso\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Malformed JSON"));
    }

    @Test
    void openApiDocumentaApiVersionApiKeyEIdempotencyKey() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.description", containsString("X-API-Version")))
                .andExpect(jsonPath("$.components.securitySchemes.ApiKeyAuth.name").value("X-API-Key"))
                .andExpect(jsonPath("$.info.description", containsString("Idempotency-Key")));
    }

    private String createApiKey(ApiAccessPlan plan) {
        ApiKeyCreateRequest request = new ApiKeyCreateRequest();
        request.setUsuarioId(1L);
        request.setName("Teste final " + plan.name());
        request.setAccessPlan(plan);
        return apiKeyService.createApiKey(request).getApiKey();
    }
}
