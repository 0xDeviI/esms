package com.arminapps.esms.utils;

import java.util.Random;

public class AvatarColorGenerator {

    // Predefined set of attractive darker colors (as hex strings)
    private static final int[] DARK_COLORS = {
            0x1E3A8A, // Dark Blue
            0x065F46, // Dark Emerald
            0x7C2D12, // Dark Brown
            0x374151, // Dark Gray
            0x6D28D9, // Dark Purple
            0xBE185D, // Dark Pink
            0x0F766E, // Dark Teal
            0x713F12, // Dark Bronze
            0x1E40AF, // Sapphire Blue
            0x7E22CE, // Dark Violet
            0x0369A1, // Dark Sky Blue
            0xA21CAF, // Dark Magenta
            0x0D9488, // Dark Cyan
            0xBE123C, // Dark Crimson
            0x4338CA, // Dark Indigo
            0xB45309, // Dark Amber
            0x15803D, // Dark Green
            0x991B1B, // Dark Red
    };

    // Generate consistent color based on name hash
    public static int getColorFromName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getRandomDarkColor();
        }

        // Clean the name and get hash code
        String cleanName = name.trim().toLowerCase();
        int hashCode = cleanName.hashCode();

        // Use absolute value and modulus to get index
        int index = Math.abs(hashCode) % DARK_COLORS.length;

        return DARK_COLORS[index];
    }

    // Alternative: Generate color by averaging character codes
    public static int getColorFromNameV2(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getRandomDarkColor();
        }

        String cleanName = name.trim().toLowerCase();
        int hash = 0;

        // Create a simple hash from the name
        for (int i = 0; i < cleanName.length(); i++) {
            hash = 31 * hash + cleanName.charAt(i);
        }

        // Generate RGB values with darker ranges (30-180 for each component)
        int r = (Math.abs(hash) % 151) + 30;      // 30-180
        int g = (Math.abs(hash * 7) % 151) + 30;  // 30-180
        int b = (Math.abs(hash * 13) % 151) + 30; // 30-180

        // Ensure it's dark enough by applying additional darkening if needed
        int brightness = (int)(0.299 * r + 0.587 * g + 0.114 * b);
        if (brightness > 100) { // If too bright, make it darker
            float factor = 100.0f / brightness;
            r = (int)(r * factor);
            g = (int)(g * factor);
            b = (int)(b * factor);
        }

        return Integer.parseInt(String.format("#%02X%02X%02X", r, g, b));
    }

    // Completely random dark color generator
    public static int getRandomDarkColor() {
        Random random = new Random();

        // Generate random but dark RGB values (0-180 range for each component)
        int r = random.nextInt(181);       // 0-180
        int g = random.nextInt(181);       // 0-180
        int b = random.nextInt(181);       // 0-180

        // Ensure minimum darkness by setting a max brightness
        int maxComponent = Math.max(r, Math.max(g, b));
        if (maxComponent < 30) {
            // If too dark, brighten a bit
            int increase = 30 - maxComponent;
            r = Math.min(180, r + increase);
            g = Math.min(180, g + increase);
            b = Math.min(180, b + increase);
        }

        return Integer.parseInt(String.format("#%02X%02X%02X", r, g, b));
    }

    // Generate color with hue-based approach
    public static int getColorFromNameHueBased(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getRandomDarkColor();
        }

        String cleanName = name.trim().toLowerCase();
        int hash = cleanName.hashCode();

        // Get hue from hash (0-360 degrees)
        float hue = Math.abs(hash) % 360;

        // Convert HSL to RGB with fixed saturation and lightness for dark colors
        float saturation = 0.7f; // 70% saturation
        float lightness = 0.3f;  // 30% lightness (dark)

        return hslToHex(hue, saturation, lightness);
    }

    // Convert HSL to Hex color
    private static int hslToHex(float h, float s, float l) {
        h /= 360f;

        float q = l < 0.5 ? l * (1 + s) : l + s - l * s;
        float p = 2 * l - q;

        float r = hueToRgb(p, q, h + 1f/3);
        float g = hueToRgb(p, q, h);
        float b = hueToRgb(p, q, h - 1f/3);

        return Integer.parseInt(
                String.format("#%02X%02X%02X",
                        Math.round(r * 255),
                        Math.round(g * 255),
                        Math.round(b * 255))
        );
    }

    private static float hueToRgb(float p, float q, float t) {
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (t < 1f/6) return p + (q - p) * 6 * t;
        if (t < 1f/2) return q;
        if (t < 2f/3) return p + (q - p) * (2f/3 - t) * 6;
        return p;
    }
}
