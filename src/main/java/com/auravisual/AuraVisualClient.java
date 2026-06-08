package com.auravisual;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.entity.player.PlayerEntity;
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
                    // ИСПРАВЛЕНО: Вместо isFallFlying() используем isGliding()
                    if (client.player.isSprinting() || client.player.isGliding()) {
                        speed = 0.35;
                    }
                    wingTimer += speed;
                } else {
                    wingTimer = 0;
                }
            }

            // --- МОДУЛЬ: Беспалевный TriggerBot (Обход PolarAC / SlothAC) ---
            if (FeatureManager.triggerBot && client.player != null && client.interactionManager != null) {
                if (client.crosshairTarget != null && client.crosshairTarget.getType() == net.minecraft.util.hit.HitResult.Type.ENTITY) {
                    net.minecraft.util.hit.EntityHitResult entityHit = (net.minecraft.util.hit.EntityHitResult) client.crosshairTarget;
                    net.minecraft.entity.Entity targetEntity = entityHit.getEntity();

                    if (targetEntity instanceof PlayerEntity && targetEntity.isAlive()) {
                        
                        boolean isFalling = client.player.fallDistance > 0.0f || (!client.player.isOnGround() && client.player.getVelocity().y < 0);
                        boolean canCrit = isFalling && !client.player.isClimbing() && !client.player.isTouchingWater();
                        
                        float randomCooldownThreshold = 0.93f + FeatureManager.random.nextFloat() * 0.07f; 
                        boolean isCharged = client.player.getAttackCooldownProgress(0.5f) >= randomCooldownThreshold;

                        if (isCharged && canCrit) {
                            if (FeatureManager.triggerDelayTicks == 0) {
                                FeatureManager.triggerDelayTicks = 2 + FeatureManager.random.nextInt(3); 
                            }
                            
                            if (FeatureManager.triggerDelayTicks > 0) {
                                FeatureManager.triggerDelayTicks--;
                                
                                if (FeatureManager.triggerDelayTicks == 0) {
                                    client.interactionManager.attackEntity(client.player, targetEntity);
                                    client.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
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

        // Рендеринг элементов интерфейса
        HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> {
            net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
            if (client.player == null || client.options.hudHidden) return;

            TextRenderer textRenderer = client.textRenderer;

            // --- МОДУЛЬ: TargetHUD (Классика, 3D Модель, Микро) ---
            if (FeatureManager.targetHUD) {
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
                        else if (FeatureManager.targetHudMode == 2) { // ИСПРАВЛЕНО: Безопасный 3D/Расширенный режим без краша drawEntity
                            drawContext.fill(posX, posY, posX + 140, posY + 40, 0xB0101010);
                            drawContext.drawText(textRenderer, "[ СЛЕДУЕТ ЗА ]", posX + 5, posY + 5, 0x80FFFFFF, true);
                            drawContext.drawText(textRenderer, target.getName().getString() + " (" + (int)target.getHealth() + " HP)", posX + 5, posY + 18, FeatureManager.clientColor, true);
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
                for (int i = 3; i >= 0; i--) {
                    net.minecraft.item.ItemStack stack = client.player.getInventory().getArmorStack(i);
                    if (!stack.isEmpty()) {
                        int durability = stack.getMaxDamage() - stack.getDamage();
                        drawContext.drawText(textRenderer, durability + "ед", armorX, armorY, 0xFFFFFF00, true);
                        armorX += 25;
                    }
                }
            }

            // --- МОДУЛЬ: PotionHUD (Эффекты зелий) ---
            if (FeatureManager.potionHUD) {
                int potY = 5;
                for (net.minecraft.entity.effect.StatusEffectInstance effect : client.player.getStatusEffects()) {
                    // ИСПРАВЛЕНО: Ванильное легитное форматирование времени эффекта
                    int durationTicks = effect.getDuration();
                    int seconds = (durationTicks / 20) % 60;
                    int minutes = (durationTicks / 1200);
                    String timeStr = String.format("%02d:%02d", minutes, seconds);
                    
                    String effectName = effect.getEffectType().value().getName().getString();
                    drawContext.drawText(textRenderer, effectName + " (" + timeStr + ")", 5, potY, 0xFF00FFCC, true);
                    potY += 12;
                }
            }

            // --- МОДУЛЬ: ItemSwap Visual ---
            if (FeatureManager.itemSwapVisual) {
                int swapX = client.getWindow().getScaledWidth() / 2 - 50;
                int swapY = client.getWindow().getScaledHeight() - 80;
                drawContext.fill(swapX, swapY, swapX + 100, swapY + 16, 0x50000000);
                drawContext.drawText(textRenderer, "ItemSwap Ready", swapX + 12, swapY + 4, FeatureManager.clientColor, true);
            }

            // --- ЛОГИКА GLOW ESP (ЛУТ) ---
            if (client.world != null) {
                for (net.minecraft.entity.Entity entity : client.world.getEntities()) {
                    if (entity instanceof net.minecraft.entity.ItemEntity) {
                        entity.setGlowing(FeatureManager.glowESP);
                    }
                }
            }

            // --- ЛОГИКА SOULSIGHT (ИГРОКИ) ---
            if (client.world != null) {
                for (net.minecraft.entity.Entity entity : client.world.getEntities()) {
                    if (entity instanceof PlayerEntity && entity != client.player) {
                        entity.setGlowing(FeatureManager.soulSight);
                    }
                }
            }
        });
    }

    public static double getWingTimer() {
        return wingTimer;
    }
}
