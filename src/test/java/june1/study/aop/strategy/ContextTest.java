package june1.study.aop.strategy;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class ContextTest {

    @Test
    void test_v1() {
        StrategyIns1 client1 = new StrategyIns1();
        Context context1 = Context.builder()
                .name("준일")
                .strategy(client1)
                .build();
        context1.execute();
    }

    @Test
    void test_v2() {
        Strategy client1 = new Strategy() {
            @Override
            public void call() {
                System.out.println("암묵적 111 실행");
            }
        };

        Context context1 = Context.builder()
                .name("준일")
                .strategy(client1)
                .build();
        context1.execute();
    }

    @Test
    void test_v3() {
        Context context1 = Context.builder()
                .name("준일")
                .strategy(() -> System.out.println("람다로 111 실행"))
                .build();
        context1.execute();
    }

    @Test
    void test_v4() {
        //Context 와 Strategy 를 미리 조립 후 실행하지 않는다.
        //Context 를 실행하는 시점에 Strategy 를 생성하여 인자로 넘긴다.
        //Context 를 하나만 생성해도 된다.
        Context context = Context.builder().build();
        context.run(() -> System.out.println("람다로 함수에 주입하기 - 111"));
        context.run(() -> System.out.println("람다로 함수에 주입하기 - 222"));
    }
}
