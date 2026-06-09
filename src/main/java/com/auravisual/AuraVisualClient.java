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
        // Если игрок не в мире или триггербот выключен в меню — выходим
        if (mc.player == null || mc.world == null || !FeatureManager.triggerBot) return;

        processBypassTrigger(mc);
    }

    private static void processBypassTrigger(MinecraftClient mc) {
        HitResult hitResult = mc.crosshairTarget;
        
        // Проверяем, наведен ли крестик на сущность
        if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult) hitResult;
            Entity entity = entityHitResult.getEntity();

            // Проверяем, что цель жива, это игрок/моб и это не мы сами
            if (entity instanceof LivingEntity && entity.isAlive() && entity != mc.player) {
                // Защита от ударов сквозь стены и по полным невидимкам (анти-бот проверка античитов)
                if (!mc.player.canSee(entity) || entity.isInvisible()) return;

                long now = System.currentTimeMillis();

                // Фикс для SlothAc: если только навелись, запускаем таймер реакции человека
                if (timeTargetInView == 0L) {
                    timeTargetInView = now;
                }
                
                // Имитируем задержку реакции (~25-45 мс). Мгновенный удар вызовет бан
                if (now - timeTargetInView < (25 + random.nextInt(20))) return;
                
                // Фикс для PolarAc: проверка динамической задержки между кликами (CPS)
                if (now - lastAttackTime < currentDelay) return;

                // Бьем цель через официальный менеджер игры
                if (mc.interactionManager != null) {
                    mc.interactionManager.attackEntity(mc.player, entity);
                    mc.player.swingHand(Hand.MAIN_HAND); // Синхронный пакет взмаха для FunTime
                    
                    lastAttackTime = now;
                    
                    // ГЕНЕРАЦИЯ ОБХОДА: ломаем тайминги синусоидой + случайным шумом.
                    // Выдает плавающий CPS от 11 до 16 ударов, который невозможно отследить алгоритмами.
                    currentDelay = 60 + (long)(Math.sin(now) * 12) + random.nextInt(25); 
                }
            }
        } else {
            // Если увели прицел с врага — сбрасываем таймер наводки
            timeTargetInView = 0L;
        }
    }
}
