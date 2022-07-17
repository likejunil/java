package june1.aop.v3;

import june1.aop.trace.Context;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StrategyRepository {

    private final static String title = "TemplateMethodRepository.save()";
    private final Context<Void> context;

    public void save(String hello) {
        context.execute(title, () -> {
            body(hello);
            return null;
        });
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
