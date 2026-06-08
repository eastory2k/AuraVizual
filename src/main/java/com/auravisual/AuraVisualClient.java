package com.auravisual;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Hand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import java.util.Random;

public class AuraVisualClient {

    private static long lastAttackTime = 0L;
    private static long currentDelay = 0L;
    private static final Random random = new Random();

    public static void onClientTick(MinecraftClient mc) {
        if (mc.player == null || mc.world == null) return;

        // 1. Работает TriggerBot
        if (FeatureManager.triggerBot) {
            runSmartTriggerBot(mc);
        }

        // 2. Параллельно и одновременно работает TargetHUD
        if (FeatureManager.targetHUD) {
            // Твоя логика отображения TargetHUD
        }

        // 3. Одновременно работает GlowESP
        if (FeatureManager.glowESP) {
            // Твоя логика отображения ESP
        }
    }

    private static void runSmartTriggerBot(MinecraftClient mc) {
        HitResult hitResult = mc.crosshairTarget;
        
        if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult) hitResult;
            Entity target = entityHitResult.getEntity();

            if (target instanceof LivingEntity && target.isAlive() && target != mc.player) {
                long currentTime = System.currentTimeMillis();
                
                // Проверяем, прошла ли динамическая задержка
                if (currentTime - lastAttackTime < currentDelay) {
                    return;
                }

                // КРИТЫ: Легитная проверка условий падения персонажа
                boolean isCritPhase = !mc.player.isOnGround() 
                        && mc.player.fallDistance > 0.0F 
                        && !mc.player.isClimbing() 
                        && !mc.player.isTouchingWater();

                if (isCritPhase) {
                    if (mc.interactionManager != null) {
                        mc.interactionManager.attackEntity(mc.player, target);
                        mc.player.swingHand(Hand.MAIN_HAND);
                        
                        lastAttackTime = currentTime;
                        
                        // РАНДОМИЗАЦИЯ ДЛЯ ОБХОДА ПОЛЯРА/СЛОТА (100-150 мс)
                        currentDelay = 100 + random.nextInt(50); 
                    }
                }
            }
        }
    }
}
