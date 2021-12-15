package june1.study.aop.template_method;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class TemplateMethod {

    public void execute() {
        long s = System.currentTimeMillis();
        call();
        long e = System.currentTimeMillis();
        log.info("{} 밀리초의 시간이 경과되었습니다.", e - s);
    }

    public abstract void call();
}
