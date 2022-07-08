package june1.vgen.open.controller.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
public class ColorInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        String color = request.getParameter("color");
        log.info("color interceptor=[{}][{}][{}][{}]",
                UUID.randomUUID(),
                request.getDispatcherType(),
                request.getRequestURI(),
                color);

        if (color.equals("black")) {
            log.error("검은색은 컨트롤러를 호출할 수 없습니다.");
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView) throws Exception {

        log.info("postHandler 는 예외 처리를 할 수 없습니다.");
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) throws Exception {

        log.info("afterCompletion 은 예외 처리를 할 수 있습니다. 메시지=[{}]",
                ex == null ? "" : ex.getMessage());
    }
}
