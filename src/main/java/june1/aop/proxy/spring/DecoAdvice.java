package june1.aop.proxy.spring;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

@Slf4j
public class DecoAdvice implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        log.info("응답 결과를 주목하게 만들어 주마..");
        String prefix = "\n-----------------------------------------------\n";
        String postfix = "\n-----------------------------------------------\n";
        Object result = invocation.proceed();
        return prefix + (result == null ? "" : result) + postfix;
    }
}
