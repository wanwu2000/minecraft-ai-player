package com.aiplayer.comet;

import com.aiplayer.AiPlayerMod;
import com.aiplayer.config.ConfigManager;

public class CometBridge {
    private boolean cometAvailable = false;

    public CometBridge() { checkAvailability(); }

    private void checkAvailability() {
        try {
            Class.forName("io.github.skycrypt.comet.api.CometAPI");
            cometAvailable = true;
            AiPlayerMod.LOGGER.info("Comet plugin detected and integrated");
        } catch (ClassNotFoundException e) {
            AiPlayerMod.LOGGER.info("Comet plugin not found, running in standalone mode");
        }
    }

    public boolean isAvailable() {
        return cometAvailable && AiPlayerMod.getInstance().getConfigManager().enableCometIntegration();
    }

    public void startRecording(String name) { if (!isAvailable()) return; }
    public void stopRecording() { if (!isAvailable()) return; }
    public void playBehavior(String name) { if (!isAvailable()) return; }
}
