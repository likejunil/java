package june1.open.common.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class GenericFilter extends GenericFilterBean {

    private static final String object = "GenericFilter";

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        log.info("{} {}=[{}][{}][{}]",
                FilterConstant.filterLogPrefix, object, uuid, req.getDispatcherType(), req.getRequestURI());

        chain.doFilter(request, response);
    }
}
