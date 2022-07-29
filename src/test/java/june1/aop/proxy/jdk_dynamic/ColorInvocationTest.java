package june1.aop.proxy.jdk_dynamic;

import june1.aop.proxy.jdk_dynamic.ColorInterface;
import june1.aop.proxy.jdk_dynamic.ColorInvocation;
import june1.aop.proxy.jdk_dynamic.RedImpl;
import june1.aop.trace.LogTrace;
import june1.aop.trace.LogTraceImpl;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

class ColorInvocationTest {

    @Test
    void test() {
        //추가하고 싶은 기능..
        LogTrace logTrace = new LogTraceImpl();
        //추가하려는 본 대상..
        ColorInterface target = new RedImpl();

        InvocationHandler handler = new ColorInvocation(target, logTrace);
        ColorInterface proxy = (ColorInterface) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                new Class[]{ColorInterface.class},
                handler);

        String ret = proxy.color("black");
        System.out.println(ret);
    }
}