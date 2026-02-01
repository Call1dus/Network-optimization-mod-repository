package com.example.networkoptimizationmod.mixin;

import com.example.networkoptimizationmod.NetworkOptimizationModClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerStatusEffectMixin {
    @Inject(method = "addStatusEffect", at = @At("TAIL"))
    private void networkOptimization$sendStatusEffect(
            StatusEffectInstance effect,
            Entity source,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (cir.getReturnValue()) {
            NetworkOptimizationModClient.sendClientStatusEffect(effect);
        }
    }
}
