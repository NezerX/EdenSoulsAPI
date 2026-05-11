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

    @ConfigEntry.Category("normal")
    public RollTypeConfig normal = new RollTypeConfig(22, 18, 15, 0, 3.0);

    @ConfigEntry.Category("fat_roll")
    public RollTypeConfig fat_roll = new RollTypeConfig(28, 30, 15, 10, 3.0);

    @ConfigEntry.Category("no_roll")
    public RollTypeConfig no_roll = new RollTypeConfig(22, 22, 0, 0, 1.5);

    public RollTypeConfig getConfig(RollType type) {
        return switch (type) {
            case NORMAL -> normal;
            case FAT_ROLL -> fat_roll;
            case NO_ROLL -> no_roll;
        };
    }

    public static class RollTypeConfig {
        public int animation_ticks; //длительность переката
        public int movement_ticks; // тики во время которых применяется velocity
        public int iframes; //кадры неуязвимости
        public int startup_delay; //задержка начала движения
        public double distance_blocks; //расстояние, пройденное в кувырке

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