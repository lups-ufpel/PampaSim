package org.pampasim.VirtualMachine;

public class Sbyte {
    private byte value;

    public byte getByte() {
        return value;
    }

    public String getStringByte() {
        return String.format("%8s", Integer.toBinaryString(value & 0xFF)).replace(' ', '0');
    }

    public void setByte(String newValue) {
        if (newValue.length() <= 8) {
            value = (byte) Integer.parseInt(newValue, 2);
        } else {
            throw new IllegalArgumentException("Value is too big");
        }
    }

    public void setByte(byte value) {
        this.value = value;
    }
}
