# EdenSouls API

A custom souls-like combat roll system for Minecraft 1.20.1 Forge servers. Built as a standalone alternative to Combat Roll with extended functionality, server-side validation, and optional Peak Stamina integration.

For a more in-depth and better formatted guide, scroll down and read the documentation below.

----------

## Features

- **Three Roll Types**: NORMAL (standard iframes), FAT_ROLL (delayed wind-up), NO_ROLL (stagger, no iframes)
- **Directional Rolling**: Roll direction determined by movement input (WASD), not camera direction
- **Server-Side Validation**: All critical checks (cooldown, iframes, stamina) are authoritative on the server
- **Peak Stamina Integration**: Automatic stamina consumption when mod is present
- **Configurable Everything**: Animation duration, movement distance, iframes, startup delay per roll type
- **Custom Animations**: Replaceable PlayerAnimator format animations
- **Armor Sound System**: Different roll sounds based on whether armor is equipped

![EdenSouls API Roll Demo](https://via.placeholder.com/800x400?text=EdenSouls+API+Roll+Demo)

----------

## Requirements

| Dependency | Version | Required |
|---|---|---|
| Minecraft | 1.20.1 | Yes |
| Forge | 47.4.20+ | Yes |
| GeckoLib | 4.x | Yes |
| PlayerAnimator | compatible with 1.20.1 | Yes |
| Peak Stamina | any | No (optional) |

----------

## Installation

1. Place `edensoulsapi-1.0.1.jar` into your `mods/` folder.
2. Place all required dependency jars into `mods/` as well.
3. Launch the server or client. The config file will be generated automatically at `config/edensoulsapi.json`.

----------

## Configuration Files

- **`config/edensoulsapi.json`**: All core gameplay values (general settings, roll types, stamina integration).

**Note**: You may need to restart your game or server for config changes to take effect.

----------

## Base Settings

### General

| Field | Default | Description |
|---|---|---|
| `allow_rolling_while_airborn` | `false` | Allow rolling while not on the ground |
| `food_level_required` | `0.0` | Minimum food level required to roll (0 = no restriction) |

### Peak Stamina (only active if Peak Stamina is installed)

| Field | Default | Description |
|---|---|---|
| `roll_stamina_cost` | `15.0` | Stamina drained per roll |
| `roll_stamina_required` | `10.0` | Minimum stamina required to roll |

----------

## Roll Type Settings

Each roll type (`normal`, `fat_roll`, `no_roll`) has its own configuration block with the following fields:

| Field | Description |
|---|---|
| `animation_ticks` | Total duration of the roll animation in ticks |
| `movement_ticks` | Number of ticks during which velocity is applied |
| `iframes` | Number of invincibility frames |
| `startup_delay` | Ticks before movement begins (used for fat roll wind-up) |
| `distance_blocks` | Distance traveled during the roll in blocks |

### Default Values

```json
"normal": {
  "animation_ticks": 22,
  "movement_ticks": 15,
  "iframes": 15,
  "startup_delay": 0,
  "distance_blocks": 6.0
},
"fat_roll": {
  "animation_ticks": 40,
  "movement_ticks": 20,
  "iframes": 15,
  "startup_delay": 10,
  "distance_blocks": 6.0
},
"no_roll": {
  "animation_ticks": 28,
  "movement_ticks": 28,
  "iframes": 0,
  "startup_delay": 10,
  "distance_blocks": 0.5
}
