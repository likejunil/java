package june1.aop.v2;

import june1.aop.trace.LogTrace;
import june1.aop.trace.TemplateMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TemplateMethodRepository {

    private final static String title = "TemplateMethodRepository.save()";
    private final LogTrace trace;

    public void save(String hello) {
        TemplateMethod<Void> template = new TemplateMethod<>(trace) {
            @Override
            public Void call() {
                body(hello);
                return null;
            }
        };

        template.execute(title);
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
