package com.auravisual;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import org.lwjgl.glfw.GLFW;

public class AuraVisualClient implements ClientModInitializer {
    public static KeyBinding openGuiKey;
    private static double wingTimer = 0; // Кастомный таймер для плавной анимации крыльев

    @Override
    public void onInitializeClient() {
        // Регистрация кнопки открытия ClickGUI (на Правый Шифт)
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.auravisual.opengui", 
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT, 
                "category.auravisual.general"
        ));

        // Открытие меню при нажатии кнопки
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && openGuiKey.wasPressed()) {
                client.setScreen(new ClickGUI());
            }

            // --- ОБРАБОТКА ДИНАМИЧЕСКОЙ КОСМЕТИКИ ---
            if (client.world != null && client.player != null && !client.options.hudHidden) {
                long time = client.world.getTime();
                double px = client.player.getX();
                double py = client.player.getY();
                double pz = client.player.getZ();
                float yaw = client.player.getBodyYaw();

                // 1. НАСТРОЙКА: КРЫЛЬЯ С ПРИВЯЗКОЙ К СКОРОСТИ
                if (FeatureManager.showWings) {
                    // Считаем скорость движения игрока (длина вектора velocity)
                    double velocity = client.player.getVelocity().horizontalLength();
                    
                    // Базовая скорость маха (0.15 когда стоим) + ускорение от бега/полета
                    double wingSpeed = 0.15 + (velocity * 1.8);
                    
                    // ИСПРАВЛЕНО: Вместо isFallFlying() используем isGliding() для Fabric 1.21.4
                    if (client.player.isGliding()) wingSpeed = 0.8; 
                    
                    wingTimer += wingSpeed;
                    double wingWave = Math.sin(wingTimer) * 0.45; // Амплитуда взмаха

                    // Левое крыло
                    for (int i = 0; i < 5; i++) {
                        double wingX = Math.sin(Math.toRadians(yaw + 135)) * (0.3 + i * 0.15) - (wingWave * 0.12);
                        double wingZ = Math.cos(Math.toRadians(yaw + 135)) * (0.3 + i * 0.15);
                        client.world.addParticle(
                            net.minecraft.particle.ParticleTypes.DRAGON_BREATH,
                            px + wingX, py + 0.8 + (i * 0.08), pz + wingZ,
                            0, 0, 0
                        );
                    }
                    // Правое крыло
                    for (int i = 0; i < 5; i++) {
                        double wingX = Math.sin(Math.toRadians(yaw - 135)) * (0.3 + i * 0.15) + (wingWave * 0.12);
                        double wingZ = Math.cos(Math.toRadians(yaw - 135)) * (0.3 + i * 0.15);
                        client.world.addParticle(
                            net.minecraft.particle.ParticleTypes.DRAGON_BREATH,
                            px + wingX, py + 0.8 + (i * 0.08), pz + wingZ,
                            0, 0, 0
                        );
                    }
                }

                // 2. НАСТРОЙКА: ШАПКА / НИМБ
                if (FeatureManager.showHat) {
                    double angle = (time * 0.2);
                    double radius = 0.35;
                    double hatX = Math.sin(angle) * radius;
                    double hatZ = Math.cos(angle) * radius;
                    double headHeight = client.player.isSneaking() ? 1.6 : 1.9;

                    client.world.addParticle(
                        net.minecraft.particle.ParticleTypes.WITCH,
                        px + hatX, py + headHeight, pz + hatZ,
                        0, 0, 0
                    );
                }

                // 3. НАСТРОЙКА: ДЕМОНИЧЕСКИЕ ЛУЧИ ИЗ ГЛАЗ
                if (FeatureManager.showDemonicRays) {
                    double headHeight = client.player.isSneaking() ? 1.4 : 1.7;
                    double lookX = client.player.getRotationVector().x * 0.4;
                    double lookZ = client.player.getRotationVector().z * 0.4;

                    client.world.addParticle(
                        net.minecraft.particle.ParticleTypes.FLAME,
                        px + lookX, py + headHeight, pz + lookZ,
                        lookX * 0.05, 0, lookZ * 0.05
                    );
                }
            }
        });

        // ОСНОВНОЙ ХОД РЕНДЕРИНГА ИНТЕРФЕЙСА (HUD)
        HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> {
            net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
            if (client.player == null || client.options.hudHidden) return;

            TextRenderer tr = client.textRenderer;
            int screenWidth = client.getWindow().getScaledWidth();
            int screenHeight = client.getWindow().getScaledHeight();

            // --- ЛОГИКА ТРЁХ РЕЖИМОВ TARGET HUD ---
            if (FeatureManager.targetHUD) {
                PlayerEntity target = TargetScanner.getTargetPlayer(30.0);

                if (target != null) {
                    float hp = target.getHealth();
                    float maxHp = target.getMaxHealth();
                    float hpPercent = Math.min(1.0f, Math.max(0.0f, hp / maxHp));
                    String name = target.getName().getString();

                    // РЕЖИМ 1: КЛАССИКА
                    if (FeatureManager.targetHudMode == 1) {
                        int tx = screenWidth / 2 - 70; int ty = screenHeight / 2 + 40;
                        drawContext.fill(tx, ty, tx + 140, ty + 40, 0xED101010);
                        drawContext.fill(tx, ty, tx + 140, ty + 2, FeatureManager.clientColor);
                        drawContext.drawText(tr, name, tx + 8, ty + 8, 0xFFFFFFFF, false);
                        drawContext.drawText(tr, (int)hp + " HP", tx + 105, ty + 8, FeatureManager.clientColor, false);
                        drawContext.fill(tx + 8, ty + 22, tx + 132, ty + 26, 0x25FFFFFF);
                        drawContext.fill(tx + 8, ty + 22, tx + 8 + (int) (124 * hpPercent), ty + 26, FeatureManager.clientColor);
                    }
                    // РЕЖИМ 2: 3D-МОДЕЛЬ
                    else if (FeatureManager.targetHudMode == 2) {
                        int tx = screenWidth / 2 - 80; int ty = screenHeight / 2 + 45;
                        drawContext.fill(tx, ty, tx + 160, ty + 45, 0xF50D0D0D);
                        drawContext.fill(tx, ty, tx + 2, ty + 45, FeatureManager.clientColor);
                        InventoryScreen.drawEntity(drawContext, tx + 22, ty + 38, 16, 0, 0, 0.0f, 0.0f, 0.0f, target);
                        drawContext.drawText(tr, name, tx + 42, ty + 8, 0xFFFFFFFF, false);
                        drawContext.drawText(tr, "HP: " + (int)hp + " / " + (int)maxHp, tx + 42, ty + 20, 0x80FFFFFF, false);
                        drawContext.fill(tx + 42, ty + 32, tx + 150, ty + 35, 0x20FFFFFF);
                        drawContext.fill(tx + 42, ty + 32, tx + 42 + (int)(108 * hpPercent), ty + 35, FeatureManager.clientColor);
                    }
                    // РЕЖИМ 3: МИКРО
                    else if (FeatureManager.targetHudMode == 3) {
                        int tx = screenWidth / 2 - 25; int ty = screenHeight / 2 + 15;
                        drawContext.fill(tx, ty, tx + 50, ty + 12, 0xCC111111);
                        drawContext.fill(tx, ty + 10, tx + (int) (50 * hpPercent), ty + 12, FeatureManager.clientColor);
                        String miniText = (int)hp + " HP";
                        drawContext.drawText(tr, miniText, tx + (50 - tr.getWidth(miniText)) / 2, ty + 1, 0xFFFFFFFF, false);
                    }
                }
            }

            // --- ОТРИСОВКА ПЛАШКИ ITEMSWAP ---
            if (FeatureManager.itemSwapVisual) {
                int x = 10; int y = 50;
                drawContext.fill(x, y, x + 100, y + 20, 0xC0101010);
                drawContext.fill(x, y, x + 2, y + 20, FeatureManager.clientColor);
                drawContext.drawText(tr, "ItemSwap: Active", x + 8, y + 6, 0xFFFFFFFF, false);
            }

            // --- ArmorHUD ---
            if (FeatureManager.armorHUD) {
                int ax = screenWidth / 2 + 15; int ay = screenHeight - 55;
                for (int i = 3; i >= 0; i--) {
                    net.minecraft.item.ItemStack armorItem = client.player.getInventory().getArmorStack(i);
                    if (!armorItem.isEmpty()) {
                        drawContext.drawItem(armorItem, ax, ay);
                        if (armorItem.isDamageable()) {
                            int percent = ((armorItem.getMaxDamage() - armorItem.getDamage()) * 100) / armorItem.getMaxDamage();
                            int durabilityColor = percent > 50 ? 0xFF00FF00 : (percent > 20 ? 0xFFFFD700 : 0xFFFF0000);
                            drawContext.drawText(tr, percent + "%", ax + 18, ay + 4, durabilityColor, true);
                        } else {
                            drawContext.drawText(tr, "100%", ax + 18, ay + 4, 0xFFFFFFFF, true);
                        }
                        ay += 16;
                    }
                }
            }

            // --- PotionHUD ---
            if (FeatureManager.potionHUD) {
                int px = screenWidth - 100; int py = 10;
                for (net.minecraft.entity.effect.StatusEffectInstance effect : client.player.getStatusEffects()) {
                    String effectName = effect.getEffectType().value().getName().getString();
                    int sec = effect.getDuration() / 20;
                    drawContext.fill(px, py, px + 90, py + 14, 0x90151515);
                    drawContext.fill(px + 88, py, px + 90, py + 14, FeatureManager.clientColor);
                    drawContext.drawText(tr, effectName, px + 5, py + 3, 0xFFFFFFFF, false);
                    drawContext.drawText(tr, String.format("%d:%02d", sec / 60, sec % 60), px + 55, py + 3, 0x80FFFFFF, false);
                    py += 16;
                }
            }

            // --- Glow ESP ---
            if (client.world != null) {
                if (FeatureManager.glowESP) {
                    for (net.minecraft.entity.Entity entity : client.world.getEntities()) {
                        if (entity instanceof net.minecraft.entity.ItemEntity) entity.setGlowing(true);
                    }
                } else {
                    for (net.minecraft.entity.Entity entity : client.world.getEntities()) {
                        if (entity instanceof net.minecraft.entity.ItemEntity && entity.isGlowing()) entity.setGlowing(false);
                    }
                }
            }
        });
    }
}
