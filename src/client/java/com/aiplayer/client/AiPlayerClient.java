package com.aiplayer.client;

import com.aiplayer.chat.ChatSystem;
import com.aiplayer.command.CommandRegistry;
import com.aiplayer.comet.CometBridge;
import com.aiplayer.human.Humanizer;
import com.aiplayer.learning.LearningSystem;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AiPlayerClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("AIPlayerClient");
    
    private static AiPlayerClient instance;
    private ChatSystem chatSystem;
    private CommandRegistry commandRegistry;
    private CometBridge cometBridge;
    private Humanizer humanizer;
    private LearningSystem learningSystem;
    
    private boolean isRunning = false;

    @Override
    public void onInitializeClient() {
        instance = this;
        
        // 初始化各子系统
        chatSystem = new ChatSystem();
        commandRegistry = new CommandRegistry();
        cometBridge = new CometBridge();
        humanizer = new Humanizer();
        learningSystem = new LearningSystem();
        
        // 注册聊天命令
        commandRegistry.registerCommands();
        
        // 加载记忆数据
        learningSystem.load();
        chatSystem.loadPreferences();
        
        LOGGER.info("AI Player client initialized - Pure Client Version");
    }

    public static AiPlayerClient getInstance() { 
        return instance; 
    }
    
    public ChatSystem getChatSystem() { return chatSystem; }
    public CommandRegistry getCommandRegistry() { return commandRegistry; }
    public CometBridge getCometBridge() { return cometBridge; }
    public Humanizer getHumanizer() { return humanizer; }
    public LearningSystem getLearningSystem() { return learningSystem; }
    public boolean isRunning() { return isRunning; }
    
    public void startAI() {
        isRunning = true;
        chatSystem.updateMood(10);
        LOGGER.info("AI player started");
    }
    
    public void stopAI() {
        isRunning = false;
        chatSystem.clearCurrentTask();
        cometBridge.stopBaritone();
        LOGGER.info("AI player stopped");
    }
}
