# Minecraft AI Player

AI-controlled Minecraft player mod with Meteor Client (彗星) integration, natural language commands, and human-like behavior.

> **注意**: 本模组需要服务器安装 [Meteor Client](https://www.meteorclient.com/) 才能使用彗星集成功能。

## 功能特性

- 🤖 **自然语言指令** - 用中文或英文控制AI玩家
- 🎮 **拟人化行为** - 真实玩家的反应延迟、偶发失误、习惯模式
- 💬 **聊天系统** - AI可以与你对话并回应指令
- 🧠 **学习能力** - 越玩越聪明，记住成功经验
- ☄️ **彗星集成** - 支持Meteor Client的模块控制和Baritone路径搜索
- 🔌 **任意服务器** - 支持所有Fabric服务器
- 🌙 **昼夜感知** - 夜晚更安全的行为模式

## 安装

### 要求
- Minecraft 1.21
- Fabric Loader 0.16.0+
- Fabric API
- Java 21+
- **Meteor Client 1.21** (推荐安装: [meteorclient.com](https://www.meteorclient.com/))

### 步骤
1. 下载 Meteor Client [26.2](https://www.meteorclient.com/) 并放入 `mods` 文件夹
2. 下载本 mod 最新发布的 `.jar` 文件
3. 放入 `mods` 文件夹
4. 启动游戏
5. 使用聊天指令控制AI

## 使用

### AI 聊天指令
```
/ai start      - 启动AI
/ai stop       - 停止AI
/ai status     - 查看状态
/ai learn      - 记录学习经验
/ai chat "你好" - 与AI聊天
/ai task 挖矿  - 执行任务
```

### 彗星/Meteor 指令
```
/mc enable scaffold    - 启用自动建筑
/mc disable scaffold   - 禁用自动建筑
/mc list               - 列出所有模块
/mc baritone goto 100 64 -200  - 设置Baritone目标
/mc baritone stop      - 停止Baritone
```

### 自然语言命令
- "帮我挖矿"
- "建造高速公路"
- "保护我"
- "回家"
- "去主世界建路"

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
- [Meteor Client](https://www.meteorclient.com/)
- [Baritone](https://github.com/cabaletta/baritone)
