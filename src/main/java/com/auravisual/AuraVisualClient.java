package com.auravisual;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Hand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import java.util.Random;

public class AuraVisualClient {

    private static long lastAttackTime = 0L;
    private static long currentDelay = 0L;
    private static long timeTargetInView = 0L;
    private static final Random random = new Random();

    // Настройки дистанции и отзывчивости для плотного боя
    private static final double COMBAT_RANGE = 4.1; 
    private static final float SMOOTHING_FACTOR = 3.8f; 

    public static void onClientTick(MinecraftClient mc) {
        if (mc.player == null || mc.world == null) return;

        if (FeatureManager.triggerBot) {
            LivingEntity target = findValidTarget(mc);
            if (target != null) {
                applyAimedSmoothing(mc, target);
            }
            processOptimizedTrigger(mc);
        }
    }

    private static LivingEntity findValidTarget(MinecraftClient mc) {
        LivingEntity closest = null;
        double closestDist = COMBAT_RANGE;

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof LivingEntity && entity.isAlive() && entity != mc.player) {
                // Игнорируем энтити, которые игрок физически не видит (защита от проверки углов сквозь стены)
                if (!mc.player.canSee(entity) || entity.isInvisible()) continue;

                double dist = mc.player.distanceTo(entity);
                if (dist < closestDist) {
                    closestDist = dist;
                    closest = (LivingEntity) entity;
                }
            }
        }
        return closest;
    }

    private static void applyAimedSmoothing(MinecraftClient mc, LivingEntity target) {
        // Наводка на область шеи/груди вместо центра головы для более естественной траектории
        Vec3d targetPos = target.getEyePos().subtract(0, 0.20, 0);
        Vec3d playerPos = mc.player.getEyePos();

        double dX = targetPos.x - playerPos.x;
        double dY = targetPos.y - playerPos.y;
        double dZ = targetPos.z - playerPos.z;
        double dXZ = MathHelper.sqrt((float) (dX * dX + dZ * dZ));

        float targetYaw = (float) (MathHelper.atan2(dZ, dX) * 180.0 / MathHelper.PI) - 90.0f;
        float targetPitch = (float) (-(MathHelper.atan2(dY, dXZ) * 180.0 / MathHelper.PI));

        // Введение шума (микро-смещения мыши, имитирующие дрожание руки)
        float noiseX = (random.nextFloat() - 0.5f) * 0.15f;
        float noiseY = (random.nextFloat() - 0.5f) * 0.10f;

        float diffYaw = MathHelper.wrapDegrees(targetYaw - mc.player.getYaw()) + noiseX;
        float diffPitch = MathHelper.wrapDegrees(targetPitch - mc.player.getPitch()) + noiseY;

        // Скорость подстраивается под угол наклона: чем ближе прицел, тем незаметнее доводка
        float speedModifier = Math.abs(diffYaw) < 4.0f ? 2.0f : 0.0f;
        float finalSpeed = SMOOTHING_FACTOR + speedModifier;

        mc.player.setYaw(mc.player.getYaw() + diffYaw / finalSpeed);
        mc.player.setPitch(mc.player.getPitch() + diffPitch / finalSpeed);
    }

    private static void processOptimizedTrigger(MinecraftClient mc) {
        HitResult hitResult = mc.crosshairTarget;
        
        if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult) hitResult;
            Entity entity = entityHitResult.getEntity();

            if (entity instanceof LivingEntity && entity.isAlive() && entity != mc.player) {
                if (timeTargetInView == 0L) {
                    timeTargetInView = System.currentTimeMillis();
                }

                long now = System.currentTimeMillis();
                
                // Эмуляция физической реакции (минимальный порог задержки перед началом серии ударов)
                if (now - timeTargetInView < (50 + random.nextInt(30))) return;
                if (now - lastAttackTime < currentDelay) return;

                // Определение окна для совершения критического удара
                boolean isFalling = !mc.player.isOnGround() 
                        && mc.player.fallDistance > 0.02F 
                        && !mc.player.isClimbing() 
                        && !mc.player.isTouchingWater();

                if (isFalling) {
                    if (mc.interactionManager != null) {
                        mc.interactionManager.attackEntity(mc.player, entity);
                        mc.player.swingHand(Hand.MAIN_HAND);
                        
                        lastAttackTime = now;
                        // Синусоидальный разброс паузы для генерации рваного CPS (в среднем 12-15 ударов)
                        currentDelay = 65 + (long)(Math.sin(now) * 15) + random.nextInt(25); 
                    }
                }
            }
        } else {
            timeTargetInView = 0L; // Сброс таймера реакции при потере цели из перекрестия
        }
    }
}
