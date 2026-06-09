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
    private static long timeTargetInView = 0L;
    private static final Random random = new Random();

    public static void onClientTick(MinecraftClient mc) {
        if (mc.player == null || mc.world == null) return;

        // Работает ТОЛЬКО триггербот, никакого авто-наведения аима!
        if (FeatureManager.triggerBot) {
            processFastTrigger(mc);
        }
    }

    private static void processFastTrigger(MinecraftClient mc) {
        HitResult hitResult = mc.crosshairTarget;
        
        if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult) hitResult;
            Entity entity = entityHitResult.getEntity();

            // Проверяем, что цель жива, это не мы сами, и она видима сквозь прозрачные блоки
            if (entity instanceof LivingEntity && entity.isAlive() && entity != mc.player) {
                if (!mc.player.canSee(entity) || entity.isInvisible()) return;

                if (timeTargetInView == 0L) {
                    timeTargetInView = System.currentTimeMillis();
                }

                long now = System.currentTimeMillis();
                
                // Имитация минимальной микро-задержки человеческого клика (очень быстрая, 25-45 мс)
                if (now - timeTargetInView < (25 + random.nextInt(20))) return;
                if (now - lastAttackTime < currentDelay) return;

                // Проверка на фазу падения для прожатия 100% критического удара
                boolean isFalling = !mc.player.isOnGround() 
                        && mc.player.fallDistance > 0.01F 
                        && !mc.player.isClimbing() 
                        && !mc.player.isTouchingWater();

                if (isFalling) {
                    if (mc.interactionManager != null) {
                        mc.interactionManager.attackEntity(mc.player, entity);
                        mc.player.swingHand(Hand.MAIN_HAND);
                        
                        lastAttackTime = now;
                        // Быстрый рваный CPS (в среднем 13-16 кликов) для идеального обхода Polar
                        currentDelay = 55 + (long)(Math.sin(now) * 10) + random.nextInt(15); 
                    }
                }
            }
        } else {
            timeTargetInView = 0L; // Сбрасываем таймер взгляда, если увели прицел
        }
    }
}
