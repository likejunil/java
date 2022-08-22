package june1.db;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
public class RollbackTest {

    @Autowired
    private Service service;

    /**
     * 일반적으로 언체크(RuntimeException) 예외는 복구 불가능한 예외로 가정한다.
     * 이러한 경우 서비스 실패로 간주하고 처음의 상태로 되돌린다.
     */
    @Test
    void callRuntimeException() {
        Assertions.assertThatThrownBy(() -> service.runtimeException())
                .isInstanceOf(RuntimeException.class);
    }

    /**
     * 체크 예외는 비지니스 로직에서 부분적으로 처리 가능한 데이터를 기록할 때 사용할 수 있다.
     * 예를 들면.. 주문 시 결제 잔고가 부족하면 주문 데이터를 저장하고 결제 상태를 대기로 처리할 수 있다.
     * 이러한 경우 rollback 보다는 부분적으로 처리한 상태까지 commit 하는 것이 가능하다.
     * 중요한 비지니스 로직에 대하여 예외가 발생하면 체크 예외를 정의하여 사용하는 것을 추천한다.
     * (강제는 아님)
     */
    @Test
    void callCheckException() {
        Assertions.assertThatThrownBy(() -> service.checkedException())
                .isInstanceOf(CheckedException.class);
    }

    /**
     * @Transaction(rollbackFor = ...) 을 사용하면
     * 체크 예외까지도 rollback 할 수 있다.
     */
    @Test
    void callRollbackFor() {
        Assertions.assertThatThrownBy(() -> service.rollbackFor())
                .isInstanceOf(CheckedException.class);
    }

    @TestConfiguration
    static class ServiceConfig {

        @Bean
        public Service service() {
            return new Service();
        }
    }

    @Slf4j
    static class Service {

        /**
         * 체크 예외는 트랜잭션 매니저에 의하여 commit 을 시도한다.
         */
        @Transactional
        public void checkedException() throws CheckedException {
            log.info("체크 예외가 발생했을 때 커밋 여부 확인");
            throw new CheckedException();
        }

        /**
         * 기본적으로 Error, RuntimeException 이 발생하면..
         * 트랜잭션 매니저는 rollback 을 시도한다.
         */
        @Transactional
        public void runtimeException() {
            log.info("런타임 예외가 발생했을 때 롤백 여부 확인");
            throw new RuntimeException();
        }

        /**
         * rollbackFor 옵션으로 체크 예외도 rollback 이 가능하다.
         */
        @Transactional(rollbackFor = CheckedException.class)
        public void rollbackFor() throws CheckedException {
            log.info("체크 예외를 롤백시키는 옵션 확인");
            throw new CheckedException();
        }
    }

    static class CheckedException extends Exception {
    }
}
