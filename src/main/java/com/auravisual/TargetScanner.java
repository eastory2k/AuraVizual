package com.auravisual;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class TargetScanner {
    
    public static PlayerEntity getTargetPlayer(double maxDistance) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        // Проверяем, что игрок в мире и прицел на чем-то сфокусирован
        if (client.player == null || client.crosshairTarget == null) {
            return null;
        }

        // Если прицел наведен на сущность (Entity)
        if (client.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) client.crosshairTarget).getEntity();
            
            // Проверяем, что это игрок, а не моб или стойка для брони
            if (entity instanceof PlayerEntity targetPlayer) {
                // Проверяем дистанцию (чтобы HUD не горел через полкарты)
                if (client.player.distanceTo(targetPlayer) <= maxDistance) {
                    return targetPlayer;
                }
            }
        }
        return null;
    }
}
