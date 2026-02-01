package com.example.networkoptimizationmod.mixin;

import com.example.networkoptimizationmod.NetworkOptimizationModClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @Shadow
    public abstract Hand getActiveHand();

    @Shadow
    public abstract ItemStack getStackInHand(Hand hand);

    @Unique
    private Hand networkOptimization$consumedHand;

    @Unique
    private boolean networkOptimization$wasFood;

    @Inject(method = "consumeItem", at = @At("HEAD"))
    private void networkOptimization$captureFoodConsumed(CallbackInfo ci) {
        Hand hand = getActiveHand();
        networkOptimization$consumedHand = hand;
        networkOptimization$wasFood = false;

        if (hand == null) {
            return;
        }

        ItemStack stack = getStackInHand(hand);
        networkOptimization$wasFood = !stack.isEmpty() && stack.isFood();
    }

    @Inject(method = "consumeItem", at = @At("TAIL"))
    private void networkOptimization$sendFoodConsumedPacket(CallbackInfo ci) {
        if (!networkOptimization$wasFood || networkOptimization$consumedHand == null) {
            return;
        }

        NetworkOptimizationModClient.sendFoodConsumed(networkOptimization$consumedHand);
    }
}
