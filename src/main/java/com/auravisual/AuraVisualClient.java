package com.auravisual;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Hand;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class AuraVisualEngine {

    public static void onClientTick(MinecraftClient mc) {
        if (mc.player == null || mc.world == null) return;

        // ПРОВЕРКА 1: Работает быстрый TriggerBot
        if (FeatureManager.triggerBot) {
            runFastTriggerBot(mc);
        }

        // ПРОВЕРКА 2: Параллельно работает TargetHUD
        if (FeatureManager.targetHUD) {
            // Твоя логика отрисовки TargetHUD
        }

        // ПРОВЕРКА 3: Параллельно работает ArmorHUD
        if (FeatureManager.armorHUD) {
            // Твоя логика отрисовки ArmorHUD
        }
        
        // Добавь сюда остальные if (FeatureManager.ХХХ) для остальных функций...
    }

    private static void runFastTriggerBot(MinecraftClient mc) {
        HitResult hitResult = mc.crosshairTarget;
        if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult) hitResult;
            Entity target = entityHitResult.getEntity();

            // Проверяем, что цель жива и это не сам игрок
            if (target != null && target.isAlive() && target != mc.player) {
                
                // РАЗГОН: Убрали условие getAttackCooldownProgress >= 1.0f!
                // Бьем сразу же, как только прицел наведен на сущность!
                if (mc.interactionManager != null) {
                    mc.interactionManager.attackEntity(mc.player, target);
                    mc.player.swingHand(Hand.MAIN_HAND);
                }
            }
        }
    }
}
