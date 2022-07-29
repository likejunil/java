package june1.aop.proxy.jdk_dynamic;

public class BlueImpl implements ColorInterface {

    @Override
    public String color(String color) {
        return "blue and " + color;
    }
}
