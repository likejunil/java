package june1.aop.proxy.cglib;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Concrete {

    public void call() {
        log.info("Concrete 가 호출되었습니다.");
    }
}
