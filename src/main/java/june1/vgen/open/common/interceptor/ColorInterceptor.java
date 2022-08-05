package june1.vgen.open.common.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

import static june1.vgen.open.common.interceptor.InterceptorConstant.interceptorLogPrefix;

@Slf4j
public class ColorInterceptor implements HandlerInterceptor {

    private static final String object = "ColorInterceptor";
    private String uuid;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) {

        uuid = UUID.randomUUID().toString().substring(0, 8);
        log.info("{} {}=[{}][{}][{}]",
                interceptorLogPrefix, object, uuid,
                request.getDispatcherType(),
                request.getRequestURI());

        String color = request.getParameter("color");
        if (color.equals("black")) {
            log.error("{} {} {}은 컨트롤러를 호출할 수 없습니다.",
                    interceptorLogPrefix, object, color);
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView) {

        log.info("{} {}=[{}] postHandle() 호출",
                interceptorLogPrefix, object, uuid);
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception e) {

        log.info("{} {}=[{}] afterCompletion() 호출, 예외 처리가 가능=[{}]",
                interceptorLogPrefix, object, uuid, e != null ? e.getMessage() : "");
    }
}
