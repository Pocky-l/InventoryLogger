# 📦 Inventory Backups

<div align="center">

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-green?style=for-the-badge&logo=minecraft)
![NeoForge](https://img.shields.io/badge/NeoForge-52.0.19-orange?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-21-blue?style=for-the-badge&logo=openjdk)
![Localization](https://img.shields.io/badge/Languages-EN%20%7C%20RU-blue?style=for-the-badge)
![License](https://img.shields.io/badge/License-All%20Rights%20Reserved-red?style=for-the-badge)

**A powerful Minecraft NeoForge mod for automatic player inventory backups and restoration**

[Features](#-features) • [Installation](#-installation) • [Commands](#-commands) • [Configuration](#-configuration) • [Building](#-building)

</div>

---

## 🌟 Features

### 🔄 Automatic Backups
- **⏰ Periodic Saves** - Automatically saves inventory every 10 minutes
- **💀 Death Protection** - Creates backup when player dies
- **➡️ Join/Quit Saves** - Saves on player login and logout
- **🧹 Smart Cleanup** - Automatically deletes backups older than 7 days

### 🎯 Smart Backup System
- **📊 Deduplication** - Only saves when inventory changes
- **🚫 Empty Check** - Never saves empty inventories
- **🗂️ Organized Storage** - Backups stored by player UUID

### 💎 Interactive Chat Interface
Beautiful, clickable buttons with pagination and quick filters:

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
         📦 BACKUP LIST
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Player: Steve
Total backups: 47
Page: 1/5

Quick filters: [📅 Today] [Yesterday] [This Month] [All]

1. 💀 2025-01-15-18-30-45 [Death] [👁] [↻] [📥]
2. ⏰ 2025-01-15-18-20-00 [Auto] [👁] [↻] [📥]
3. ➡ 2025-01-15-18-00-15 [Join] [👁] [↻] [📥]
4. ⬅ 2025-01-15-17-45-30 [Quit] [👁] [↻] [📥]
... 6 more items ...
10. ⏰ 2025-01-15-14-30-00 [Auto] [👁] [↻] [📥]

[◀ Previous | Page: 1/5 | Next ▶]
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

**New Features:**
- 📄 **Pagination** - 10 backups per page with Previous/Next navigation
- 🔍 **Quick Filters** - One-click filters: Today, Yesterday, This Month, All
- 🌐 **Full Localization** - Complete Russian and English translations
- 🎨 **Color-coded UI** - Easy-to-read colored text and buttons

### 🎮 Advanced Features
- **👁️ Preview Mode** - View backups in read-only chest GUI
- **↻ Restore** - Load backup directly to player
- **📥 Copy to Self** - Copy backup items to your own inventory
- **🔍 Smart Filtering** - Search backups by date/time with quick filter buttons
- **📄 Pagination System** - Navigate through large backup lists (10 per page)
- **🌐 Localization** - Fully translated interface (English & Russian)

---

## 📥 Installation

1. **Download** the latest release
2. **Install** [Minecraft NeoForge 1.21.1](https://files.minecraftforge.net/)
3. **Place** the mod file in your `mods` folder
4. **Launch** Minecraft with NeoForge profile

---

## 🎮 Commands

> **🔒 Security:** All commands require operator permission (level 2) - only server admins can use them

### 📋 Main Commands

| Command | Description |
|---------|-------------|
| `/inventory` | Shows help with all available commands |
| `/inventory list <player> [filter] [page]` | Lists backups with pagination and quick filters |
| `/inventory view <player> <backup>` | Opens read-only preview of backup |
| `/inventory set <player> <backup>` | Restores backup to player |
| `/inventory copy <player> <backup>` | Copies backup items to your inventory |

### 📖 Examples

```bash
# List all backups for player "Steve" (first page)
/inventory list Steve

# List backups with pagination - page 2
/inventory list Steve "" 2

# Filter backups from March 2023
/inventory list Steve 2023-03

# Filter backups from specific day
/inventory list Steve 2025-01-15

# Filter backups from specific day, page 2
/inventory list Steve 2025-01-15 2

# View a specific backup
/inventory view Steve 2025-01-15-14-30-45

# Restore backup to player
/inventory set Steve 2025-01-15-14-30-45-death

# Copy backup items to yourself
/inventory copy Steve 2025-01-15-14-30-45
```

### 🚀 Quick Filter Usage

When viewing the backup list, click these buttons for instant filtering:
- **📅 Today** - Show only today's backups
- **Yesterday** - Show yesterday's backups
- **This Month** - Show current month's backups
- **All** - Remove all filters

---

## ⚙️ Configuration

Configuration file: `config/inventory/InventoryBackups.toml`

### 🎛️ Available Settings

```toml
[general]
    # Enable periodic inventory saves
    tickSaveEnabled = true

    # Save interval in seconds (600 = 10 minutes)
    preservationPeriod = 600

    # Save inventory on player death
    deadSaveEnabled = true

    # Save inventory when player joins server
    joinSaveEnabled = true

    # Save inventory when player quits server
    quitSaveEnabled = true

    # Days to keep backups before auto-deletion
    retentionDays = 7
```

### 🔧 Customization

- **Change backup frequency:** Modify `preservationPeriod` (in seconds)
- **Retention period:** Adjust `retentionDays` (1-365 days)
- **Disable specific triggers:** Set any `*SaveEnabled` to `false`

---

## 📂 File Structure

```
InventoryLog/
└── inventory/
    └── <player-uuid>/
        ├── 2025-01-15-18-30-45-death.json
        ├── 2025-01-15-18-20-00.json
        ├── 2025-01-15-18-00-15-join.json
        └── 2025-01-15-17-45-30-quit.json
```

### 🏷️ Backup Types

| Suffix | Icon | Description |
|--------|------|-------------|
| `-death` | 💀 | Saved on player death |
| `-join` | ➡️ | Saved when joining server |
| `-quit` | ⬅️ | Saved when leaving server |
| *(none)* | ⏰ | Automatic periodic save |

---

## 🔨 Building

### Prerequisites
- ✅ Java 21
- ✅ Gradle 8.11.1+

### Build Commands

```bash
# Build the mod
./gradlew build

# Run client for testing
./gradlew runClient

# Run server for testing
./gradlew runServer

# Clean build artifacts
./gradlew clean
```

The compiled JAR will be in `build/libs/`

---

## 🔒 Security & Permissions

### 🛡️ Access Control
All commands require **Operator Level 2** permission:
```bash
# Grant operator status to a player
/op PlayerName

# Check permission level
/op list

# Remove operator status
/deop PlayerName
```

**Permission Levels:**
- Level 0 - Regular player (no access)
- Level 1 - Can bypass spawn protection (no access)
- **Level 2** - Can use cheat commands ✅ **Required for this mod**
- Level 3 - Can kick/ban players
- Level 4 - Can use /op and /stop

### ✅ What Gets Saved
- Main inventory (36 slots)
- Armor slots (4 slots)
- Offhand slot
- All item NBT data (enchantments, custom names, etc.)

### 🛡️ Protection Features
- **Deduplication** - Won't save identical inventories
- **Empty check** - Skips empty inventories
- **Auto-cleanup** - Removes old backups
- **Corruption handling** - Graceful error messages
- **Admin-only access** - Requires operator level 2

---

## 📊 System Requirements

| Component | Requirement |
|-----------|-------------|
| Minecraft | 1.21.1 |
| NeoForge | 52.0.19+ |
| Java | 21 |
| Server RAM | 512MB+ recommended |
| Disk Space | ~10MB per 1000 backups |
| Permissions | Operator Level 2+ |

---

## 🎯 Use Cases

### 💼 Server Administration
- **Rollback griefing** - Restore player inventories after incidents
- **Bug recovery** - Fix inventory losses from bugs/glitches
- **Duplication detection** - Audit player inventories over time

### 🎮 Player Protection
- **Accident recovery** - Restore items after accidental deaths
- **Lag deaths** - Recover from unfair lag-related deaths
- **Backup before risky actions** - Safety net for dangerous activities

### 🔍 Investigation
- **Copy evidence** - Examine player inventories without alerting them
- **Historical analysis** - Review inventory changes over time
- **Proof collection** - Backup evidence for rule violations

---

## 📝 Technical Details

### 🏗️ Architecture
- **Event-driven** - Efficient NeoForge event system
- **Async-friendly** - Non-blocking operations
- **JSON storage** - Human-readable backup format
- **NBT preservation** - Complete item data retention
- **Component-based UI** - Native Minecraft text components with proper styling
- **I18n Support** - Uses Minecraft's built-in translation system

### ⚡ Performance
- **Smart deduplication** - Reduces disk I/O
- **Lazy loading** - Only loads backups when needed
- **Efficient cleanup** - Hourly background task
- **Low overhead** - Minimal server impact
- **Optimized pagination** - Displays only 10 items at a time

### 🌍 Localization
- **English (en_us)** - Default language
- **Russian (ru_ru)** - Полная русская локализация
- **Automatic detection** - Uses client's language setting
- **Easy to extend** - Add new languages via JSON files

### 🔐 Data Format
Backups are stored as JSON with the following structure:
```json
{
  "data": [
    {
      "index": 0,
      "nbt": "{id:\"minecraft:diamond_sword\",Count:1b,tag:{...}}"
    }
  ]
}
```

---

## 🤝 Credits

**Special thanks to PiglinMine.com**

---

## 📄 License

**All Rights Reserved** - This mod is proprietary software.

---

<div align="center">

### 🌟 Made with ❤️ for Minecraft NeoForge 1.21.1

**[Report Bug](https://github.com/Pocky-l/InventoryLogger/issues)** • **[Request Feature](https://github.com/Pocky-l/InventoryLogger/issues)**

</div>
