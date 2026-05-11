# EdenSouls API

A custom souls-like combat roll system for Minecraft 1.20.1 Forge servers. Built as a standalone alternative to Combat Roll with extended functionality, server-side validation, and optional Peak Stamina integration.

---

## Requirements

| Dependency | Version | Required |
|---|---|---|
| Minecraft | 1.20.1 | Yes |
| Forge | 47.4.20+ | Yes |
| GeckoLib | 4.x | Yes |
| PlayerAnimator | compatible with 1.20.1 | Yes |
| Peak Stamina | any | No (optional) |

---

## Installation

1. Place `edensoulsapi-1.0.1.jar` into your `mods/` folder.
2. Place all required dependency jars into `mods/` as well.
3. Launch the server or client. The config file will be generated automatically at `config/edensoulsapi.json`.

---

## How It Works

### Roll Types

The mod defines three roll types that are assigned externally (e.g. by your server plugin or another mod based on player equipment weight):

| Type | Description |
|---|---|
| `NORMAL` | Standard roll with invincibility frames |
| `FAT_ROLL` | Delayed roll with startup lag, for overloaded players |
| `NO_ROLL` | Stagger animation, no invincibility frames, minimal distance |

### Controls

The roll keybind is registered under **Options → Controls → EdenSouls API**. Default key is unbound — assign it in the controls menu.

### Roll Behavior

- Roll direction is determined by **movement input (WASD)**, not camera direction. Pressing `S + A` rolls the player diagonally backward-left regardless of where the camera points.
- The player model is locked to the roll direction for the full animation duration. The camera remains free.
- Attacking, blocking, and using items are blocked during a roll.
- Rolling while swimming or riding a vehicle is not possible.

### Sounds

- No armor equipped → plays `roll.ogg`
- Any armor piece equipped → plays `roll_armor.ogg`

Place your sound files at:
```
assets/edensoulsapi/sounds/roll.ogg
assets/edensoulsapi/sounds/roll_armor.ogg
```

### Animations

Three animations are loaded from `assets/edensoulsapi/player_animation/`:

```
roll_normal.json
roll_fat.json
roll_no.json
```

These use the PlayerAnimator format. Replace them with your own animations to customize the look.

### Invincibility Frames

I-frames are applied server-side. During the invincibility window, incoming damage is fully cancelled at the `LivingEntity#hurt()` level — no hit flash, no hurt sound.

---

## Configuration

Config file location: `config/edensoulsapi.json`

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

### Roll Type Settings

Each roll type (`normal`, `fat_roll`, `no_roll`) has its own block with the following fields:

| Field | Description |
|---|---|
| `animation_ticks` | Total duration of the roll animation in ticks |
| `movement_ticks` | Number of ticks during which velocity is applied |
| `iframes` | Number of invincibility frames |
| `startup_delay` | Ticks before movement begins (used for fat roll wind-up) |
| `distance_blocks` | Distance traveled during the roll in blocks |

**Default values:**

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
```

---

## Peak Stamina Integration

EdenSouls API integrates with [Peak Stamina](https://www.curseforge.com/minecraft/mc-mods/peak-stamina) automatically when it is present. No additional configuration is required on the Peak Stamina side.

**What happens:**
- Before rolling, the client checks the synced stamina value. If stamina is below `roll_stamina_required`, the roll is blocked visually.
- When a roll is executed, the server deducts `roll_stamina_cost` from the player's stamina, scaled by the player's `STAMINA_USAGE` attribute.
- The stamina regen delay is reset after each roll, matching Peak Stamina's standard behavior.
- If Peak Stamina is not installed, the mod works normally with no stamina system.

---

## Setting the Roll Type

Roll type is assigned per-player server-side via `RollManager`. Access it through the `RollingEntity` interface:

```java
RollManager manager = ((RollingEntity) player).getRollManager();
manager.setRollType(RollType.FAT_ROLL);
```

After changing the roll type on the server, sync it to the client using:

```java
NetworkHandler.CHANNEL.send(
    PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
    new SyncRollTypePacket(RollType.FAT_ROLL)
);
```

Typical usage: call this whenever the player's equipment weight changes.

---

## Developer API

### RollingEntity

All players implement `RollingEntity`. Cast any `Player` to access roll state:

```java
RollingEntity rolling = (RollingEntity) player;
RollManager manager = rolling.getRollManager();

manager.isRolling();        // true during active roll
manager.isInvulnerable();   // true during i-frame window
manager.getRollType();      // current RollType
manager.getCooldownTicks(); // ticks remaining on cooldown
manager.setRollType(RollType.NORMAL);
```

### RollType Enum

```java
public enum RollType {
    NORMAL,
    FAT_ROLL,
    NO_ROLL
}
```

---

## Architecture Notes

- Roll input is processed client-side in `MinecraftClientMixin`. All guard checks (cooldown, food, stamina, swimming) run before the roll is initiated.
- A `ServerboundRollPacket` is sent to the server on each roll. The server re-validates availability and runs `RollManager.onRoll()` independently, ensuring i-frames and cooldown are authoritative server-side.
- Model rotation is locked via `LocalPlayerMixin` by freezing `yBodyRot` and `yHeadRot` each tick during the roll. The camera is unaffected.
- Peak Stamina compat is loaded via reflection to remain compatible without Peak Stamina present.

---

## License

MIT License — see `LICENSE` for details.
