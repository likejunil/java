package june1.aop.dynamic_proxy;

public class BlueImpl implements ColorInterface {

    @Override
    public String color(String color) {
        return "blue and " + color;
    }
}
