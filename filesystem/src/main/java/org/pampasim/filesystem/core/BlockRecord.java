package org.pampasim.filesystem.core;

import javafx.scene.paint.Color;
import org.pampasim.filesystem.core.BlockType;

public record BlockRecord(
        BlockType type,
        String userString,
        int userInt
) {

    public static Color getCorrespondingColor(BlockRecord blockRecord) {
            return switch (blockRecord.type()) {
                case FILE, DIRECTORY ->
                    colorFromString(blockRecord.userString());

                case INODE_INDIRECT_BLOCK ->
                    colorFromInt(blockRecord.userInt());

                default -> BlockType.getCorrespondingColor(blockRecord.type());
            };

    }

    private static Color colorFromString(String s) {
        if (s == null || s.isBlank()) {
            return Color.web("#adb5bd"); // neutral fallback
        }
    
        int hash = Math.abs(s.hashCode());
    
        double hue = hash % 360;        // spread across color wheel
        double saturation = 0.55;       // controlled, not chaotic
        double brightness = 0.85;       // readable on light bg
    
        return Color.hsb(hue, saturation, brightness);
    }

    private static Color colorFromInt(int value) {
        int hash = Math.abs(Integer.hashCode(value));
    
        double minHue = 30;   // orange
        double maxHue = 60;   // yellow
        double hueRange = maxHue - minHue;
    
        double hue = minHue + (hash % (int) hueRange);
    
        double saturation = 0.65;
        double brightness = 0.85;
    
        return Color.hsb(hue, saturation, brightness);
    }


}
