package vn.nqtlk8.voucher.config;
import vn.nqtlk8.voucher.context.AppContextHolder;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AppRequestContextFilter implements Filter {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String USER_ID_HEADER = "X-User-Id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest) {
            String traceId = httpRequest.getHeader(TRACE_ID_HEADER);
            String userId = httpRequest.getHeader(USER_ID_HEADER);

            if (traceId != null) AppContextHolder.setTraceId(traceId);
            if (userId != null) AppContextHolder.setUserId(userId);
        }

        try {
            chain.doFilter(request, response);
        } finally {
            AppContextHolder.clear();
        }
    }
}
