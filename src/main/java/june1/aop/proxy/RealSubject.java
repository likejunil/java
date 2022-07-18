package june1.aop.proxy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RealSubject implements Subject {

    @Override
    public String operation() {
        log.info("실제 객체를 호출했음..");
        sleep(1);
        return "money";
    }

    private void sleep(int i) {
        try {
            Thread.sleep(i * 1_000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
