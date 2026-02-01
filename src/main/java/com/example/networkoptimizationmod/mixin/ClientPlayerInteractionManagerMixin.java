package com.example.networkoptimizationmod.mixin;

import com.example.networkoptimizationmod.NetworkOptimizationModClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.LingeringPotionItem;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {
    @Inject(method = "interactItem", at = @At("HEAD"), cancellable = true)
    private void networkOptimization$interceptPotionThrow(
            ClientPlayerEntity player,
            Hand hand,
            CallbackInfoReturnable<ActionResult> cir
    ) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.isEmpty()) {
            return;
        }

        if (stack.getItem() instanceof SplashPotionItem || stack.getItem() instanceof LingeringPotionItem) {
            ActionResult result = player.useItem(player.getWorld(), hand);
            if (result.isAccepted()) {
                NetworkOptimizationModClient.sendPotionThrown(hand);
            }
            cir.setReturnValue(result);
        }
    }
}
