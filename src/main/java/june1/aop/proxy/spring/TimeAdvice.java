package june1.aop.proxy.spring;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

@Slf4j
public class TimeAdvice implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        long st = System.currentTimeMillis();
        // -----------------------------------------
        Object result = invocation.proceed();
        // -----------------------------------------
        long et = System.currentTimeMillis();
        log.info("메서드=[{}] 소요시간=[{}]ms", invocation.getMethod().getName(), et - st);
        return result;
    }
}
