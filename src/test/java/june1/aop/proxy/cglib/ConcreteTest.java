package june1.aop.proxy.cglib;

import org.junit.jupiter.api.Test;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

class ConcreteTest {

    @Test
    void test() {
        Concrete target = new Concrete();
        MethodInterceptor handler = new TimeMethodInterceptor(target);

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Concrete.class);
        enhancer.setCallback(handler);
        Concrete proxy = (Concrete) enhancer.create();
        proxy.call();
    }
}