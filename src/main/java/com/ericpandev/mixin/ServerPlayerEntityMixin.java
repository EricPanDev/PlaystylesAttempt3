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
     * Intercept damage on ServerPlayerEntity.
     * The expected method signature now includes a ServerWorld parameter.
     */
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;
        if (PlaystyleManager.isPeaceful(self)) {
            if (source.getAttacker() instanceof MobEntity) {
                cir.setReturnValue(false);
            }
        }
    }
}
