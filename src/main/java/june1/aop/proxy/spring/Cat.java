package june1.aop.proxy.spring;

public class Cat implements Animal {

    @Override
    public String makeSound() {
        return "냐옹";
    }

    @Override
    public String fly() {
        return "나는 하늘을 날 수 없습니다.";
    }
}
