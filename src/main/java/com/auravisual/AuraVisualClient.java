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

        // ОСНОВНОЙ ХОД РЕНДЕРИНГА ИНТЕРФЕЙСА (HUD)
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

                        // Вызов drawEntity с 10 аргументами под Майнкрафт 1.21.4
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
        });
    }
}
