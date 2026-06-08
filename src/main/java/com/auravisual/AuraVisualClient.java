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
    private static double wingTimer = 0;

    @Override
    public void onInitializeClient() {
        // Регистрация клавиши открытия меню (Правый Шифт)
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.auravisual.opengui", 
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT, 
                "category.auravisual.general"
        ));

        // Основной игровой тик клиента
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && openGuiKey.wasPressed()) {
                client.setScreen(new ClickGUI());
            }

            if (client.world != null && client.player != null && !client.options.hudHidden) {
                // --- КОСМЕТИКА: Расчет махов кастомных крыльев ---
                if (FeatureManager.showWings) {
                    double speed = 0.15;
                    // Если игрок бежит или летит на элитрах — крылья машут быстрее
                    if (client.player.isSprinting() || client.player.isFallFlying()) {
                        speed = 0.35;
                    }
                    wingTimer += speed;
                } else {
                    wingTimer = 0;
                }
            }

            // --- МОДУЛЬ: Беспалевный TriggerBot (Обход PolarAC / SlothAC) ---
            if (FeatureManager.triggerBot && client.player != null && client.interactionManager != null) {
                // Проверяем, наведено ли перекрестие на сущность
                if (client.crosshairTarget != null && client.crosshairTarget.getType() == net.minecraft.util.hit.HitResult.Type.ENTITY) {
                    net.minecraft.util.hit.EntityHitResult entityHit = (net.minecraft.util.hit.EntityHitResult) client.crosshairTarget;
                    net.minecraft.entity.Entity targetEntity = entityHit.getEntity();

                    if (targetEntity instanceof PlayerEntity && targetEntity.isAlive()) {
                        
                        // 1. Проверяем условия для КРИТА (игрок должен падать)
                        boolean isFalling = client.player.fallDistance > 0.0f || (!client.player.isOnGround() && client.player.getVelocity().y < 0);
                        boolean canCrit = isFalling && !client.player.isClimbing() && !client.player.isTouchingWater();
                        
                        // 2. Проверяем кулдаун атаки с рандомным смещением (имитация неидеального клика)
                        float randomCooldownThreshold = 0.93f + FeatureManager.random.nextFloat() * 0.07f; 
                        boolean isCharged = client.player.getAttackCooldownProgress(0.5f) >= randomCooldownThreshold;

                        if (isCharged && canCrit) {
                            // Если таймер задержки еще не запущен, генерируем случайную паузу (в тиках)
                            // 1 тик = 50 мс. Задержка 2-4 тика = 100-200 мс (симуляция реакции человека)
                            if (FeatureManager.triggerDelayTicks == 0) {
                                FeatureManager.triggerDelayTicks = 2 + FeatureManager.random.nextInt(3); 
                            }
                            
                            // Уменьшаем таймер задержки каждый тик
                            if (FeatureManager.triggerDelayTicks > 0) {
                                FeatureManager.triggerDelayTicks--;
                                
                                // Когда задержка полностью прошла — совершаем легитный удар
                                if (FeatureManager.triggerDelayTicks == 0) {
                                    client.interactionManager.attackEntity(client.player, targetEntity);
                                    client.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
                                    
                                    // Сбрасываем кулдаун в коде мода
                                    FeatureManager.triggerDelayTicks = 0;
                                }
                            }
                        }
                    } else {
                        FeatureManager.triggerDelayTicks = 0;
                    }
                } else {
                    FeatureManager.triggerDelayTicks = 0;
                }
            }
        });

        // Калбэк для рендеринга элементов интерфейса и визуалов мира
        HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> {
            net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
            if (client.player == null || client.options.hudHidden) return;

            TextRenderer textRenderer = client.textRenderer;

            // --- МОДУЛЬ: TargetHUD (Классика, 3D Модель, Микро) ---
            if (FeatureManager.targetHUD) {
                // Логика поиска текущей цели (для примера берем фокус или ближайшего)
                if (client.crosshairTarget != null && client.crosshairTarget.getType() == net.minecraft.util.hit.HitResult.Type.ENTITY) {
                    net.minecraft.util.hit.EntityHitResult entityHit = (net.minecraft.util.hit.EntityHitResult) client.crosshairTarget;
                    if (entityHit.getEntity() instanceof PlayerEntity target) {
                        
                        int posX = 10;
                        int posY = 50;

                        if (FeatureManager.targetHudMode == 1) { // Классический режим
                            drawContext.fill(posX, posY, posX + 140, posY + 40, 0x90000000);
                            drawContext.drawText(textRenderer, target.getName().getString(), posX + 5, posY + 5, 0xFFFFFFFF, true);
                            drawContext.drawText(textRenderer, "HP: " + (int)target.getHealth(), posX + 5, posY + 18, FeatureManager.clientColor, true);
                        } 
                        else if (FeatureManager.targetHudMode == 2) { // 3D Модель режим
                            drawContext.fill(posX, posY, posX + 160, posY + 50, 0xB0101010);
                            // Отрисовка мини-модели игрока на экране
                            InventoryScreen.drawEntity(drawContext, posX + 25, posY + 45, 20, (float)(posX + 25) - posX, (float)(posY + 20) - posY, target);
                            drawContext.drawText(textRenderer, target.getName().getString(), posX + 50, posY + 10, 0xFFFFFFFF, true);
                            drawContext.drawText(textRenderer, "Health: " + (int)target.getHealth(), posX + 50, posY + 25, FeatureManager.clientColor, true);
                        } 
                        else if (FeatureManager.targetHudMode == 3) { // Микро режим
                            drawContext.fill(posX, posY, posX + 90, posY + 18, 0x70000000);
                            String microInfo = target.getName().getString() + " | " + (int)target.getHealth() + "♥";
                            drawContext.drawText(textRenderer, microInfo, posX + 4, posY + 5, FeatureManager.clientColor, true);
                        }
                    }
                }
            }

            // --- МОДУЛЬ: ArmorHUD (Отображение прочности брони) ---
            if (FeatureManager.armorHUD) {
                int armorX = client.getWindow().getScaledWidth() / 2 + 15;
                int armorY = client.getWindow().getScaledHeight() - 55;
                // Перебор и вывод прочности экипированных предметов брони
                for (int i = 3; i >= 0; i--) {
                    net.minecraft.item.ItemStack stack = client.player.getInventory().getArmorStack(i);
                    if (!stack.isEmpty()) {
                        int durability = stack.getMaxDamage() - stack.getDamage();
                        drawContext.drawText(textRenderer, durability + "ед", armorX, armorY, 0xFFFFFF00, true);
                        armorX += 25;
                    }
                }
            }

            // --- МОДУЛЬ: PotionHUD (Отображение активных эффектов зелий) ---
            if (FeatureManager.potionHUD) {
                int potY = 5;
                for (net.minecraft.entity.effect.StatusEffectInstance effect : client.player.getStatusEffects()) {
                    String duration = net.minecraft.util.Util.formatDuration(effect, 1.0f);
                    String effectName = effect.getEffectType().value().getName().getString();
                    drawContext.drawText(textRenderer, effectName + " (" + duration + ")", 5, potY, 0xFF00FFCC, true);
                    potY += 12;
                }
            }

            // --- МОДУЛЬ: ItemSwap Visual (Индикатор быстрой смены предметов) ---
            if (FeatureManager.itemSwapVisual) {
                int swapX = client.getWindow().getScaledWidth() / 2 - 50;
                int swapY = client.getWindow().getScaledHeight() - 80;
                drawContext.fill(swapX, swapY, swapX + 100, swapY + 16, 0x50000000);
                drawContext.drawText(textRenderer, "ItemSwap Ready", swapX + 12, swapY + 4, FeatureManager.clientColor, true);
            }

            // --- ЛОГИКА GLOW ESP (ЛУТ / ПРЕДМЕТЫ НА ЗЕМЛЕ) ---
            if (client.world != null) {
                for (net.minecraft.entity.Entity entity : client.world.getEntities()) {
                    if (entity instanceof net.minecraft.entity.ItemEntity) {
                        entity.setGlowing(FeatureManager.glowESP);
                    }
                }
            }

            // --- ЛОГИКА SOULSIGHT (СВЕЧЕНИЕ ИГРОКОВ) ---
            if (client.world != null) {
                for (net.minecraft.entity.Entity entity : client.world.getEntities()) {
                    if (entity instanceof PlayerEntity && entity != client.player) {
                        entity.setGlowing(FeatureManager.soulSight);
                    }
                }
            }
        });
    }

    // Вспомогательный геттер для анимации кастомной косметики (если понадобится в миксинах)
    public static double getWingTimer() {
        return wingTimer;
    }
}
