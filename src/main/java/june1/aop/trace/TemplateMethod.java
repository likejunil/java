package june1.aop.trace;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class TemplateMethod<T> {

    private final LogTrace logTrace;

    public T execute(String message) {
        TraceStatus status = null;
        try {
            status = logTrace.begin(message);
            T result = call();
            logTrace.end(status);
            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }

    public abstract T call();
}
