package june1.vgen.open.common.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

import static june1.vgen.open.common.filter.FilterConstant.filterLogPrefix;

@Slf4j
public class ColorFilter implements Filter {

    private static final String object = "ColorFilter";
    private static final String[] allowList = {"red", "blue", "green", "black"};

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        log.info("{} {}=[{}][{}][{}]",
                filterLogPrefix, object, uuid, req.getDispatcherType(), req.getRequestURI());

        String color = req.getParameter("color");
        if (color != null && !PatternMatchUtils.simpleMatch(allowList, color)) {
            //전달하는 색깔이 null 이 아니면서 허락 목록에 없으면 다음 Filter 로 진행하지 않는다.
            log.info("{} 색깔[{}] 거부", filterLogPrefix, color);
            return;
        }

        chain.doFilter(request, response);
    }
}
