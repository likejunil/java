package june1.study.aop;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class TemplateMethodTest {

    private void logic_1() {
        long start = System.currentTimeMillis();

        //-------------------------------------------
        // 핵심 영역 #1
        System.out.println("핵심 1번");
        //-------------------------------------------

        long end = System.currentTimeMillis();
        log.info("{} 밀리초 시간이 소요되었습니다.", end - start);
    }

    private void logic_2() {
        long start = System.currentTimeMillis();

        //-------------------------------------------
        // 핵심 영역 #2
        System.out.println("핵심 2번");
        //-------------------------------------------

        long end = System.currentTimeMillis();
        log.info("{} 밀리초 시간이 소요되었습니다.", end - start);
    }

    @Test
    void test_1() {
        logic_1();
        logic_2();
    }

    @Test
    void test_2() {
        //Logic_1, Logic_2 클래스를 따로 생성
        //필요한 만큼 생성해야 하는 불편함
        Logic_1 logic_1 = new Logic_1();
        Logic_2 logic_2 = new Logic_2();
        logic_1.execute();
        logic_2.execute();

        //익명 클래스를 사용하여 해결할 수 있다.
        AbstractTemplateMethod logic_3 = new AbstractTemplateMethod() {
            @Override
            public void call() {
                System.out.println("로직 3을 실행합니다.");
            }
        };
        logic_3.execute();
    }
}
