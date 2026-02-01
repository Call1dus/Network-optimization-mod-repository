package com.example.networkoptimizationmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.LingeringPotionItem;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class NetworkOptimizationMod implements ModInitializer {
    public static final String MOD_ID = "network_optimization_mod";
    public static final Identifier CONSUME_FOOD_PACKET = new Identifier(MOD_ID, "consume_food");
    public static final Identifier THROW_POTION_PACKET = new Identifier(MOD_ID, "throw_potion");
    public static final Identifier CLIENT_EFFECT_PACKET = new Identifier(MOD_ID, "client_effect");

    @Override
    public void onInitialize() {
        ServerPlayNetworking.registerGlobalReceiver(
                CONSUME_FOOD_PACKET,
                (server, player, handler, buf, responseSender) -> {
                    Hand hand = buf.readEnumConstant(Hand.class);
                    server.execute(() -> applyServerFoodConsumption(player, hand));
                }
        );

        ServerPlayNetworking.registerGlobalReceiver(
                THROW_POTION_PACKET,
                (server, player, handler, buf, responseSender) -> {
                    Hand hand = buf.readEnumConstant(Hand.class);
                    server.execute(() -> applyServerPotionThrow(player, hand));
                }
        );

        ServerPlayNetworking.registerGlobalReceiver(
                CLIENT_EFFECT_PACKET,
                (server, player, handler, buf, responseSender) -> {
                    Identifier effectId = buf.readIdentifier();
                    int duration = buf.readVarInt();
                    int amplifier = buf.readVarInt();
                    boolean ambient = buf.readBoolean();
                    boolean showParticles = buf.readBoolean();
                    boolean showIcon = buf.readBoolean();
                    server.execute(() -> applyServerStatusEffect(
                            player,
                            effectId,
                            duration,
                            amplifier,
                            ambient,
                            showParticles,
                            showIcon
                    ));
                }
        );
    }

    private void applyServerFoodConsumption(ServerPlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.isEmpty() || !stack.isFood()) {
            return;
        }

        ItemStack result = player.eatFood(player.getWorld(), stack);
        if (result != stack) {
            player.setStackInHand(hand, result);
        }
    }

    private void applyServerPotionThrow(ServerPlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.isEmpty()) {
            return;
        }

        if (stack.getItem() instanceof SplashPotionItem || stack.getItem() instanceof LingeringPotionItem) {
            player.useItem(player.getWorld(), hand);
        }
    }

    private void applyServerStatusEffect(
            LivingEntity entity,
            Identifier effectId,
            int duration,
            int amplifier,
            boolean ambient,
            boolean showParticles,
            boolean showIcon
    ) {
        StatusEffect effect = Registries.STATUS_EFFECT.get(effectId);
        if (effect == null) {
            return;
        }

        StatusEffectInstance current = entity.getStatusEffect(effect);
        if (current != null && current.getDuration() >= duration && current.getAmplifier() >= amplifier) {
            return;
        }

        entity.addStatusEffect(new StatusEffectInstance(effect, duration, amplifier, ambient, showParticles, showIcon));
    }
}
