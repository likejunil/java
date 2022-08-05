package june1.vgen.open.common.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

import static june1.vgen.open.common.filter.FilterConstant.filterLogPrefix;

@Slf4j
public class HelloFilter implements Filter {

    private static final String object = "HelloFilter";

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        log.info("{} {}=[{}][{}][{}]",
                filterLogPrefix, object, uuid, req.getDispatcherType(), req.getRequestURI());

        chain.doFilter(request, response);
    }
}
