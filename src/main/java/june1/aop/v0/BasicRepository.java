package june1.aop.v0;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BasicRepository {

    protected final static String title = "BasicRepository.save()";

    public void save(String hello) {
        body(hello);
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
