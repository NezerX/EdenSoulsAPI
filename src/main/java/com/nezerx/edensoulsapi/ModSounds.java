package com.nezerx.edensoulsapi;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, EdenSoulsAPI.MOD_ID);

    public static final RegistryObject<SoundEvent> ROLL =
            SOUNDS.register("roll", () ->
                    SoundEvent.createVariableRangeEvent(new ResourceLocation(EdenSoulsAPI.MOD_ID, "roll")));

    public static final RegistryObject<SoundEvent> ROLL_ARMOR =
            SOUNDS.register("roll_armor", () ->
                    SoundEvent.createVariableRangeEvent(new ResourceLocation(EdenSoulsAPI.MOD_ID, "roll_armor")));
}