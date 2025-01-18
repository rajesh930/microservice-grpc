package example.co.ontic.ms.client;

/**
 * @author rajesh
 * @since 17/01/25 22:39
 */
public class CustomObject {
    private String value1;
    private String value2;

    public CustomObject() {
    }

    public CustomObject(String value1, String value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    @Override
    public String toString() {
        return "CustomObject{" +
                "value1='" + value1 + '\'' +
                ", value2='" + value2 + '\'' +
                '}';
    }

}
