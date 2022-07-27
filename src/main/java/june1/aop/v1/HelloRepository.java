package june1.aop.v1;

import june1.aop.trace.LogTrace;
import june1.aop.trace.TraceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HelloRepository {

    private final static String title = "HelloRepository.save()";
    private final LogTrace trace;

    public void save(String hello) {
        TraceStatus status = null;
        try {
            status = trace.begin(title);

            // --------------------------------------
            body(hello);
            // --------------------------------------

            trace.end(status);
        } catch (Exception e) {
            trace.exception(status, e);
            throw e;
        }
    }

    private void body(String hello) {
        if (hello.equals("ex")) {
            throw new IllegalStateException("예외 발생");
        }

        doSomething();
    }

    private void doSomething() {
        try {
            Thread.sleep(1_000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
