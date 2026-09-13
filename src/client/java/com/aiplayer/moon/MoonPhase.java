package com.aiplayer.moon;

public class MoonPhase {
    private static final String[] PHASES = {"新月", "盈凸月", "满月", "亏凸月"};

    public static String getPhase(long worldTime) {
        int phase = (int) ((worldTime / 24000) % 4);
        return PHASES[phase];
    }

    public static String getEmoji(long worldTime) {
        int phase = (int) ((worldTime / 24000) % 4);
        return switch (phase) {
            case 0 -> "🌑";
            case 1 -> "🌓";
            case 2 -> "🌕";
            case 3 -> "🌗";
            default -> "🌑";
        };
    }
}
