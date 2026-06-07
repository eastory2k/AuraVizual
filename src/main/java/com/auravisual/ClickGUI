package com.auravisual;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class ClickGUI extends Screen {

    public ClickGUI() {
        super(Text.literal("AuraVisual ClickGUI"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 1. Рисуем красивый задний полупрозрачный фон на весь экран (затемнение игры)
        this.renderBackground(context, mouseX, mouseY, delta);

        // Размеры главного окна меню
        int width = 200;
        int height = 180;
        int x = (this.width - width) / 2;
        int y = (this.height - height) / 2;

        // 2. Главная панель меню (Тёмный современный минимализм)
        context.fill(x, y, x + width, y + height, 0xED151515); // Основной фон (почти черный)
        context.fill(x, y, x + width, y + 2, FeatureManager.clientColor); // Фирменная неоновая полоска сверху

        // 3. Заголовок меню
        context.drawText(this.textRenderer, "AURA VISUAL", x + 10, y + 10, FeatureManager.clientColor, false);
        context.drawText(this.textRenderer, "v1.0.0", x + width - 40, y + 10, 0x60FFFFFF, false); // Серый текст версии

        // Разделительная линия
        context.fill(x + 10, y + 24, x + width - 10, y + 25, 0x20FFFFFF);

        // 4. Отрисовка интерактивных кнопок-модулей
        int buttonY = y + 35;

        // Кнопка 1: ItemSwap
        drawModuleButton(context, x + 10, buttonY, width - 20, "ItemSwap Animation", FeatureManager.itemSwapVisual, mouseX, mouseY);
        
        // Кнопка 2: TargetHUD
        drawModuleButton(context, x + 10, buttonY + 30, width - 20, "Target HUD", FeatureManager.targetHUD, mouseX, mouseY);

        // Кнопка 3: DynamicIsland
        drawModuleButton(context, x + 10, buttonY + 60, width - 20, "Dynamic Island", FeatureManager.dynamicIsland, mouseX, mouseY);

        super.render(context, mouseX, mouseY, delta);
    }

    // Вспомогательный метод для рисования стильных кнопок
    private void drawModuleButton(DrawContext context, int bx, int by, int bWidth, String name, boolean enabled, int mouseX, int mouseY) {
        // Проверяем, наведена ли мышка на кнопку (Hover эффект)
        boolean hovered = mouseX >= bx && mouseX <= bx + bWidth && mouseY >= by && mouseY <= by + 22;
        
        // Цвет фона кнопки (становится чуть светлее при наведении)
        int backgroundColor = hovered ? 0x35FFFFFF : 0x15FFFFFF;
        context.fill(bx, by, bx + bWidth, by + 22, backgroundColor);

        // Текст модуля
        context.drawText(this.textRenderer, name, bx + 8, by + 7, 0xFFFFFFFF, false);

        // Индикатор состояния (Вкл/Выкл) в стиле чекбокса
        String stateText = enabled ? "ENABLED" : "DISABLED";
        int stateColor = enabled ? FeatureManager.clientColor : 0x50FFFFFF;
        context.drawText(this.textRenderer, stateText, bx + bWidth - 60, by + 7, stateColor, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int width = 200;
        int x = (this.width - width) / 2;
        int y = (this.height - 180) / 2;
        int buttonY = y + 35;

        // Обработка кликов по кнопкам (переключение true/false)
        if (mouseX >= x + 10 && mouseX <= x + width - 10) {
            if (mouseY >= buttonY && mouseY <= buttonY + 22) {
                FeatureManager.itemSwapVisual = !FeatureManager.itemSwapVisual;
                return true;
            }
            if (mouseY >= buttonY + 30 && mouseY <= buttonY + 32 + 22) {
                FeatureManager.targetHUD = !FeatureManager.targetHUD;
                return true;
            }
            if (mouseY >= buttonY + 60 && mouseY <= buttonY + 62 + 22) {
                FeatureManager.dynamicIsland = !FeatureManager.dynamicIsland;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
return false; // Чтобы игра не вставала на паузу в сетевом режиме при открытии меню
    }
}
