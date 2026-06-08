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
        });

        // ОСНОВНОЙ ХОД РЕНДЕРИНГА ИНТЕРФЕЙСА И ЧАСТИЦ (HUD)
        HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> {
            net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
            if (client.player == null || client.options.hudHidden) return;

            TextRenderer tr = client.textRenderer;
            int screenWidth = client.getWindow().getScaledWidth();
            int screenHeight = client.getWindow().getScaledHeight();

            // --- ЛОГИКА ТРЁХ РЕЖИМОВ TARGET HUD ---
            if (FeatureManager.targetHUD) {
                // Ищем игрока в прицеле через наш сканер
                PlayerEntity target = TargetScanner.getTargetPlayer(30.0);

                if (target != null) {
                    float hp = target.getHealth();
                    float maxHp = target.getMaxHealth();
                    float hpPercent = Math.min(1.0f, Math.max(0.0f, hp / maxHp));
                    String name = target.getName().getString();

                    // РЕЖИМ 1: КЛАССИКА (В стиле SoupAPI)
                    if (FeatureManager.targetHudMode == 1) {
                        int tx = screenWidth / 2 - 70;
                        int ty = screenHeight / 2 + 40;

                        // Тёмная подложка и неоновая линия сверху
                        drawContext.fill(tx, ty, tx + 140, ty + 40, 0xED101010);
                        drawContext.fill(tx, ty, tx + 140, ty + 2, FeatureManager.clientColor);
                        
                        // Имя и количество ХП текстом
                        drawContext.drawText(tr, name, tx + 8, ty + 8, 0xFFFFFFFF, false);
                        drawContext.drawText(tr, (int)hp + " HP", tx + 105, ty + 8, FeatureManager.clientColor, false);

                        // Полоска здоровья
                        drawContext.fill(tx + 8, ty + 22, tx + 132, ty + 26, 0x25FFFFFF);
                        int barWidth = (int) (124 * hpPercent);
                        drawContext.fill(tx + 8, ty + 22, tx + 8 + barWidth, ty + 26, FeatureManager.clientColor);
                    }

                    // РЕЖИМ 2: 3D-МОДЕЛЬ (В стиле Celestial / PulseVisual)
                    else if (FeatureManager.targetHudMode == 2) {
                        int tx = screenWidth / 2 - 80;
                        int ty = screenHeight / 2 + 45;

                        drawContext.fill(tx, ty, tx + 160, ty + 45, 0xF50D0D0D);
                        drawContext.fill(tx, ty, tx + 2, ty + 45, FeatureManager.clientColor);

                        // Вызов drawEntity под Майнкрафт 1.21.4
                        int entityX = tx + 22;
                        int entityY = ty + 38;
                        int size = 16;
                        
                        InventoryScreen.drawEntity(
                            drawContext, 
                            entityX, entityY, 
                            size,             
                            0, 0,             
                            0.0f,             
                            0.0f, 0.0f,       
                            target            
                        );

                        // Текстовая информация справа от скина врага
                        drawContext.drawText(tr, name, tx + 42, ty + 8, 0xFFFFFFFF, false);
                        drawContext.drawText(tr, "HP: " + (int)hp + " / " + (int)maxHp, tx + 42, ty + 20, 0x80FFFFFF, false);

                        // Нижний индикатор здоровья под моделью
                        drawContext.fill(tx + 42, ty + 32, tx + 150, ty + 35, 0x20FFFFFF);
                        int barWidth2 = (int) (108 * hpPercent);
                        drawContext.fill(tx + 42, ty + 32, tx + 42 + barWidth2, ty + 35, FeatureManager.clientColor);
                    }

                    // РЕЖИМ 3: МИКРО / МИНИМАЛИЗМ (Прямо под прицелом)
                    else if (FeatureManager.targetHudMode == 3) {
                        int tx = screenWidth / 2 - 25;
                        int ty = screenHeight / 2 + 15;

                        drawContext.fill(tx, ty, tx + 50, ty + 12, 0xCC111111);
                        int barWidth3 = (int) (50 * hpPercent);
                        drawContext.fill(tx, ty + 10, tx + barWidth3, ty + 12, FeatureManager.clientColor);
                        
                        String miniText = (int)hp + " HP";
                        int textX = tx + (50 - tr.getWidth(miniText)) / 2;
                        drawContext.drawText(tr, miniText, textX, ty + 1, 0xFFFFFFFF, false);
                    }
                }
            }

            // --- ОТРИСОВКА ПЛАШКИ ITEMSWAP ---
            if (FeatureManager.itemSwapVisual) {
                int x = 10;
                int y = 50;
                drawContext.fill(x, y, x + 100, y + 20, 0xC0101010);
                drawContext.fill(x, y, x + 2, y + 20, FeatureManager.clientColor);
                drawContext.drawText(tr, "ItemSwap: Active", x + 8, y + 6, 0xFFFFFFFF, false);
            }

            // --- Категория: ИНФО-ПАНЕЛИ (ArmorHUD) ---
            if (FeatureManager.armorHUD) {
                int ax = screenWidth / 2 + 15;
                int ay = screenHeight - 55;

                for (int i = 3; i >= 0; i--) {
                    net.minecraft.item.ItemStack armorItem = client.player.getInventory().getArmorStack(i);
                    if (!armorItem.isEmpty()) {
                        drawContext.drawItem(armorItem, ax, ay);
                        
                        if (armorItem.isDamageable()) {
                            int maxDamage = armorItem.getMaxDamage();
                            int currentDamage = maxDamage - armorItem.getDamage();
                            int percent = (currentDamage * 100) / maxDamage;
                            
                            int durabilityColor = percent > 50 ? 0xFF00FF00 : (percent > 20 ? 0xFFFFD700 : 0xFFFF0000);
                            drawContext.drawText(tr, percent + "%", ax + 18, ay + 4, durabilityColor, true);
                        } else {
                            drawContext.drawText(tr, "100%", ax + 18, ay + 4, 0xFFFFFFFF, true);
                        }
                        ay += 16;
                    }
                }
            }

            // --- Категория: ИНФО-ПАНЕЛИ (PotionHUD) ---
            if (FeatureManager.potionHUD) {
                int px = screenWidth - 100;
                int py = 10;

                for (net.minecraft.entity.effect.StatusEffectInstance effect : client.player.getStatusEffects()) {
                    String effectName = effect.getEffectType().value().getName().getString();
                    int durationTicks = effect.getDuration();
                    
                    int totalSeconds = durationTicks / 20;
                    int minutes = totalSeconds / 60;
                    int seconds = totalSeconds % 60;
                    String timeText = String.format("%d:%02d", minutes, seconds);

                    drawContext.fill(px, py, px + 90, py + 14, 0x90151515);
                    drawContext.fill(px + 88, py, px + 90, py + 14, FeatureManager.clientColor);

                    drawContext.drawText(tr, effectName, px + 5, py + 3, 0xFFFFFFFF, false);
                    drawContext.drawText(tr, timeText, px + 55, py + 3, 0x80FFFFFF, false);

                    py += 16;
                }
            }

            // --- Категория: ВИЗУАЛ И МИР (Glow ESP для лута) ---
            if (client.world != null) {
                if (FeatureManager.glowESP) {
                    for (net.minecraft.entity.Entity entity : client.world.getEntities()) {
                        if (entity instanceof net.minecraft.entity.ItemEntity) {
                            entity.setGlowing(true);
                        }
                    }
                } else {
                    // Гасим свечение предметов, если модуль выключили
                    for (net.minecraft.entity.Entity entity : client.world.getEntities()) {
                        if (entity instanceof net.minecraft.entity.ItemEntity && entity.isGlowing()) {
                            entity.setGlowing(false);
                        }
                    }
                }
            }

            // --- Категория: ВИЗУАЛ И МИР (Движущиеся призраки вокруг цели) ---
            if (FeatureManager.customParticles && client.world != null) {
                PlayerEntity particleTarget = TargetScanner.getTargetPlayer(30.0);

                if (particleTarget != null) {
                    long time = client.world.getTime();
                    
                    double entityX = particleTarget.getX();
                    double entityY = particleTarget.getY();
                    double entityZ = particleTarget.getZ();

                    // Спавним 3 призрачных орба, летящих по спирали вверх
                    for (int j = 0; j < 3; j++) {
                        double angle = (time * 0.2) + (j * 2.0);
                        double radius = 0.8;

                        double offsetX = Math.sin(angle) * radius;
                        double offsetZ = Math.cos(angle) * radius;
                        double offsetY = 0.2 + ((time + j * 10) % 40) * 0.04;

                        client.world.addParticle(
                            net.minecraft.particle.ParticleTypes.WITCH, 
                            entityX + offsetX, 
                            entityY + offsetY, 
                            entityZ + offsetZ, 
                            0, 0, 0
                        );
                    }
                }
            }
        });
    }
}
