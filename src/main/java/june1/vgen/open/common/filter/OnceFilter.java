package june1.vgen.open.common.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import static june1.vgen.open.common.filter.FilterConstant.filterLogPrefix;

@Slf4j
@Component
public class OnceFilter extends OncePerRequestFilter {

    private static final String object = "OnceFilter";

    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain filterChain) throws ServletException, IOException {

        String uuid = UUID.randomUUID().toString().substring(0, 8);
        log.info("{} {}=[{}][{}][{}]",
                filterLogPrefix, object, uuid, req.getDispatcherType(), req.getRequestURI());

        filterChain.doFilter(req, res);
    }
}
