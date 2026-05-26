package senac.tsi.physique.versioning;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import senac.tsi.physique.exceptions.ApiErrorWriter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
public class ApiVersionHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String version = request.getHeader(ApiVersionConstants.HEADER_NAME);
        if (version != null && !version.isBlank() && !ApiVersionConstants.SUPPORTED_VERSIONS.contains(version.trim())) {
            ApiErrorWriter.write(
                    request,
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Unsupported API version",
                    "Supported versions are: 1, 2"
            );
            return;
        }
        filterChain.doFilter(request, response);
    }
}
