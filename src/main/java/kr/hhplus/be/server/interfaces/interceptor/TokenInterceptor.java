package kr.hhplus.be.server.interfaces.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hhplus.be.server.domain.entity.Queue;
import kr.hhplus.be.server.domain.service.QueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenInterceptor implements HandlerInterceptor {

    private final QueueService queueService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getHeader("Token");
        log.info("Token Value: {}", token);
        queueService.validateToken(token);
        log.info("토큰 검증 성공");

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
