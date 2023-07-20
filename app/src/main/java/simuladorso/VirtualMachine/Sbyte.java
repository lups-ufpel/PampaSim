package simuladorso.VirtualMachine;

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
        this.setValue(Integer.toString(newValue));
    }
}
