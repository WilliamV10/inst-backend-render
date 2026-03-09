package sv.gob.bcr.onec.inst.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * Interceptor para trazabilidad de peticiones.
 * Extrae o genera un ID de transacción y el usuario para inyectarlos en el contexto de logs (MDC).
 */
@Component
public class TraceInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TraceInterceptor.class);

    private static final String HEADER_TX_ID = "X-Transaction-Id";
    private static final String HEADER_USER = "X-App-Client";

    public static final String MDC_TX_ID = "transactionId";
    public static final String MDC_USER = "user";

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {

        String txId = request.getHeader(HEADER_TX_ID);
        String user = request.getHeader(HEADER_USER);

        if (txId == null || txId.isBlank()) {
            txId = UUID.randomUUID().toString();
        }

        MDC.put(MDC_TX_ID, txId);
        MDC.put(MDC_USER, user != null ? user : "anonymous");

        logger.info(">>> Iniciando Petición: {} {} [IP: {}]",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr());

        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                Exception ex) {

        logger.info("<<< Finalizando Petición: {} {} [Estado: {}]",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus());

        MDC.clear();
    }
}
