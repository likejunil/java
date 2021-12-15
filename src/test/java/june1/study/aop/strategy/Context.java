package june1.study.aop.strategy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Builder
public class Context {

    private String name;
    private Strategy strategy;

    public void execute() {
        long s = System.currentTimeMillis();
        strategy.call();
        long e = System.currentTimeMillis();
        log.info("{} 을 실행하는데 {} 밀리초가 소요되었습니다.", name, e - s);
    }

    public void run(Strategy strategy) {
        long s = System.currentTimeMillis();
        strategy.call();
        long e = System.currentTimeMillis();
        log.info("{} 밀리초가 소요되었습니다.", e - s);
    }
}
