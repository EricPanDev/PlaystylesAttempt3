package com.ericpandev.mixin;

import com.ericpandev.playstyle.PlaystyleManager;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    /**
     * Intercept damage on ServerPlayerEntity and modify damage based on playstyle.
     */
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;

        // If the playstyle is "peaceful", prevent mob damage
        if (PlaystyleManager.isPeaceful(self) && source.getAttacker() instanceof MobEntity) {
            cir.setReturnValue(false); // Cancel the damage event
            return;
        }

        // If the playstyle is "difficult", apply 1.5x damage
        if (PlaystyleManager.isDifficult(self)) {
            amount *= 1.5f; // Modify the damage amount to be 1.5x
        }

        // Allow normal damage to continue with the modified amount
    }
}
