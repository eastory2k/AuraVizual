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

        // Если функция включена в GUI — запускаем умный TriggerBot
        if (FeatureManager.triggerBot) {
            runSmartTriggerBot(mc);
        }

        // Параллельно проверяем другие функции (они работают одновременно)
        if (FeatureManager.targetHUD) {
            // Твоя логика TargetHUD
        }
        if (FeatureManager.glowESP) {
            // Твоя логика ESP
        }
    }

    private static void runSmartTriggerBot(MinecraftClient mc) {
        HitResult hitResult = mc.crosshairTarget;
        
        if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult) hitResult;
            Entity target = entityHitResult.getEntity();

            // Проверяем, что цель жива, это живое существо (игрок/моб) и это не сам игрок
            if (target instanceof LivingEntity && target.isAlive() && target != mc.player) {
                
                long currentTime = System.currentTimeMillis();
                
                // Если задержка после прошлого удара еще не прошла — пропускаем тик
                if (currentTime - lastAttackTime < currentDelay) {
                    return;
                }

                // КРИТЫ: Проверяем, что игрок летит вниз (не стоит на земле, скорость падения > 0)
                // Это единственный легальный способ бить критами без флагов на Polar/Sloth
                boolean isCritPhase = !mc.player.isOnGround() 
                        && mc.player.fallDistance > 0.0F 
                        && !mc.player.isClimbing() 
                        && !mc.player.isTouchingWater();

                if (isCritPhase) {
                    if (mc.interactionManager != null) {
                        // Совершаем атаку
                        mc.interactionManager.attackEntity(mc.player, target);
                        mc.player.swingHand(Hand.MAIN_HAND);
                        
                        // Запоминаем время удара
                        lastAttackTime = currentTime;
                        
                        // РАНДОМИЗАЦИЯ ДЛЯ ОБХОДА: Генерируем случайную задержку до следующего удара.
                        // Диапазон 110-160 мс дает отличную скорость атаки и симулирует клики человека.
                        currentDelay = 110 + random.nextInt(50); 
                    }
                }
            }
        }
    }
}
