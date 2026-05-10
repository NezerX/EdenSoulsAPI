package com.nezerx.edensouls.client;

import com.nezerx.edensouls.EdenSouls;
import com.nezerx.edensouls.ModSounds;
import com.nezerx.edensouls.roll.RollType;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;

public class RollAnimationHandler {

    private static final ResourceLocation ANIM_NORMAL = new ResourceLocation(EdenSouls.MOD_ID, "roll_normal");
    private static final ResourceLocation ANIM_FAT    = new ResourceLocation(EdenSouls.MOD_ID, "roll_fat");
    private static final ResourceLocation ANIM_NO     = new ResourceLocation(EdenSouls.MOD_ID, "roll_no");
    public  static final ResourceLocation LAYER_ID    = new ResourceLocation(EdenSouls.MOD_ID, "roll_layer");

    public static void registerLayer() {
        PlayerAnimationAccess.REGISTER_ANIMATION_EVENT.register((player, animationStack) -> {
            ModifierLayer<KeyframeAnimationPlayer> layer = new ModifierLayer<>();
            animationStack.addAnimLayer(500, layer);
            PlayerAnimationAccess.getPlayerAssociatedData(player).set(LAYER_ID, layer);
        });
    }

    public static void playAnimation(AbstractClientPlayer player, RollType rollType) {
        ResourceLocation animId = switch (rollType) {
            case NORMAL -> ANIM_NORMAL;
            case FAT_ROLL -> ANIM_FAT;
            case NO_ROLL -> ANIM_NO;
        };

        KeyframeAnimation animData = PlayerAnimationRegistry.getAnimation(animId);
        if (animData == null) {
            EdenSouls.LOGGER.warn("Animation not found: {}", animId);
            return;
        }

        // Звук — проверяем броню
        boolean hasArmor =
                !player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD).isEmpty()  ||
                        !player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST).isEmpty() ||
                        !player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.LEGS).isEmpty()  ||
                        !player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET).isEmpty();

        net.minecraft.sounds.SoundEvent sound = hasArmor
                ? ModSounds.ROLL_ARMOR.get()
                : ModSounds.ROLL.get();

        player.playSound(sound, 1.0f, 1.0f);

        var data = PlayerAnimationAccess.getPlayerAssociatedData(player);
        if (data.get(LAYER_ID) instanceof ModifierLayer<?> rawLayer) {
            @SuppressWarnings("unchecked")
            ModifierLayer<KeyframeAnimationPlayer> layer = (ModifierLayer<KeyframeAnimationPlayer>) rawLayer;
            layer.setAnimation(new KeyframeAnimationPlayer(animData));
        }
    }
}