package june1.vgen.open.common.config;

import june1.vgen.open.common.filter.ColorFilter;
import june1.vgen.open.common.filter.ErrorFilter;
import june1.vgen.open.common.filter.HelloFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<ColorFilter> colorFilter() {
        FilterRegistrationBean<ColorFilter> it = new FilterRegistrationBean<>();
        ColorFilter target = new ColorFilter();
        String urlPattern = "/color/*";

        it.setOrder(2);
        it.setFilter(target);
        it.addUrlPatterns(urlPattern);
        it.setDispatcherTypes(
                DispatcherType.REQUEST,
                DispatcherType.ERROR,
                DispatcherType.FORWARD);
        return it;
    }

    @Bean
    public FilterRegistrationBean<HelloFilter> helloFilter() {
        FilterRegistrationBean<HelloFilter> it = new FilterRegistrationBean<>();
        HelloFilter target = new HelloFilter();
        String urlPattern = "/*";

        it.setOrder(1);
        it.setFilter(target);
        it.addUrlPatterns(urlPattern);
        it.setDispatcherTypes(
                DispatcherType.REQUEST,
                DispatcherType.ERROR,
                DispatcherType.FORWARD);
        return it;
    }

    @Bean
    public FilterRegistrationBean<ErrorFilter> errorFilter() {
        FilterRegistrationBean<ErrorFilter> it = new FilterRegistrationBean<>();
        ErrorFilter target = new ErrorFilter();
        String urlPattern = "/error/*";

        it.setOrder(100);
        it.setFilter(target);
        it.addUrlPatterns(urlPattern);
        it.setDispatcherTypes(DispatcherType.ERROR);
        return it;
    }
}
