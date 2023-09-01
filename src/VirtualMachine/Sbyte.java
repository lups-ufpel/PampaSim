package VirtualMachine;

public class Sbyte {
    private String value;
    private int size;

    public Sbyte(String value, int size) {
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
            throw new IllegalArgumentException("Value is too big");
        }
    }

    public void setValue(int newValue){
        String binary = Integer.toBinaryString(newValue);
        if (binary.length() <= size) {
            value = binary;
        } else {
            throw new IllegalArgumentException("Value is too big");
        }
    }
}
