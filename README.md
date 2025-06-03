# 🧰 Inventory Logger (Forge Mod)
*A reliable way to safeguard inventories — even after disaster strikes.*

Welcome to **Inventory Logger**, a mod built for **Minecraft Forge** that helps server owners, players, and modpacks preserve inventories against unexpected item loss. Whether due to death, glitches, or rogue mods — never lose precious loot again.

> ⚠️ This mod **does not actively prevent** inventory loss. Instead, it provides a system to **log, list, and restore** inventory backups for any player.

---

## 📦 Features

- 📂 **Automatic inventory backup system** (configurable intervals planned)
- 🧾 **Readable JSON format** for inventory snapshots
- 💬 **Simple commands** to restore or inspect past inventories
- 🧍 Works for **any player**, not just the current one
- 🔄 Restore specific inventory states from any date (e.g., `/inventory set PlayerName 2025-05-19-21-30-00`)

---

## 🔧 Commands

| Command | Description |
|--------|-------------|
| `/inventory set <player> <date>` | Replaces the player’s inventory with the saved state from the given date (`yyyy-MM-dd-HH-mm-ss`). |
| `/inventory list <player> [partial-date]` | Lists all available inventory saves. You can enter partial dates like `2025-06` to filter by month. |

> ℹ️ You can **click on inventory files** in the chat to instantly preview them in a fake chest GUI (read-only).

---

## 🗂️ File Storage

Saved inventories are stored in:
/InventoryLog/inventory/<player-uuid>/<timestamp>.json

Each file includes complete inventory state, armor, offhand, and other data encoded for accurate restoration.

---

## 🧪 Planned Features

Here’s what’s coming next:

- [x] Add View button to list
- [x] Offline Player Support(keeping player tab complete if they are still online)
- [ ] Configurable backup triggers (death, join, leave, timed)
- [ ] Inventory backup retention (limit max saves per player)
- [ ] Restore inventory to *another* player
- [ ] Sync with remote storage / FTP or Discord Webhooks
- [ ] Config option to auto-backup Ender Chest
- [ ] Multi-loader support (NeoForge, Fabric?)
- [ ] Curios Support

---

## 🙌 Special Thanks

Big shout-out to **PiglinMine.com** for the inspiration and testing support.

---

## 💬 Feedback & Contributions

Pull requests, suggestions, and bug reports are always welcome!
You can also open a discussion if you have feature ideas or questions.

Enjoy the game, and never worry about losing your best gear again. 🛡️✨
