package june1.vgen.open.common.config;

import june1.vgen.open.common.interceptor.ColorInterceptor;
import june1.vgen.open.common.interceptor.HelloInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        ColorInterceptor colorInterceptor = new ColorInterceptor();
        registry.addInterceptor(colorInterceptor)
                .order(1)
                .addPathPatterns("/color/*")
                .excludePathPatterns("/color/forward");

        HelloInterceptor helloInterceptor = new HelloInterceptor();
        registry.addInterceptor(helloInterceptor)
                .order(2)
                .addPathPatterns("/**")
                .excludePathPatterns("/error/**");
    }
}
