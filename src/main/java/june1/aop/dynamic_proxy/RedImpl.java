package june1.aop.dynamic_proxy;

public class RedImpl implements ColorInterface {

    @Override
    public String color(String color) {
        return "red and " + color;
    }
}
