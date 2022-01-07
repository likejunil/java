package june1.batch.execute;

import june1.batch.launcher.Launcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Map;

@Slf4j
//@Component
@RequiredArgsConstructor
public class QuartzCaller {

    private final Launcher launcher;

    @PostConstruct
    void start() {

        String name = "jobByQuartz";
        Map<String, Object> params = Map.of(
                "date", new Date(),
                "id", "june1",
                "age", 48L,
                "height", 175.6);

        log.info("111... 쿼츠에서 배치({})를 호출합니다.", name);
        launcher.executeBatch(name, params);
        log.info("555... 배치 호출을 마칩니다.");
    }
}
