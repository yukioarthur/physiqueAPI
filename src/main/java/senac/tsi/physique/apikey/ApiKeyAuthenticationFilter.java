package senac.tsi.physique.apikey;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import senac.tsi.physique.idempotency.CachedBodyHttpServletRequest;
import senac.tsi.physique.exceptions.ApiErrorWriter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-Key";
    private static final Pattern USUARIO_PATH_PATTERN = Pattern.compile("/usuarios/(\\d+)(/.*)?$");
    private static final Pattern USUARIO_ID_JSON_PATTERN = Pattern.compile("\\\"usuarioId\\\"\\s*:\\s*(\\d+)");

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final ApiKeyService apiKeyService;
    private final RateLimitService rateLimitService;

    public ApiKeyAuthenticationFilter(RequestMappingHandlerMapping requestMappingHandlerMapping,
                                      ApiKeyService apiKeyService,
                                      RateLimitService rateLimitService) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        this.apiKeyService = apiKeyService;
        this.rateLimitService = rateLimitService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        RequireApiKey requireApiKey = getRequireApiKey(request);
        if (requireApiKey == null) {
            filterChain.doFilter(request, response);
            return;
        }

        HttpServletRequest requestToUse = request;
        CachedBodyHttpServletRequest cachedRequest = null;
        if (mayHaveBody(request)) {
            cachedRequest = new CachedBodyHttpServletRequest(request);
            requestToUse = cachedRequest;
        }

        String headerApiKey = apiKeyService.extractApiKey(request.getHeader(API_KEY_HEADER));
        if (headerApiKey == null) {
            writeError(request, response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized", "Missing X-API-Key header");
            return;
        }

        ApiKeyAuthenticationContext context;
        try {
            context = apiKeyService.authenticate(headerApiKey);
        } catch (ApiKeyValidationException exception) {
            writeError(request, response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized", exception.getMessage());
            return;
        }

        ApiAccessPlan requiredPlan = requireApiKey.minPlan();
        if (!context.getAccessPlan().hasAtLeast(requiredPlan)) {
            writeError(request, response, HttpServletResponse.SC_FORBIDDEN, "Forbidden", "Insufficient API access plan");
            return;
        }

        if (!canAlunoAccessRequestedUsuario(context, requestToUse, cachedRequest)) {
            writeError(request, response, HttpServletResponse.SC_FORBIDDEN, "Forbidden", "Insufficient API access plan");
            return;
        }

        RateLimitResult rateLimitResult = rateLimitService.consume(context.getApiKeyId(), context.getAccessPlan());
        addRateLimitHeaders(response, context.getAccessPlan(), rateLimitResult);
        if (!rateLimitResult.isAllowed()) {
            response.setHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(rateLimitResult.getRetryAfterSeconds()));
            response.setHeader("Retry-After", String.valueOf(rateLimitResult.getRetryAfterSeconds()));
            writeRateLimitError(request, response);
            return;
        }

        requestToUse.setAttribute(ApiKeyAuthenticationContext.REQUEST_ATTRIBUTE_NAME, context);
        filterChain.doFilter(requestToUse, response);
    }

    private RequireApiKey getRequireApiKey(HttpServletRequest request) throws ServletException {
        try {
            HandlerExecutionChain handlerExecutionChain = requestMappingHandlerMapping.getHandler(request);
            if (handlerExecutionChain == null || !(handlerExecutionChain.getHandler() instanceof HandlerMethod handlerMethod)) {
                return null;
            }

            RequireApiKey methodAnnotation = handlerMethod.getMethodAnnotation(RequireApiKey.class);
            if (methodAnnotation != null) {
                return methodAnnotation;
            }
            return handlerMethod.getBeanType().getAnnotation(RequireApiKey.class);
        } catch (Exception exception) {
            throw new ServletException("Could not inspect handler for API key requirement", exception);
        }
    }

    private boolean mayHaveBody(HttpServletRequest request) {
        return HttpMethod.POST.matches(request.getMethod())
                || HttpMethod.PUT.matches(request.getMethod())
                || HttpMethod.PATCH.matches(request.getMethod());
    }

    private boolean canAlunoAccessRequestedUsuario(ApiKeyAuthenticationContext context,
                                                  HttpServletRequest request,
                                                  CachedBodyHttpServletRequest cachedRequest) {
        if (context.getAccessPlan() != ApiAccessPlan.ALUNO || context.getUsuarioId() == null) {
            return true;
        }

        Long pathUsuarioId = extractUsuarioIdFromPath(request.getRequestURI());
        if (pathUsuarioId != null && !pathUsuarioId.equals(context.getUsuarioId())) {
            return false;
        }

        Long bodyUsuarioId = extractUsuarioIdFromBody(cachedRequest);
        return bodyUsuarioId == null || bodyUsuarioId.equals(context.getUsuarioId());
    }

    private Long extractUsuarioIdFromPath(String requestUri) {
        Matcher matcher = USUARIO_PATH_PATTERN.matcher(requestUri);
        if (matcher.find()) {
            return Long.valueOf(matcher.group(1));
        }
        return null;
    }

    private Long extractUsuarioIdFromBody(CachedBodyHttpServletRequest cachedRequest) {
        if (cachedRequest == null || cachedRequest.getCachedBody() == null || cachedRequest.getCachedBody().length == 0) {
            return null;
        }
        String body = new String(cachedRequest.getCachedBody(), StandardCharsets.UTF_8);
        Matcher matcher = USUARIO_ID_JSON_PATTERN.matcher(body);
        if (matcher.find()) {
            return Long.valueOf(matcher.group(1));
        }
        return null;
    }

    private void addRateLimitHeaders(HttpServletResponse response, ApiAccessPlan plan, RateLimitResult result) {
        response.setHeader("X-Rate-Limit-Plan", plan.name());
        response.setHeader("X-Rate-Limit-Remaining", String.valueOf(result.getRemainingTokens()));
        response.setHeader("RateLimit-Policy", plan.getRateLimitPolicy());
        response.setHeader("RateLimit", "limit=" + plan.getRequestsPerMinute() + ", remaining=" + result.getRemainingTokens() + ", reset=60");
    }

    private void writeError(HttpServletRequest request, HttpServletResponse response, int status, String error, String message) throws IOException {
        ApiErrorWriter.write(request, response, status, error, message);
    }

    private void writeRateLimitError(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ApiErrorWriter.write(request, response, 429, "Too Many Requests", "API rate limit exceeded");
    }
}
