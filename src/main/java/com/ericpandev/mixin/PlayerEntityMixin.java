package com.ericpandev.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.ericpandev.playstyle.PlaystyleManager;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    // Inject at the beginning of dropInventory and cancel its execution.
    @Inject(method = "dropInventory", at = @At("HEAD"), cancellable = true)
    private void onDropInventory(CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        if (PlaystyleManager.isRetain(self)) {
            System.out.println("CANCELLED INVENTORY DROP");
            ci.cancel();
        }
    }


}