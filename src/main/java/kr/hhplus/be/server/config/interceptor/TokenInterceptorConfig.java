package kr.hhplus.be.server.config.interceptor;

import kr.hhplus.be.server.interfaces.interceptor.TokenInterceptor;
import lombok.RequiredArgsConstructor;
import org.aopalliance.intercept.Interceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class TokenInterceptorConfig implements WebMvcConfigurer {

    private final TokenInterceptor tokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/api/concerts/**")
                .addPathPatterns("/api/reservations")
                .addPathPatterns("/api/payments");

    }
}
