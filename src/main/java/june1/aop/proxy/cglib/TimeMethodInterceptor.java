package june1.aop.proxy.cglib;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

@Slf4j
@RequiredArgsConstructor
public class TimeMethodInterceptor implements MethodInterceptor {

    private final Object target;

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        long st = System.currentTimeMillis();
        // ---------------------------------------------------------
        Object result = methodProxy.invoke(target, args);
        // ---------------------------------------------------------
        long et = System.currentTimeMillis();
        log.info("메서드=[{}] 소요시간=[{}]ms", method.getName(), et - st);
        return result;
    }
}
