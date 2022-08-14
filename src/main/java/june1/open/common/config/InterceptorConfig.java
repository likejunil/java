package june1.open.common.config;

import june1.open.common.interceptor.HelloInterceptor;
import june1.open.common.interceptor.LogInterceptor;
import june1.open.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {

    private final LogRepository logRepository;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LogInterceptor logInterceptor = new LogInterceptor(logRepository);
        registry.addInterceptor(logInterceptor)
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/error/**");

        HelloInterceptor helloInterceptor = new HelloInterceptor();
        registry.addInterceptor(helloInterceptor)
                .order(2)
                .addPathPatterns("/**")
                .excludePathPatterns("/error/**");
    }
}
