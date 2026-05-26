package senac.tsi.physique.cors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import senac.tsi.physique.apikey.ApiAccessPlan;
import senac.tsi.physique.apikey.ApiKeyService;
import senac.tsi.physique.dto.apikey.ApiKeyCreateRequest;
import senac.tsi.physique.repositories.ApiKeyRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class CorsIntegrationTest {

    private static final String ALLOWED_ORIGIN = "http://localhost:5173";
    private static final String FORBIDDEN_ORIGIN = "http://malicious.example";

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
    void optionsDeOrigemPermitidaRetornaHeadersCors() throws Exception {
        mockMvc.perform(options("/treinos")
                        .header(HttpHeaders.ORIGIN, ALLOWED_ORIGIN)
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "Content-Type,X-API-Key,Idempotency-Key"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED_ORIGIN))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, containsString("POST")))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, containsString("X-API-Key")))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, containsString("Idempotency-Key")));
    }

    @Test
    void getDeOrigemPermitidaContemAccessControlAllowOrigin() throws Exception {
        String apiKey = createApiKey(ApiAccessPlan.ALUNO);

        mockMvc.perform(get("/dashboard/1")
                        .header(HttpHeaders.ORIGIN, ALLOWED_ORIGIN)
                        .header("X-API-Key", apiKey))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED_ORIGIN))
                .andExpect(header().exists("X-Rate-Limit-Remaining"));
    }

    @Test
    void origemNaoPermitidaNaoRecebeLiberacaoCors() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .header(HttpHeaders.ORIGIN, FORBIDDEN_ORIGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"jorge@email.com\",\"senha\":\"123456\"}"))
                .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    void optionsNaoExigeApiKeyMesmoParaEndpointProtegido() throws Exception {
        mockMvc.perform(options("/dashboard/1")
                        .header(HttpHeaders.ORIGIN, ALLOWED_ORIGIN)
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "X-API-Key"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED_ORIGIN));
    }

    @Test
    void optionsNaoExigeIdempotencyKeyMesmoParaPostIdempotente() throws Exception {
        mockMvc.perform(options("/treinos")
                        .header(HttpHeaders.ORIGIN, ALLOWED_ORIGIN)
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "Content-Type,X-API-Key,Idempotency-Key"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED_ORIGIN));
    }

    @Test
    void optionsNaoConsomeRateLimit() throws Exception {
        String apiKey = createApiKey(ApiAccessPlan.ALUNO);

        for (int i = 0; i < 5; i++) {
            mockMvc.perform(options("/dashboard/1")
                            .header(HttpHeaders.ORIGIN, ALLOWED_ORIGIN)
                            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "X-API-Key"))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get("/dashboard/1")
                        .header(HttpHeaders.ORIGIN, ALLOWED_ORIGIN)
                        .header("X-API-Key", apiKey))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Rate-Limit-Remaining", "59"));
    }

    @Test
    void headersCustomizadosSaoPermitidosNoPreflight() throws Exception {
        mockMvc.perform(options("/treinos")
                        .header(HttpHeaders.ORIGIN, ALLOWED_ORIGIN)
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "Content-Type,X-API-Key,Idempotency-Key,X-Idempotency-Key"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, containsString("X-API-Key")))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, containsString("Idempotency-Key")))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, containsString("X-Idempotency-Key")));
    }

    @Test
    void endpointNormalContinuaProtegidoPorApiKeyQuandoNaoForOptions() throws Exception {
        mockMvc.perform(get("/dashboard/1")
                        .header(HttpHeaders.ORIGIN, ALLOWED_ORIGIN))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED_ORIGIN));
    }

    @Test
    void openApiMencionaCors() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.info.description", containsString("CORS")))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.info.description", containsString("preflight OPTIONS")));
    }

    private String createApiKey(ApiAccessPlan plan) {
        ApiKeyCreateRequest request = new ApiKeyCreateRequest();
        request.setUsuarioId(1L);
        request.setName("Teste CORS " + plan.name());
        request.setAccessPlan(plan);
        request.setExpiresAt(LocalDateTime.now().plusDays(1));
        return apiKeyService.createApiKey(request).getApiKey();
    }
}
