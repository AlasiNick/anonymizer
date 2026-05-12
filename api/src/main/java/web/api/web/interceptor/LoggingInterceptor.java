package web.api.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        log.info("=== Incoming Request ===");
        log.info("Method: {} {}", request.getMethod(), request.getRequestURI());
        log.info("Headers: {}", getHeadersAsString(request));

        if (request instanceof ContentCachingRequestWrapper) {
            ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
            byte[] content = wrapper.getContentAsByteArray();
            if (content.length > 0) {
                log.info("Request Body: {}", new String(content, StandardCharsets.UTF_8));
            }
        }

        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {

        Long startTime = (Long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - (startTime != null ? startTime : System.currentTimeMillis());

        log.info("=== Response ===");
        log.info("Status: {}", response.getStatus());
        log.info("Duration: {}ms", duration);
        log.info("====================");
    }

    private String getHeadersAsString(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder();
        Collections.list(request.getHeaderNames())
                .forEach(name -> headers.append(name).append(": ").append(request.getHeader(name)).append("; "));
        return headers.toString();
    }
}
