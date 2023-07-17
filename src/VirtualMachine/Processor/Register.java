package VirtualMachine.Processor;

public class Register {
    private static final int REG_SIZE = 32;
    private int value;

    public Register() {
        value = 0;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        if (value <= Math.pow(2, REG_SIZE)) {
            this.value = value;
        } else {
            throw new IllegalArgumentException(
                    "Error: Register value overflow, current size is " + Math.pow(2, REG_SIZE) + " and value is"
                            + Integer.toString(value));
        }
    }

    public String getStringValue() {
        return Integer.toString(value);
    }

    public void setValue(String value) {
        setValue(Integer.parseInt(value));
    }
}
