package june1.aop.v1;

import june1.aop.trace.LogTrace;
import june1.aop.trace.TraceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HelloService {

    private final static String title = "HelloService.save()";
    private final HelloRepository helloRepository;
    private final LogTrace trace;

    public void save(String hello) {
        TraceStatus status = null;
        try {
            status = trace.begin(title);
            helloRepository.save(hello);
            trace.end(status);
        } catch (Exception e) {
            trace.exception(status, e);
            throw e;
        }
    }
}
