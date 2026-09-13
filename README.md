# Minecraft AI Player

AI-controlled Minecraft player mod with human-like behavior and natural language commands.

## Features

- 🤖 **Natural Language Control** - Control AI with Chinese/English commands
- 🎮 **Human-like Behavior** - Reaction delays, mouse movements, occasional mistakes
- 💬 **Chat System** - AI can chat and respond to players
- 🧠 **Learning System** - Remembers successful experiences
- ☄️ **Meteor Client Integration** - Support for Baritone pathfinding
- 🔌 **Pure Client-side** - Works on any Fabric server
- 🌙 **Day/Night Awareness** - Safer behavior at night

## Installation

### Requirements
- Minecraft 26.1.2
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
/ai learn      - Record learning experience
/ai task 挖矿  - Execute specific task
/mc enable scaffold    - Enable Meteor module
/mc disable scaffold   - Disable Meteor module
/mc list               - List all modules
/mc baritone goto 100 64 200 - Set Baritone target
```

### Natural Language Commands
- "帮我挖矿" - Mine resources
- "建造高速公路" - Build highway with Meteor
- "保护我" - Protect mode
- "回家" - Return to base

## Configuration

Config file: `config/aiplayer/config.json`

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

## Building from Source

```bash
./gradlew build
```

Output: `build/libs/minecraft-ai-player-mc26.1.2-1.0.0.jar`

## License

MIT License - See [LICENSE](LICENSE) file

## Credits

- [Fabric MC](https://fabricmc.net/)
- [Meteor Client](https://www.meteorclient.com/)
