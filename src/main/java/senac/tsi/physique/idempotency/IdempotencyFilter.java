package senac.tsi.physique.idempotency;

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
import org.springframework.web.util.ContentCachingResponseWrapper;
import senac.tsi.physique.apikey.ApiKeyAuthenticationContext;
import senac.tsi.physique.exceptions.ApiErrorWriter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class IdempotencyFilter extends OncePerRequestFilter {

    private static final String JSON_CONTENT_TYPE = "application/json";
    private static final String ANONYMOUS_USER = "anonymous";

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final IdempotencyService idempotencyService;

    public IdempotencyFilter(RequestMappingHandlerMapping requestMappingHandlerMapping,
                             IdempotencyService idempotencyService) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        this.idempotencyService = idempotencyService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!HttpMethod.POST.matches(request.getMethod()) || !requiresIdempotency(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);
        String idempotencyKey = idempotencyService.extractIdempotencyKey(cachedRequest);
        if (idempotencyKey == null) {
            writeError(request, response, HttpServletResponse.SC_BAD_REQUEST, "Bad Request", "Missing Idempotency-Key header");
            return;
        }

        String requestPath = cachedRequest.getRequestURI();
        String userIdentifier = resolveUserIdentifier(cachedRequest);
        String requestHash = IdempotencyHashUtil.sha256(
                cachedRequest.getMethod(),
                requestPath,
                userIdentifier,
                cachedRequest.getCachedBody()
        );

        IdempotencyDecision decision = idempotencyService.evaluate(
                idempotencyKey,
                cachedRequest.getMethod(),
                requestPath,
                userIdentifier,
                requestHash
        );

        switch (decision.getType()) {
            case REPLAY -> writeSavedResponse(response, decision);
            case PAYLOAD_MISMATCH, PROCESSING, FAILED -> writeError(
                    request,
                    response,
                    decision.getResponseStatus(),
                    resolveErrorName(decision.getResponseStatus()),
                    decision.getErrorMessage()
            );
            case PROCEED -> proceedAndStoreResponse(cachedRequest, response, filterChain, decision.getRecordId());
        }
    }

    private boolean requiresIdempotency(HttpServletRequest request) throws ServletException {
        try {
            HandlerExecutionChain handlerExecutionChain = requestMappingHandlerMapping.getHandler(request);
            if (handlerExecutionChain == null || !(handlerExecutionChain.getHandler() instanceof HandlerMethod handlerMethod)) {
                return false;
            }

            return handlerMethod.hasMethodAnnotation(RequireIdempotency.class);
        } catch (Exception exception) {
            throw new ServletException("Could not inspect handler for idempotency requirement", exception);
        }
    }

    private String resolveUserIdentifier(HttpServletRequest request) {
        Object context = request.getAttribute(ApiKeyAuthenticationContext.REQUEST_ATTRIBUTE_NAME);
        if (context instanceof ApiKeyAuthenticationContext apiKeyContext && apiKeyContext.getUsuarioId() != null) {
            return "usuario:" + apiKeyContext.getUsuarioId();
        }
        if (request.getUserPrincipal() != null && request.getUserPrincipal().getName() != null) {
            return request.getUserPrincipal().getName();
        }
        return ANONYMOUS_USER;
    }

    private void proceedAndStoreResponse(CachedBodyHttpServletRequest request,
                                         HttpServletResponse response,
                                         FilterChain filterChain,
                                         Long recordId) throws ServletException, IOException {
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(request, responseWrapper);
            String responseBody = getResponseBody(responseWrapper);
            idempotencyService.markCompleted(recordId, responseWrapper.getStatus(), responseBody);
        } catch (ServletException | IOException | RuntimeException exception) {
            idempotencyService.markFailed(recordId);
            throw exception;
        } finally {
            responseWrapper.copyBodyToResponse();
        }
    }

    private String getResponseBody(ContentCachingResponseWrapper responseWrapper) {
        byte[] responseBytes = responseWrapper.getContentAsByteArray();
        Charset charset = responseWrapper.getCharacterEncoding() == null
                ? StandardCharsets.UTF_8
                : Charset.forName(responseWrapper.getCharacterEncoding());
        return new String(responseBytes, charset);
    }

    private void writeSavedResponse(HttpServletResponse response, IdempotencyDecision decision) throws IOException {
        response.setStatus(decision.getResponseStatus() == null ? HttpServletResponse.SC_OK : decision.getResponseStatus());
        response.setContentType(JSON_CONTENT_TYPE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        if (decision.getResponseBody() != null) {
            response.getWriter().write(decision.getResponseBody());
        }
    }

    private void writeError(HttpServletRequest request, HttpServletResponse response, int status, String error, String message) throws IOException {
        ApiErrorWriter.write(request, response, status, error, message);
    }

    private String resolveErrorName(int status) {
        if (status == HttpServletResponse.SC_CONFLICT) {
            return "Conflict";
        }
        if (status == 422) {
            return "Unprocessable Entity";
        }
        return "Bad Request";
    }
}
