package com.auravisual;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Hand;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class AuraVisualClient {

    // Этот метод должен вызываться из твоего Mixin или обработчика событий каждый тик
    public static void onClientTick(MinecraftClient mc) {
        if (mc.player == null || mc.world == null) return;

        // Все функции теперь проверяются независимо (работают одновременно)
        if (FeatureManager.triggerBot) {
            runFastTriggerBot(mc);
        }

        if (FeatureManager.targetHUD) {
            // Логика TargetHUD
        }

        if (FeatureManager.glowESP) {
            // Логика ESP
        }
    }

    private static void runFastTriggerBot(MinecraftClient mc) {
        HitResult hitResult = mc.crosshairTarget;
        if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult) hitResult;
            Entity target = entityHitResult.getEntity();

            if (target != null && target.isAlive() && target != mc.player) {
                // Мгновенная атака без задержки (ускоренный TriggerBot)
                if (mc.interactionManager != null) {
                    mc.interactionManager.attackEntity(mc.player, target);
                    mc.player.swingHand(Hand.MAIN_HAND);
                }
            }
        }
    }
}
