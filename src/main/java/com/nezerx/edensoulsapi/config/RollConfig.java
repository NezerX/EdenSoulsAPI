package com.nezerx.edensoulsapi.config;

import com.nezerx.edensoulsapi.roll.RollType;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "edensoulsapi")
public class RollConfig implements ConfigData {

    @ConfigEntry.Category("general")
    public boolean allow_rolling_while_airborn = false;

    @ConfigEntry.Category("general")
    public float food_level_required = 0.0f;

    @ConfigEntry.Category("peakstamina")
    public float roll_stamina_cost = 15.0f;

    @ConfigEntry.Category("peakstamina")
    public float roll_stamina_required = 10.0f;

    @ConfigEntry.Category("normal")
    public RollTypeConfig normal = new RollTypeConfig(22, 15, 15, 0, 6.0);

    @ConfigEntry.Category("fat_roll")
    public RollTypeConfig fat_roll = new RollTypeConfig(40, 20, 15, 10, 6.0);

    @ConfigEntry.Category("no_roll")
    public RollTypeConfig no_roll = new RollTypeConfig(28, 28, 0, 10, 0.5);

    public RollTypeConfig getConfig(RollType type) {
        return switch (type) {
            case NORMAL -> normal;
            case FAT_ROLL -> fat_roll;
            case NO_ROLL -> no_roll;
        };
    }

    public static class RollTypeConfig {
        public int animation_ticks;
        public int movement_ticks;
        public int iframes;
        public int startup_delay;
        public double distance_blocks;

        public RollTypeConfig(int animation_ticks, int movement_ticks, int iframes, int startup_delay, double distance_blocks) {
            this.animation_ticks = animation_ticks;
            this.movement_ticks = movement_ticks;
            this.iframes = iframes;
            this.startup_delay = startup_delay;
            this.distance_blocks = distance_blocks;
        }
    }

    public static RollConfig get() {
        return AutoConfig.getConfigHolder(RollConfig.class).getConfig();
    }
}