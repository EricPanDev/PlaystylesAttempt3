package com.ericpandev.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.text.Text;
import com.ericpandev.playstyle.PlaystyleManager;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    private int lastMessageTick = -200; // Ensure the first message is sent

    @Inject(method = "dropInventory", at = @At("HEAD"), cancellable = true)
    private void onDropInventory(CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        if (PlaystyleManager.isRetain(self)) {
            ci.cancel();
        }
    }

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void preventDamage(Entity target, CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        if (target instanceof LivingEntity && PlaystyleManager.isPeaceful(self)) {
            if (self instanceof ServerPlayerEntity serverPlayer) {
                int currentTick = serverPlayer.getServer().getTicks();
                if (currentTick - lastMessageTick > 100) { // Adjust 100 ticks as needed (5 seconds at 20 TPS)
                    serverPlayer.sendMessage(Text.literal("You are playing using the peaceful playstyle! You cannot attack other entities."), false);
                    lastMessageTick = currentTick;
                }
            }
            ci.cancel();
        }
    }
}