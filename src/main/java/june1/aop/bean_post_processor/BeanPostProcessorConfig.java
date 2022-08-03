package june1.aop.bean_post_processor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanPostProcessorConfig {

    @Bean
    public PackageLogPostProcessor packageLogPostProcessor() {

    }
}
