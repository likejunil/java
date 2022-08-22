package june1.db;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.PostConstruct;

/**
 * @Transactional 은 public method 에만 적용된다.
 * 다른 접근제어자 메서드에 적용해도 예외가 발생하지는 않지만 무시된다.
 * <p>
 * target 인스턴스에서 호출하는 내부 메서드는 @Transactional 적용을 받지 못한다.
 * 이러한 경우 내부 메서드를 별도의 클래스로 분리하면 된다.
 */
@Slf4j
@SpringBootTest
public class InitTxTest {

    @Autowired
    private Hello hello;

    @Test
    void hello() {
        /* ... */
    }

    @TestConfiguration
    static class HelloConfig {

        @Bean
        public Hello hello() {
            return new Hello();
        }
    }

    static class Hello {

        /**
         * 스프링 트랜잭션 AOP 는..
         * 스프링이 완전하게 가동된 후 적용된다.
         * 즉, @PostConstruct 가 처리된 후에 @InitTxTest 이 적용된다.
         * 따라서 다음 초기화 메서드는 트랜잭션을 적용받지 못한다.
         * <p>
         * 보통.. @PostConstruct 는 빈후처리기(Post Bean Processor)에 의해 처리되기 때문에..
         * Spring Container 에 등록되기 전에 실행된다.
         */
        @PostConstruct
        @Transactional
        public void init1() {
            //트랜잭션 동기화 매니저에게..
            //현재 트랜잭션이 활성화되어 있는지 물어본다.
            boolean isActive = TransactionSynchronizationManager
                    .isActualTransactionActive();
            log.info("Hello 클래스가 로딩되는 시점에 "
                    + "@PostConstruct 가 적용된 메서드 init1() 의 "
                    + "트랜잭션 적용 여부=[{}]", isActive);
        }

        /**
         * 애플리케이션이 완전히 준비가 되면.. (AOP 까지 모두 준비가 완료되면..)
         * ApplicationReadyEvent 가 발생한다.
         *
         * @EventListener() 가 해당 이벤트를 듣고 있다가 실행하면
         * 트랜잭션을 적용받을 수 있다.
         */
        @EventListener(ApplicationReadyEvent.class)
        @Transactional
        public void init2() {
            //트랜잭션 동기화 매니저에게..
            //현재 트랜잭션이 활성화되어 있는지 물어본다.
            boolean isActive = TransactionSynchronizationManager
                    .isActualTransactionActive();
            log.info("Application 자체가 완전히 준비되는 시점에 "
                    + "ApplicationReadyEvent 를 받은 메서드 init2() 의 "
                    + "트랜잭션 적용 여부=[{}]", isActive);
        }
    }
}
