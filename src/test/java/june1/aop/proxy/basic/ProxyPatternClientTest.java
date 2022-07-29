package june1.aop.proxy.basic;

import june1.aop.proxy.basic.*;
import org.junit.jupiter.api.Test;

class ProxyPatternClientTest {

    @Test
    void 실제_객체를_호출함() {
        Subject subject = new RealSubject();
        ProxyPatternClient client = new ProxyPatternClient(subject);
        client.execute();
        client.execute();
        client.execute();
    }

    @Test
    void 프록시를_통해_캐쉬_데이터를_받음() {
        Subject real = new RealSubject();
        Subject cache = new CacheSubject(real);
        ProxyPatternClient client = new ProxyPatternClient(cache);
        client.execute();
        client.execute();
        client.execute();
    }

    @Test
    void 프록시_체인을_진행함() {
        Subject real = new RealSubject();
        Subject cache = new CacheSubject(real);
        Subject deco = new DecorateSubject(cache);
        ProxyPatternClient client = new ProxyPatternClient(deco);
        System.out.println(client.execute());
        System.out.println(client.execute());
        System.out.println(client.execute());
    }
}