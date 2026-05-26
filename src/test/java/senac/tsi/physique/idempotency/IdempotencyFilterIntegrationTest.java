package senac.tsi.physique.idempotency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import senac.tsi.physique.dto.apikey.ApiKeyCreateRequest;
import senac.tsi.physique.apikey.ApiAccessPlan;
import senac.tsi.physique.apikey.ApiKeyService;
import senac.tsi.physique.entities.IdempotencyRecord;
import senac.tsi.physique.repositories.ApiKeyRepository;
import senac.tsi.physique.repositories.IdempotencyRecordRepository;
import senac.tsi.physique.repositories.TreinoSerieRepository;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class IdempotencyFilterIntegrationTest {

    private static final String PATH = "/series-calculadas";
    private static final String BODY = "{\"treino\":\"Supino reto\",\"peso\":60.0,\"reps\":8}";
    private static final String DIFFERENT_BODY = "{\"treino\":\"Supino reto\",\"peso\":65.0,\"reps\":8}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IdempotencyRecordRepository idempotencyRecordRepository;

    @Autowired
    private TreinoSerieRepository treinoSerieRepository;

    @Autowired
    private ApiKeyService apiKeyService;

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    private String professorApiKey;

    @BeforeEach
    void cleanIdempotencyRecords() {
        idempotencyRecordRepository.deleteAll();
        apiKeyRepository.deleteAll();
        professorApiKey = createApiKey(ApiAccessPlan.PROFESSOR);
    }

    @Test
    void postCriticoSemIdempotencyKeyRetorna400() throws Exception {
        mockMvc.perform(post(PATH)
                        .header("X-API-Key", professorApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Missing Idempotency-Key header"));
    }

    @Test
    void primeiraChamadaComIdempotencyKeyProcessaNormalmente() throws Exception {
        String key = newKey();

        mockMvc.perform(post(PATH)
                        .header("X-API-Key", professorApiKey)
                        .header("Idempotency-Key", key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.treino").value("Supino reto"));

        var record = idempotencyRecordRepository
                .findByIdempotencyKeyAndHttpMethodAndRequestPathAndUserIdentifier(key, "POST", PATH, "usuario:1")
                .orElseThrow();

        assertThat(record.getStatus()).isEqualTo(IdempotencyStatus.COMPLETED);
        assertThat(record.getResponseStatus()).isEqualTo(201);
        assertThat(record.getResponseBody()).contains("Supino reto");
    }

    @Test
    void retryIdenticoComMesmaChaveRetornaRespostaSalvaENaoExecutaNovamente() throws Exception {
        String key = newKey();
        long countBefore = treinoSerieRepository.count();

        String firstResponse = mockMvc.perform(post(PATH)
                        .header("X-API-Key", professorApiKey)
                        .header("Idempotency-Key", key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long countAfterFirstCall = treinoSerieRepository.count();
        assertThat(countAfterFirstCall).isEqualTo(countBefore + 1);

        String retryResponse = mockMvc.perform(post(PATH)
                        .header("X-API-Key", professorApiKey)
                        .header("Idempotency-Key", key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(retryResponse).isEqualTo(firstResponse);
        assertThat(treinoSerieRepository.count()).isEqualTo(countAfterFirstCall);
    }

    @Test
    void mesmaChaveComPayloadDiferenteRetorna422() throws Exception {
        String key = newKey();

        mockMvc.perform(post(PATH)
                        .header("X-API-Key", professorApiKey)
                        .header("Idempotency-Key", key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isCreated());

        mockMvc.perform(post(PATH)
                        .header("X-API-Key", professorApiKey)
                        .header("Idempotency-Key", key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DIFFERENT_BODY))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Idempotency-Key reused with different request payload"));
    }

    @Test
    void mesmaChaveAindaProcessingRetorna409() throws Exception {
        String key = newKey();
        IdempotencyRecord record = new IdempotencyRecord();
        record.setIdempotencyKey(key);
        record.setHttpMethod("POST");
        record.setRequestPath(PATH);
        record.setUserIdentifier("usuario:1");
        record.setRequestHash(IdempotencyHashUtil.sha256("POST", PATH, "usuario:1", BODY.getBytes()));
        record.setStatus(IdempotencyStatus.PROCESSING);
        record.setExpiresAt(LocalDateTime.now().plusHours(24));
        idempotencyRecordRepository.save(record);

        mockMvc.perform(post(PATH)
                        .header("X-API-Key", professorApiKey)
                        .header("Idempotency-Key", key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Request with this Idempotency-Key is still processing"));
    }

    @Test
    void headerLegadoXIdempotencyKeyTambemFunciona() throws Exception {
        mockMvc.perform(post(PATH)
                        .header("X-API-Key", professorApiKey)
                        .header("X-Idempotency-Key", newKey())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isCreated());
    }

    @Test
    void endpointSemRequireIdempotencyContinuaFuncionandoSemHeader() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"jorge@email.com\",\"senha\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login realizado com sucesso"));
    }

    @Test
    void getNaoFoiQuebradoSemHeader() throws Exception {
        mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    private String createApiKey(ApiAccessPlan plan) {
        ApiKeyCreateRequest request = new ApiKeyCreateRequest();
        request.setUsuarioId(1L);
        request.setName("Teste " + plan.name());
        request.setAccessPlan(plan);
        return apiKeyService.createApiKey(request).getApiKey();
    }

    private String newKey() {
        return "test-" + UUID.randomUUID();
    }
}
