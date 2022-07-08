package june1.vgen.open.common.config;

import june1.vgen.open.controller.filter.ColorFilter;
import june1.vgen.open.controller.filter.HelloFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<ColorFilter> colorFilter() {
        FilterRegistrationBean<ColorFilter> it = new FilterRegistrationBean<>();
        ColorFilter colorFilter = new ColorFilter();

        it.setFilter(colorFilter);
        it.setOrder(2);
        it.addUrlPatterns("/color/*", "/error");
        it.setDispatcherTypes(
                DispatcherType.REQUEST,
                DispatcherType.ERROR,
                DispatcherType.FORWARD);
        return it;
    }

    @Bean
    public FilterRegistrationBean<HelloFilter> helloFilter() {
        FilterRegistrationBean<HelloFilter> it = new FilterRegistrationBean<>();
        HelloFilter helloFilter = new HelloFilter();

        it.setFilter(helloFilter);
        it.setOrder(1);
        it.addUrlPatterns("/*");
        it.setDispatcherTypes(
                DispatcherType.REQUEST,
                DispatcherType.ERROR,
                DispatcherType.FORWARD);
        return it;
    }
}
