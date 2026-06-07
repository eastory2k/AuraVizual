package com.auravisual;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.font.TextRenderer;
import org.lwjgl.glfw.GLFW;

public class AuraVisualClient implements ClientModInitializer {
    public static KeyBinding openGuiKey;

    @Override
    public void onInitializeClient() {
        // Регистрируем кнопку открытия GUI на Правый Шифт (GLFW_KEY_RIGHT_SHIFT)
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.auravisual.opengui", 
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT, 
                "category.auravisual.general"
        ));

        // Открываем меню при нажатии
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && openGuiKey.wasPressed()) {
                client.setScreen(new ClickGUI());
            }
        });

        // СОВРЕМЕННЫЙ HUD С ПРОВЕРКОЙ НАСТРОЕК
        HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> {
            net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
            if (client.player == null || client.options.hudHidden) return;

            TextRenderer textRenderer = client.textRenderer;

            // Рендерим плашку ItemSwap только если она ВКЛЮЧЕНА в меню!
            if (FeatureManager.itemSwapVisual) {
                int x = 10;
                int y = 50;
                // Стильный гладкий виджет в стиле PulseVisual
                drawContext.fill(x, y, x + 100, y + 20, 0xC0101010); // Полупрозрачный фон
                drawContext.fill(x, y, x + 2, y + 20, FeatureManager.clientColor); // Боковая неоновая линия
                drawContext.drawText(textRenderer, "ItemSwap: Active", x + 8, y + 6, 0xFFFFFFFF, false);
            }

            // Рендерим TargetHUD, если он включен (пока сделаем красивую заглушку-каркас)
            if (FeatureManager.targetHUD) {
                int screenWidth = client.getWindow().getScaledWidth();
                int screenHeight = client.getWindow().getScaledHeight();
                
                // Центрируем TargetHUD чуть ниже прицела, как в Celestial
                int tx = screenWidth / 2 - 60;
                int ty = screenHeight / 2 + 30;

                drawContext.fill(tx, ty, tx + 120, ty + 35, 0xED111111);
                drawContext.fill(tx, ty, tx + 120, ty + 2, FeatureManager.clientColor); // Верхний неон
                
                // Пример отображения противника
                drawContext.drawText(textRenderer, "EnemyPlayer", tx + 8, ty + 8, 0xFFFFFFFF, false);
                
                // Полоска здоровья (красивый кастомный прогресс-бар)
                drawContext.fill(tx + 8, ty + 22, tx + 112, ty + 27, 0x30FFFFFF); // Фон полоски
                drawContext.fill(tx + 8, ty + 22, tx + 80, ty + 27, FeatureManager.clientColor); // Текущее ХП (например 70%)
            }
        });
    }
}
