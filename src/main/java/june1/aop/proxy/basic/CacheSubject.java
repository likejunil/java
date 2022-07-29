package june1.aop.proxy.basic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CacheSubject implements Subject {

    private final Subject subject;
    private String cached;

    @Override
    public String operation() {
        if (cached == null) {
            cached = subject.operation();
        } else {
            log.info("캐쉬값을 읽었음..");
        }

        return cached;
    }
}
