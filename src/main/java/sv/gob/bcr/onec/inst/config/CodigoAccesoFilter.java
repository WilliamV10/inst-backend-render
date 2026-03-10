package sv.gob.bcr.onec.inst.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sv.gob.bcr.onec.inst.repository.CodigoAccesoRepository;

import java.io.IOException;

/**
 * Filtro que valida el header X-Access-Code en todas las peticiones a /api/**,
 * excepto en los endpoints de administración de códigos de acceso.
 */
@Component
@RequiredArgsConstructor
public class CodigoAccesoFilter extends OncePerRequestFilter {

    public static final String HEADER_ACCESS_CODE = "X-Access-Code";

    private final CodigoAccesoRepository codigoAccesoRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();
        // Excluir Swagger UI, docs y únicamente el endpoint bulk de generación de códigos
        return path.startsWith("/swagger-ui")
                || path.startsWith("/api-docs")
                || path.equals("/actuator/**")
                || (path.equals("/api/v1/access-codes/bulk") && "POST".equalsIgnoreCase(method));
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String codigo = request.getHeader(HEADER_ACCESS_CODE);

        if (codigo == null || codigo.isBlank()) {
            reject(response, "Header X-Access-Code requerido");
            return;
        }

        boolean valido = codigoAccesoRepository.findByCodigo(codigo)
                .map(c -> Boolean.TRUE.equals(c.getActivo()))
                .orElse(false);

        if (!valido) {
            reject(response, "Código de acceso inválido o inactivo");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void reject(HttpServletResponse response, String mensaje) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\":\"" + mensaje + "\"}");
    }
}
