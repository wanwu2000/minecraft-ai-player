# Minecraft AI Player

AI-controlled Minecraft player with human-like behavior, natural language commands, and Meteor Client integration.

## Features

- 🤖 **Natural Language Control** - Control AI with Chinese/English commands
- 🎮 **Human-like Behavior** - Reaction delays, mouse movements, occasional mistakes
- 💬 **Chat System** - AI can chat and respond to players
- 🧠 **Learning System** - Q-Learning based improvement over time
- ☄️ **Meteor Client Integration** - Support for Baritone pathfinding and auto-build
- 🔌 **Multi-version Support** - Works with Minecraft 1.20.1 to 1.21+
- 🌙 **Day/Night Awareness** - Safer behavior at night

## Installation

### Requirements
- Minecraft 1.21 or later
- Fabric Loader 0.16.0+
- Fabric API
- Java 21+
- **Meteor Client** (optional but recommended)

### Steps
1. Download the latest `.jar` from releases
2. Place in your `mods` folder
3. Launch Minecraft with Fabric profile
4. Use chat commands to control AI

## Usage

### Chat Commands
```
/ai start      - Start AI control
/ai stop       - Stop AI control
/ai status     - Show AI status
/ai chat "你好" - Chat with AI
/ai learn      - Learn from current situation
/ai task 挖矿  - Execute specific task
/exec "用彗星搭建高速公路" - Natural language command
/mc enable scaffold - Enable Meteor module
/mc disable no_slow - Disable Meteor module
/mc baritone goto 100 64 200 - Send Baritone command
```

### Natural Language Commands
- "帮我挖矿" - Mine resources
- "建造高速公路" - Build highway with Meteor
- "保护我" - Protect mode
- "回家" - Return to base
- "去主世界建路" - Navigate to nether_portal and build road

### Meteor Client Integration
If Meteor Client is installed, AI can:
- Use Baritone for pathfinding (`#goto`, `#stop`)
- Enable auto-build modules (`/mc enable scaffold`)
- Control other Meteor modules via `/mc` commands

## Configuration

Config file: `config/aiplayer.json`

```json
{
  "enableCometIntegration": true,
  "enableChat": true,
  "humanizationEnabled": true,
  "reactionDelayMin": 150,
  "reactionDelayMax": 300,
  "mistakeChance": 0.03,
  "maxCPS": 10,
  "learningEnabled": true,
  "memoryCapacity": 1000
}
```

## Building from Source

```bash
./gradlew build
```

Output: `build/libs/minecraft-ai-player-mc1.21-1.1.0.jar`

## Development

This project uses a layered architecture:

```
com.aiplayer
├── api/              # Version-independent interfaces
├── impl/             # Version-specific implementations
│   ├── v1_21/        # Minecraft 1.21+
│   └── v1_26_1/      # Legacy versions
├── ai/               # Behavior tree system
├── chat/             # Chat system
├── command/          # Command registry
├── comet/            # Meteor Client bridge
├── config/           # Configuration manager
├── human/            # Humanization
├── learning/         # Learning system
└── util/             # Utilities (VersionHelper)
```

## License

MIT License - See [LICENSE](LICENSE) file

## Credits

- [Fabric MC](https://fabricmc.net/)
- [Meteor Client](https://www.meteorclient.com/)
- [Baritone](https://github.com/cabaletta/baritone)
