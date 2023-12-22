package org.simuladorso.Utils;
public class RGBtoHEX {
    public static String rgbToHex(int red, int green, int blue) {
        // Convert each RGB component to HEX and concatenate them
        String hexRed = Integer.toHexString(red);
        String hexGreen = Integer.toHexString(green);
        String hexBlue = Integer.toHexString(blue);

        // Ensure that each component has two characters in the HEX representation
        if (hexRed.length() == 1) {
            hexRed = "0" + hexRed;
        }
        if (hexGreen.length() == 1) {
            hexGreen = "0" + hexGreen;
        }
        if (hexBlue.length() == 1) {
            hexBlue = "0" + hexBlue;
        }

        // Combine the HEX components and return the result
        return "#" + hexRed + hexGreen + hexBlue;
    }
}
