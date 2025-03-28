package com.ericpandev.mixin;

import com.ericpandev.playstyle.PlaystyleManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// This mixin is written for Fabric 0.117.0+1.21.4.
// It intercepts MobEntity#setTarget(LivingEntity) to prevent mobs from targeting players
// whose playstyle is set to "peaceful".
@Mixin(MobEntity.class)
public class MobEntityMixin {

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void onSetTarget(LivingEntity target, CallbackInfo ci) {
        if (target instanceof ServerPlayerEntity player && (PlaystyleManager.isPeaceful(player) || PlaystyleManager.isUndead(player))) {
            // Cancel setting the target so mobs do not attack peaceful players.
            ci.cancel();
        }
    }
}
