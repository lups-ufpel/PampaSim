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


}
