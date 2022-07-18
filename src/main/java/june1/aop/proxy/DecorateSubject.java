package june1.aop.proxy;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DecorateSubject implements Subject {

    private final Subject subject;

    @Override
    public String operation() {
        String ret = subject.operation();
        ret = "<<< 드디어 결과를 받았습니다. [" + ret + "] >>>";
        return ret;
    }
}
