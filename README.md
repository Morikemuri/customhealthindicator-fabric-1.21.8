# CustomHealthIndicator — Fabric 1.21.8

Fabric port of CustomHealthIndicator for Minecraft 1.21.8.

**Original mod by Matvey Slotvinskiy. Ported to Fabric 1.16.5 and 1.21.8, improved by Gabrielsim.**

---

## Features

- Colored nametags above players showing HP and a heart glyph
- Color changes from dark green (full HP) to pulsing red (low HP)
- Flying numbers appear when you hit a player
- Large HP number near crosshair when target is critically low
- Settings screen with 4 sliders for customization
- Configurable keybinding via the standard Controls screen

---

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.8
2. **No Fabric API required**
3. Drop `customhealthindicator-1.5-fabric-1.21.8.jar` into your `mods/` folder
4. Launch the game

---

## Usage

### Opening Settings
- Go to **Options - Controls - HealthIndicator** and assign a key
- Press that key in-game to open the settings screen

### Settings sliders
| Slider | Description |
|--------|-------------|
| Flying number size | Scale of numbers that fly out on hit (0.25x - 5x) |
| Low HP number size | Scale of the big HP number near crosshair (0.25x - 5x) |
| Low HP position: Up/Down | Vertical offset of the big HP number (-100 to +100 px) |
| Low HP position: Left/Right | Horizontal offset of the big HP number (-200 to +200 px) |

Settings are saved automatically to `config/customhealthindicator.json`.

---

## Requirements

- Minecraft 1.21.8
- Fabric Loader 0.15.0+
- Java 21+
- No Fabric API needed
