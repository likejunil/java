package june1.aop.proxy.cglib;

import org.aopalliance.intercept.MethodInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.cglib.proxy.Enhancer;

import static org.junit.jupiter.api.Assertions.*;

class ConcreteTest {

    @Test
    void test() {
        Concrete target = new Concrete();
        MethodInterceptor interceptor = new TimeMethodInterceptor();

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Concrete.class);
        enhancer.setCallback();

    }

}