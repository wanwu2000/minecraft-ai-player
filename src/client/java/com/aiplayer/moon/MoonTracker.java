package com.aiplayer.moon;

import com.aiplayer.AiPlayerMod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoonTracker {
    private static final Logger LOGGER = LoggerFactory.getLogger("MoonTracker");

    public String getCurrentPhase() {
        long timeOfDay = AiPlayerMod.getInstance().getClient().world.getTimeOfDay();
        int phase = (int) ((timeOfDay / 24000) % 4);
        return switch (phase) {
            case 0 -> "新月";
            case 1 -> "盈凸月";
            case 2 -> "满月";
            case 3 -> "亏凸月";
            default -> "新月";
        };
    }
}
