package june1.aop.v0.proxy;

import june1.aop.trace.LogTrace;
import june1.aop.v0.BasicController;
import june1.aop.v0.BasicRepository;
import june1.aop.v0.BasicService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ProxyConfig {

    private final LogTrace logTrace;

    @Bean
    public BasicRepository basicRepository() {
        return new RepositoryProxy(logTrace);
    }

    @Bean
    public BasicService basicService() {
        return new ServiceProxy(basicRepository(), logTrace);
    }

    @Bean
    public BasicController basicController() {
        return new ControllerProxy(basicService(), logTrace);
    }
}
