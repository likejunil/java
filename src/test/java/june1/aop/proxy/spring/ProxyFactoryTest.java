package june1.aop.proxy.spring;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.aop.Advice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

@Slf4j
class ProxyFactoryTest {

    /**
     * 어드바이스 사용하기
     */
    @Test
    @DisplayName("인터페이스를 구현한 객체를 대상으로 프록시 만들기")
    void 인터페이스_구현체() {
        //인터페이스 상속 구현체
        Cat cat = new Cat();
        ProxyFactory catFactory = new ProxyFactory(cat);
        catFactory.addAdvice(new TimeAdvice());
        Animal catProxy = (Animal) catFactory.getProxy();
        log.info("target class=[{}]", cat.getClass());
        log.info("proxy class=[{}]", catProxy.getClass());
        log.info("result=[{}]", catProxy.makeSound());
    }

    /**
     * 어드바이저 사용하기
     */
    @Test
    @DisplayName("인터페이스를 구현한 객체를 대상으로 강제로 cglib 사용하기")
    void 인터페이스_구현체를_강제로_cglib_사용() {
        //인터페이스 상속 구현체
        Cat cat = new Cat();
        ProxyFactory catFactory = new ProxyFactory(cat);
        catFactory.setProxyTargetClass(true);
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(Pointcut.TRUE, new TimeAdvice());
        catFactory.addAdvisor(advisor);
        Animal catProxy = (Animal) catFactory.getProxy();
        log.info("target class=[{}]", cat.getClass());
        log.info("proxy class=[{}]", catProxy.getClass());
        log.info("result=[{}]", catProxy.fly());
    }

    /**
     * 여러개의 어드바이저 사용하기
     * 직접 만든 포인트컷과 스프링이 제공하는 포인트컷 사용하기
     */
    @Test
    @DisplayName("인터페이스 없는 객체를 대상으로 프록시 만들기")
    void 인터페이스_없는_구현체() {

        //1.어드바이스와 포인트컷을 담은 어드바이저 생성
        Advice timeAdvice = new TimeAdvice();
        Pointcut myPointcut = new MyPointcut();
        DefaultPointcutAdvisor advisor1 = new DefaultPointcutAdvisor(myPointcut, timeAdvice);
        //2.스프링이 제공하는 포인트컷를 사용한 어드바이저 생성
        Advice decoAdvice = new DecoAdvice();
        NameMatchMethodPointcut springPointcut = new NameMatchMethodPointcut();
        springPointcut.setMappedName("*sleep*");
        DefaultPointcutAdvisor advisor2 = new DefaultPointcutAdvisor(springPointcut, decoAdvice);

        //인터페이스 없는 객체
        //프록시를 생성하고 어드바이저 적용
        Dog dog = new Dog();
        ProxyFactory dogFactory = new ProxyFactory(dog);
        dogFactory.addAdvisor(advisor1);
        dogFactory.addAdvisor(advisor2);
        Dog dogProxy = (Dog) dogFactory.getProxy();

        log.info("target class=[{}]", dog.getClass());
        log.info("proxy class=[{}]", dogProxy.getClass());
        log.info("result=[{}]", dogProxy.makeSound());
        log.info("result=[{}]", dogProxy.fly());
        log.info("result=[{}]", dogProxy.sleep());
    }
}
