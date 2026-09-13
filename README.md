# Minecraft AI Player

AI-controlled Minecraft player mod with Comet integration, natural language commands, and human-like behavior.

## 功能特性

- 🤖 **自然语言指令** - 用中文或英文控制AI玩家
- 🎮 **拟人化行为** - 真实玩家的反应延迟、偶发失误、习惯模式
- 💬 **聊天系统** - AI可以与你对话并回应指令
- 🧠 **学习能力** - 越玩越聪明，记住成功经验
- ☄️ **彗星集成** - 支持Comet插件的行为录制和回放
- 🔌 **任意服务器** - 支持所有Fabric服务器
- 🌙 **昼夜感知** - 夜晚更安全的行为模式

## 安装

### 要求
- Minecraft 26.1.2
- Fabric Loader 0.16.0+
- Fabric API
- Java 21+

### 步骤
1. 下载最新发布的 `.jar` 文件
2. 放入 `mods` 文件夹
3. 启动游戏
4. 使用聊天指令控制AI

## 使用

### 聊天指令
```
/ai start      - 启动AI
/ai stop       - 停止AI
/ai status     - 查看状态
/ai learn      - 记录学习经验
/ai chat "你好" - 与AI聊天
/ai task 挖矿  - 执行任务
```

### 自然语言命令
- "帮我挖矿"
- "建造高速公路"
- "保护我"
- "回家"
- "去主世界建路"

### 彗星插件集成
如果服务器安装了Comet插件，AI会自动：
- 录制你的操作习惯
- 回放行为序列
- 优化决策路径

## 配置

配置文件位于 `config/aiplayer.json`:

```json
{
  "enableCometIntegration": true,
  "enableChat": true,
  "humanizationEnabled": true,
  "reactionDelayMin": 150,
  "reactionDelayMax": 300,
  "mistakeChance": 0.03,
  "maxCPS": 10
}
```

## 开源协议

MIT License - 见 [LICENSE](LICENSE) 文件

## 开发

```bash
./gradlew build
```

构建输出位于 `build/libs/`

## 贡献

欢迎提交PR和Issue！

## 致谢

- [Fabric API](https://fabricmc.net/)
- [Comet](https://github.com/SkycryptMC/Comet)
