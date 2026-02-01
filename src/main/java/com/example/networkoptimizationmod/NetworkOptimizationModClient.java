package com.example.networkoptimizationmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;

public class NetworkOptimizationModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // No client setup needed beyond mixin-driven packet sending.
    }

    public static void sendFoodConsumed(Hand hand) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeEnumConstant(hand);
        ClientPlayNetworking.send(NetworkOptimizationMod.CONSUME_FOOD_PACKET, buf);
    }

    public static void sendPotionThrown(Hand hand) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeEnumConstant(hand);
        ClientPlayNetworking.send(NetworkOptimizationMod.THROW_POTION_PACKET, buf);
    }

    public static void sendClientStatusEffect(StatusEffectInstance effect) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeIdentifier(Registries.STATUS_EFFECT.getId(effect.getEffectType()));
        buf.writeVarInt(effect.getDuration());
        buf.writeVarInt(effect.getAmplifier());
        buf.writeBoolean(effect.isAmbient());
        buf.writeBoolean(effect.shouldShowParticles());
        buf.writeBoolean(effect.shouldShowIcon());
        ClientPlayNetworking.send(NetworkOptimizationMod.CLIENT_EFFECT_PACKET, buf);
    }
}
