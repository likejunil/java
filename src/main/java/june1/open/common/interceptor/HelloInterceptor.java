package june1.open.common.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

import static june1.open.common.interceptor.InterceptorConstant.interceptorLogPrefix;

@Slf4j
public class HelloInterceptor implements HandlerInterceptor {

    private static final String object = "HelloInterceptor";

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) {

        String uuid = UUID.randomUUID().toString().substring(0, 8);
        log.info("{} {}=[{}][{}][{}]",
                interceptorLogPrefix, object, uuid,
                request.getDispatcherType(),
                request.getRequestURI());

        return true;
    }
}
