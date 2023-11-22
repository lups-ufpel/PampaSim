<<<<<<<< HEAD:app/src/main/java/simuladorso/Vm/Sbyte.java
package simuladorso.Vm;
========
package org.simuladorso.VirtualMachine;
>>>>>>>> jansoares:src/main/java/org/simuladorso/VirtualMachine/Sbyte.java

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
