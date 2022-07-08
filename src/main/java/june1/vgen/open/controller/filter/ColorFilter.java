package june1.vgen.open.controller.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class ColorFilter implements Filter {

    private static final String[] allowList = {"red", "blue", "green", "black"};

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String color = req.getParameter("color");
        log.info("color filter=[{}][{}][{}][{}]",
                UUID.randomUUID(),
                req.getDispatcherType(),
                req.getRequestURI(),
                color);

        if (color != null && !PatternMatchUtils.simpleMatch(allowList, color)) {
            //전달하는 색깔이 null 이 아니면서 허락 목록에 없으면 다음 Filter 로 진행하지 않는다.
            log.info("color=[{}] is denied..", color);
            return;
        }

        chain.doFilter(request, response);
    }
}
