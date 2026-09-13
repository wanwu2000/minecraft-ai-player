package com.aiplayer.client;

import com.aiplayer.chat.ChatSystem;
import com.aiplayer.command.CommandRegistry;
import com.aiplayer.comet.CometBridge;
import com.aiplayer.human.Humanizer;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AiPlayerClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("AIPlayerClient");
    private ChatSystem chatSystem;
    private CommandRegistry commandRegistry;
    private CometBridge cometBridge;
    private Humanizer humanizer;

    @Override
    public void onInitializeClient() {
        chatSystem = new ChatSystem();
        commandRegistry = new CommandRegistry();
        cometBridge = new CometBridge();
        humanizer = new Humanizer();
        commandRegistry.registerCommands();
        LOGGER.info("AI Player client initialized");
    }

    public ChatSystem getChatSystem() { return chatSystem; }
    public CommandRegistry getCommandRegistry() { return commandRegistry; }
    public CometBridge getCometBridge() { return cometBridge; }
    public Humanizer getHumanizer() { return humanizer; }
}
