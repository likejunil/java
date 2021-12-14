package june1.study.aop;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Logic_1 extends AbstractTemplateMethod {

    @Override
    public void call() {
        System.out.println("로직 1을 실행합니다.");
    }
}
