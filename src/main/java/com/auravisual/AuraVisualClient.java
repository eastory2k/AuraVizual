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
    public static KeyBinding itemSwapKey;
    
    // Переменные для состояния нашей функции
    private boolean itemSwapActive = true;
    private int animationTimer = 0;

    @Override
    public void onInitializeClient() {
        // 1. Регистрируем кнопку смены (по умолчанию на R)
        itemSwapKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.auravisual.itemswap", 
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R, 
                "category.auravisual.general"
        ));

        // 2. Обработка нажатия кнопки
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                while (itemSwapKey.wasPressed()) {
                    // Переключаем режим или запускаем триггер анимации
                    itemSwapActive = !itemSwapActive;
                    animationTimer = 20; // Запускаем таймер вспышки HUD на 20 тиков (1 секунда)
                }
            }
            if (animationTimer > 0) {
                animationTimer--;
            }
        });

        // 3. РЕНДЕРИНГ HUD (Отрисовка красивой плашки на экране)
        HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> {
            net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
            if (client.player == null || client.options.hudHidden) return;

            TextRenderer textRenderer = client.textRenderer;

            // Настройки координат для нашего HUD (левый верхний угол под дебагом)
            int x = 10;
            int y = 50;
            int width = 110;
            int height = 24;

            // Рисуем красивый полупрозрачный задний фон (Тёмно-серый скругленный стиль)
            // Цвет в формате ARGB (Hex): 0x90101010 (90 - прозрачность, 101010 - почти черный)
            drawContext.fill(x, y, x + width, y + height, 0x90101010);

            // Рисуем тонкую обводку (окантовку) фиолетового "Аура" цвета
            int borderColor = itemSwapActive ? 0xFF8A2BE2 : 0xFF4A4A4A; // Фиолетовый если включен, серый если выключен
            drawContext.fill(x, y, x + width, y + 1, borderColor); // Верхняя линия

            // Выводим текст красивым шрифтом
            drawContext.drawText(textRenderer, "AuraVisual", x + 6, y + 4, 0xFFFFFFFF, true);
            
            String statusText = itemSwapActive ? "ItemSwap: READY" : "ItemSwap: OFF";
            int statusColor = itemSwapActive ? 0xFF00FF00 : 0xFFFF0000; // Зеленый или Красный
            
            drawContext.drawText(textRenderer, statusText, x + 6, y + 14, statusColor, true);

            // Если только что нажали кнопку — рисуем визуальный эффект "вспышки"
            if (animationTimer > 0) {
                drawContext.fill(x, y, x + width, y + height, 0x30FFFFFF); // Белый полупрозрачный оверлей
            }
        });
    }
}
