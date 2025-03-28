package com.ericpandev.mixin;

import com.ericpandev.playstyle.InventoryManager;
import com.ericpandev.playstyle.PlaystyleManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void modifyDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getAttacker() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) source.getAttacker();
            if (PlaystyleManager.isBerserk(player)) {
                float newAmount = amount * 2; // Double damage
                ((LivingEntity) (Object) this).setHealth(((LivingEntity) (Object) this).getHealth() - newAmount);
                cir.setReturnValue(true);
            }
        }
    }

    /**
     * Prevents the player's inventory from dropping on death if the playstyle is PEACEFUL or RETAIN.
     */
    @Inject(method = "dropInventory", at = @At("HEAD"), cancellable = true)
    private void onDropInventory(CallbackInfo ci) {
        if (((Object) this) instanceof ServerPlayerEntity) {
            ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;
            if (PlaystyleManager.isRetain(self)) {
                System.out.println("CANCELLED INVENTORY DROP");
                InventoryManager.storeInventory(self.getUuid(), self.getInventory());
                ci.cancel();
            }
        }
    }

    /**
     * Prevents the player's experience from dropping on death if the playstyle is PEACEFUL or RETAIN.
     */
    @Inject(method = "dropExperience", at = @At("HEAD"), cancellable = true)
    private void onDropExperience(CallbackInfo ci) {
        if (((Object) this) instanceof ServerPlayerEntity) {
            ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;
            if (PlaystyleManager.isRetain(self)) {
                ci.cancel();
            }
        }
    }

    /**
     * Hooks into shouldDropExperience to prevent experience dropping based on playstyle.
     * When the player's playstyle is PEACEFUL or RETAIN, it returns false.
     */
    @Inject(method = "shouldDropExperience", at = @At("HEAD"), cancellable = true)
    private void onShouldDropExperience(CallbackInfoReturnable<Boolean> cir) {
        if (((Object) this) instanceof ServerPlayerEntity) {
            ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;
            if (PlaystyleManager.isPeaceful(self) || PlaystyleManager.isRetain(self)) {
                cir.setReturnValue(false);
            }
        }
    }
}
