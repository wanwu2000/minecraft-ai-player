# Minecraft AI Player - Project Structure

## 模块概览

```
minecraft-ai-player/
├── src/main/java/com/aiplayer/
│   ├── AiPlayerMod.java              # 主入口
│   ├── api/                          # 抽象API接口
│   │   ├── MovementApi.java
│   │   ├── PerceptionApi.java
│   │   ├── ActionApi.java
│   │   └── ChatApi.java
│   ├── impl/v1_26_1/                 # 26.1.2版本实现
│   │   ├── MovementApiImpl.java
│   │   ├── PerceptionApiImpl.java
│   │   ├── ActionApiImpl.java
│   │   ├── ChatApiImpl.java
│   │   └── ApiFactory.java
│   ├── comet/                        # 彗星插件集成
│   │   └── CometBridge.java
│   ├── chat/                         # 聊天系统
│   │   └── ChatSystem.java
│   ├── command/                      # 命令注册
│   │   └── CommandRegistry.java
│   ├── human/                        # 拟人化系统
│   │   └── Humanizer.java
│   ├── learning/                     # 学习系统
│   │   └── LearningSystem.java
│   ├── ai/                           # AI核心
│   │   ├── AiController.java
│   │   └── BehaviorNode.java
│   └── config/                       # 配置管理
│       └── ConfigManager.java
│
├── src/client/java/com/aiplayer/
│   ├── AiPlayerClient.java           # 客户端入口
│   └── moon/                         # 月亮追踪（装饰）
│       ├── MoonTracker.java
│       └── MoonPhase.java
│
├── src/main/resources/
│   ├── fabric.mod.json
│   ├── aiplayer.mixins.json
│   ├── behaviors/
│   │   └── default_behaviors.json
│   └── chat_templates/
│       └── responses.json
│
├── src/client/resources/
│   ├── fabric.mod.json
│   └── aiplayer.client.mixins.json
│
├── build.gradle
├── gradle.properties
├── settings.gradle
├── README.md
├── LICENSE
└── .github/workflows/build.yml
```

## 快速开始

### 构建
```bash
./gradlew build
```

### 运行测试环境
```bash
./gradlew runClient
```

### 发布到本地Maven
```bash
./gradlew publishToMavenLocal
```

## 依赖

- Minecraft 26.1.2
- Fabric Loader 0.16.0+
- Fabric API 0.102.0+
- Java 21+
- Comet (可选)

## 下一步

1. 实现具体行为逻辑
2. 添加更多AI决策树
3. 完善聊天响应
4. 集成Comet完整API
