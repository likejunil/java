package june1.aop.proxy.spring;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.util.PatternMatchUtils;

import java.lang.reflect.Method;

public class MyPointcut implements Pointcut {

    @Override
    public ClassFilter getClassFilter() {
        return ClassFilter.TRUE;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return new MyMethodMatcher();
    }

    static class MyMethodMatcher implements MethodMatcher {

        private final String[] matchName = {"fly*", "*run"};

        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            return PatternMatchUtils.simpleMatch(matchName, method.getName());
        }

        @Override
        public boolean isRuntime() {
            return false;
        }

        @Override
        public boolean matches(Method method, Class<?> targetClass, Object... args) {
            throw new UnsupportedOperationException();
        }
    }
}
