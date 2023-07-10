package Processor;

public class Register {
    private String value;
    private int size;

    public Register(String value, int size) {
        this.value = value;
        this.size = size;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String newValue) {
        if (newValue.length() <= size) {
            value = newValue;
        } else {
            System.out.println("Error: Register size exceeded");
            System.exit(0);
        }
    }
}
