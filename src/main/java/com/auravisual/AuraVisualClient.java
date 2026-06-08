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

    // Этот метод вызывается каждый тик из обработчика событий мода
    public static void onClientTick(MinecraftClient mc) {
        if (mc.player == null || mc.world == null) return;

        // ПРОВЕРКА 1: Быстрый обходящий античиты TriggerBot
        if (FeatureManager.triggerBot) {
            runSmartTriggerBot(mc);
        }

        // ПРОВЕРКА 2: Параллельно может работать TargetHUD
        if (FeatureManager.targetHUD) {
            // Твоя логика рендеринга TargetHUD
        }

        // ПРОВЕРКА 3: Параллельно может работать GlowESP
        if (FeatureManager.glowESP) {
            // Твоя логика рендеринга ESP
        }
    }

    private static void runSmartTriggerBot(MinecraftClient mc) {
        HitResult hitResult = mc.crosshairTarget;
        
        if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult) hitResult;
            Entity target = entityHitResult.getEntity();

            // Проверяем живую цель
            if (target instanceof LivingEntity && target.isAlive() && target != mc.player) {
                
                long currentTime = System.currentTimeMillis();
                
                // Проверяем, прошла ли динамическая задержка
                if (currentTime - lastAttackTime < currentDelay) {
                    return;
                }

                // КРИТЫ: Легитная проверка условий падения персонажа.
                // Не дает флагов, так как полностью использует физику игры.
                boolean isCritPhase = !mc.player.isOnGround() 
                        && mc.player.fallDistance > 0.0F 
                        && !mc.player.isClimbing() 
                        && !mc.player.isTouchingWater();

                if (isCritPhase) {
                    if (mc.interactionManager != null) {
                        // Наносим удар
                        mc.interactionManager.attackEntity(mc.player, target);
                        mc.player.swingHand(Hand.MAIN_HAND);
                        
                        // Обновляем время атаки
                        lastAttackTime = currentTime;
                        
                        // РАНДОМИЗАЦИЯ ДЛЯ ОБХОДА ПОЛЯРА/СЛОТА:
                        // Интервал 100-150 мс симулирует отличные клики (~8 CPS) 
                        // с постоянно меняющимися промежутками. Античит думает, что кликает человек.
                        currentDelay = 100 + random.nextInt(50); 
                    }
                }
            }
        }
    }
}
