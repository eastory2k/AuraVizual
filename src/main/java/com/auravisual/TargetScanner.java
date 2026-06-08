package com.auravisual;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.util.Target;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class TargetScanner {
    
    // Метод ищет игрока, на которого направлен прицел
    public static PlayerEntity getTargetPlayer(double maxDistance) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.crosshairTarget == null) return null;

        // Используем встроенный в Майнкрафт результат сканирования взгляда
        if (client.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) client.crosshairTarget;
            if (entityHit.getEntity() instanceof PlayerEntity targetPlayer) {
                // Проверяем расстояние до цели
                if (client.player.distanceTo(targetPlayer) <= maxDistance) {
                    return targetPlayer;
                }
            }
        }
        return null;
    }
}
