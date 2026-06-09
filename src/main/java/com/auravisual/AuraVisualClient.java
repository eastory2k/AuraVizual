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
        // Если игрок не зашел в мир или функция отключена в GUI — прерываем выполнение
        if (mc.player == null || mc.world == null || !FeatureManager.triggerBot) return;

        processBypassTrigger(mc);
    }

    private static void processBypassTrigger(MinecraftClient mc) {
        HitResult hitResult = mc.crosshairTarget;
        
        // Проверяем, наведен ли прицел игрока на какую-либо сущность
        if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult) hitResult;
            Entity entity = entityHitResult.getEntity();

            // Проверяем, что цель является живым мобом/игроком, она жива и это не сам игрок
            if (entity instanceof LivingEntity && entity.isAlive() && entity != mc.player) {
                // Защита от проверки античитов (не бьем сквозь стены и игнорируем полных невидимок)
                if (!mc.player.canSee(entity) || entity.isInvisible()) return;

                long now = System.currentTimeMillis();

                // Обход SlothAc: если только навели прицел, фиксируем время начала наводки
                if (timeTargetInView == 0L) {
                    timeTargetInView = now;
                }
                
                // Имитация человеческой реакции (пауза 25-45 миллисекунд перед первым ударом)
                if (now - timeTargetInView < (25 + random.nextInt(20))) return;
                
                // Проверка плавающей задержки клика (CPS)
                if (now - lastAttackTime < currentDelay) return;

                // Наносим удар через официальный менеджер взаимодействий Minecraft
                if (mc.interactionManager != null) {
                    mc.interactionManager.attackEntity(mc.player, entity);
                    // Обязательный взмах рукой в тот же тик, чтобы FunTime регистрировал урон и не кикал
                    mc.player.swingHand(Hand.MAIN_HAND); 
                    
                    lastAttackTime = now;
                    
                    // Обход PolarAc: ломаем паттерны кликов с помощью синусоиды и случайного шума.
                    // Создает рваный CPS в районе 11-16 ударов, который невозможно задетектить алгоритмами.
                    currentDelay = 60 + (long)(Math.sin(now) * 12) + random.nextInt(25); 
                }
            }
        } else {
            // Если увели прицел с врага — сбрасываем таймер человеческой реакции
            timeTargetInView = 0L;
        }
    }
}
