package june1.aop.trace;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Context<T> {

    private final LogTrace logTrace;

    public T execute(String title, Strategy<T> strategy) {
        TraceStatus status = null;
        try {
            status = logTrace.begin(title);
            T result = strategy.call();
            logTrace.end(status);
            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }
}
