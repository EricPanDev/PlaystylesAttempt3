package com.ericpandev.mixin;

import com.ericpandev.playstyle.PlaystyleManager;
import com.ericpandev.playstyle.ExperienceManager;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;

        if ((PlaystyleManager.isPeaceful(self) || PlaystyleManager.isUndead(self)) && source.getAttacker() instanceof MobEntity) {
            cir.setReturnValue(false);
            return;
        }

        if (PlaystyleManager.isDifficult(self)) {
            amount *= 1.5f;
        }

        if (PlaystyleManager.isBerserk(self)) {
            amount *= 2f;
        }
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(DamageSource source, CallbackInfo ci) {
        ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;

        if (PlaystyleManager.isRetain(self)) {
            ExperienceManager.storeXp(self.getUuid(), self.totalExperience);
        }
    }

    @Inject(method = "copyFrom", at = @At("RETURN"))
    private void onCopyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;

        PlaystyleManager.setPlaystyle(self, PlaystyleManager.getPlaystyle(oldPlayer));

        if (PlaystyleManager.isRetain(self)) {
            int xp = ExperienceManager.retrieveXp(oldPlayer.getUuid());
            self.addExperience(xp);
            ExperienceManager.clearXp(oldPlayer.getUuid());
        }
    }
}
