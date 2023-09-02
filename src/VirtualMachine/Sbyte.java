package VirtualMachine;

public class Sbyte {
    private byte value;

    public byte getValue() {
        return value;
    }

    public String getStringValue() {
        return String.format("%8s", Integer.toBinaryString(value & 0xFF)).replace(' ', '0');
    }

    public void setValue(String newValue) {
        if (newValue.length() <= 8) {
            value = (byte) Integer.parseInt(newValue, 2);
        } else {
            throw new IllegalArgumentException("Value is too big");
        }
    }

    public void setValue(byte value) {
        this.value = value;
    }
}
