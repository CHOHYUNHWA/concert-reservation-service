package kr.hhplus.be.server.support.log;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogRecord;

@Component
@Slf4j
public class LoggingFilter implements Filter, java.util.logging.Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        long startTime = System.currentTimeMillis();
        log.info("Request: {} {} from IP: {}",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr()
        );

        filterChain.doFilter(servletRequest, servletResponse);

        long duration = System.currentTimeMillis() - startTime;
        log.info("Response: {} {} - Status: {} ({} ms)",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                duration
                );

    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    @Override
    public boolean isLoggable(LogRecord record) {
        return record.getLevel().intValue() >= Level.INFO.intValue();
    }
}
