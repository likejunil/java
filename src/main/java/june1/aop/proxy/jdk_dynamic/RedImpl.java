package june1.aop.proxy.jdk_dynamic;

public class RedImpl implements ColorInterface {

    @Override
    public String color(String color) {
        return "red and " + color;
    }
}
