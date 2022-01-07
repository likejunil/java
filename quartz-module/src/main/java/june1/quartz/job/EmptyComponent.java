package june1.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmptyComponent {

    public void ping() {
        log.info("ping");
    }
}
