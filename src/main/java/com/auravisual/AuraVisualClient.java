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
    private static final Random random = new Random();

    private static final double ASSIST_RANGE = 4.5; 
    private static final float ASSIST_SPEED = 3.5f; 

    public static void onClientTick(MinecraftClient mc) {
        if (mc.player == null || mc.world == null) return;

        if (FeatureManager.triggerBot) {
            runAimAssist(mc);
            runSmartTriggerBot(mc);
        }
    }

    private static void runSmartTriggerBot(MinecraftClient mc) {
        HitResult hitResult = mc.crosshairTarget;
        
        if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult) hitResult;
            Entity target = entityHitResult.getEntity();

            if (target instanceof LivingEntity && target.isAlive() && target != mc.player) {
                long currentTime = System.currentTimeMillis();
                
                if (currentTime - lastAttackTime < currentDelay) return;

                boolean isCritPhase = !mc.player.isOnGround() 
                        && mc.player.fallDistance > 0.0F 
                        && !mc.player.isClimbing() 
                        && !mc.player.isTouchingWater();

                if (isCritPhase) {
                    if (mc.interactionManager != null) {
                        mc.interactionManager.attackEntity(mc.player, target);
                        mc.player.swingHand(Hand.MAIN_HAND);
                        
                        lastAttackTime = currentTime;
                        currentDelay = 100 + random.nextInt(45); 
                    }
                }
            }
        }
    }

    private static void runAimAssist(MinecraftClient mc) {
        LivingEntity closestTarget = null;
        double closestDist = ASSIST_RANGE;

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof LivingEntity && entity.isAlive() && entity != mc.player) {
                double dist = mc.player.distanceTo(entity);
                if (dist < closestDist) {
                    closestDist = dist;
                    closestTarget = (LivingEntity) entity;
                }
            }
        }

        if (closestTarget != null) {
            Vec3d targetPos = closestTarget.getEyePos();
            Vec3d playerPos = mc.player.getEyePos();

            double diffX = targetPos.x - playerPos.x;
            double diffY = targetPos.y - playerPos.y;
            double diffZ = targetPos.z - playerPos.z;
            double diffXZ = MathHelper.sqrt((float) (diffX * diffX + diffZ * diffZ));

            float targetYaw = (float) (MathHelper.atan2(diffZ, diffX) * 180.0 / MathHelper.PI) - 90.0f;
            float targetPitch = (float) (-(MathHelper.atan2(diffY, diffXZ) * 180.0 / MathHelper.PI));

            mc.player.setYaw(mc.player.getYaw() + MathHelper.wrapDegrees(targetYaw - mc.player.getYaw()) / ASSIST_SPEED);
            mc.player.setPitch(mc.player.getPitch() + MathHelper.wrapDegrees(targetPitch - mc.player.getPitch()) / ASSIST_SPEED);
        }
    }
}
