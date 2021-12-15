package june1.study.aop.strategy;

public class StrategyIns1 implements Strategy {
    @Override
    public void call() {
        System.out.println("111 실행합니다.");
    }
}
