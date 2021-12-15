package june1.study.aop.template_method;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TemplateMethodIns1 extends TemplateMethod {

    @Override
    public void call() {
        System.out.println("로직 1을 실행합니다.");
    }
}
