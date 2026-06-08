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
        this.renderBackground(context, mouseX, mouseY, delta);

        // Сделаем окно чуть шире, так как функций стало много
        int width = 240;
        int height = 240;
        int x = (this.width - width) / 2;
        int y = (this.height - height) / 2;

        // Главный фон
        context.fill(x, y, x + width, y + height, 0xED121212); 
        context.fill(x, y, x + width, y + 2, FeatureManager.clientColor); 

        // Заголовок
        context.drawText(this.textRenderer, "AURA VISUAL // CONFIG", x + 12, y + 10, FeatureManager.clientColor, false);
        context.drawText(this.textRenderer, "v1.0.0", x + width - 45, y + 10, 0x50FFFFFF, false);

        context.fill(x + 10, y + 24, x + width - 10, y + 25, 0x15FFFFFF);

        // Начальная координата для отрисовки кнопок
        int bx = x + 12;
        int bWidth = width - 24;
        int startY = y + 32;
        int stepY = 22; // Расстояние между кнопками

        // 1. TargetHUD с выбором режимов
        String thModeName = "Классика";
        if (FeatureManager.targetHudMode == 2) thModeName = "3D Модель";
        if (FeatureManager.targetHudMode == 3) thModeName = "Микро";
        String thText = "TargetHUD [" + thModeName + "]";
        drawBtn(context, bx, startY, bWidth, thText, FeatureManager.targetHUD, mouseX, mouseY);

        // 2. Инфо-панели
        drawBtn(context, bx, startY + stepY, bWidth, "ArmorHUD (Броня и Сферы)", FeatureManager.armorHUD, mouseX, mouseY);
        drawBtn(context, bx, startY + stepY * 2, bWidth, "PotionHUD (Эффекты)", FeatureManager.potionHUD, mouseX, mouseY);
        drawBtn(context, bx, startY + stepY * 3, bWidth, "KeyStrokes (Клавиши)", FeatureManager.keyStrokes, mouseX, mouseY);

        // 3. Визуал и Мир
        drawBtn(context, bx, startY + stepY * 4, bWidth, "Glow / ESP (Подсветка Лута)", FeatureManager.glowESP, mouseX, mouseY);
        drawBtn(context, bx, startY + stepY * 5, bWidth, "Custom Particles (Криты)", FeatureManager.customParticles, mouseX, mouseY);
        drawBtn(context, bx, startY + stepY * 6, bWidth, "StaffAlert (Админ-Чекалка)", FeatureManager.staffAlert, mouseX, mouseY);
        
        // 4. Базовые
        drawBtn(context, bx, startY + stepY * 7, bWidth, "ItemSwap Visual", FeatureManager.itemSwapVisual, mouseX, mouseY);
        drawBtn(context, bx, startY + stepY * 8, bWidth, "Dynamic Island", FeatureManager.dynamicIsland, mouseX, mouseY);

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawBtn(DrawContext ctx, int bx, int by, int bWidth, String name, boolean active, int mx, int my) {
        boolean hovered = mx >= bx && mx <= bx + bWidth && my >= by && my <= by + 18;
        int bgColor = hovered ? 0x25FFFFFF : 0x10FFFFFF;
        
        // Рисуем задний план кнопки
        ctx.fill(bx, by, bx + bWidth, by + 18, bgColor);
        
        // Текст названия кнопки
        ctx.drawText(this.textRenderer, name, bx + 6, by + 5, 0xFFE0E0E0, false);
        
        // Текст статуса
        String status = active ? "• ON" : "• OFF";
        int statusColor = active ? FeatureManager.clientColor : 0x40FFFFFF;
        ctx.drawText(this.textRenderer, status, bx + bWidth - 40, by + 5, statusColor, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int width = 240;
        int x = (this.width - width) / 2;
        int y = (this.height - 240) / 2;
        
        int bx = x + 12;
        int bWidth = width - 24;
        int startY = y + 32;
        int stepY = 22;

        if (mouseX >= bx && mouseX <= bx + bWidth) {
            // Клик по TargetHUD
            if (mouseY >= startY && mouseY <= startY + 18) {
                if (!FeatureManager.targetHUD) {
                    FeatureManager.targetHUD = true;
                    FeatureManager.targetHudMode = 1;
                } else if (FeatureManager.targetHudMode == 1) {
                    FeatureManager.targetHudMode = 2;
                } else if (FeatureManager.targetHudMode == 2) {
                    FeatureManager.targetHudMode = 3;
                } else {
                    FeatureManager.targetHUD = false;
                }
                return true;
            }
            // ArmorHUD
            if (mouseY >= startY + stepY && mouseY <= startY + stepY + 18) {
                FeatureManager.armorHUD = !FeatureManager.armorHUD;
                return true;
            }
            // PotionHUD
            if (mouseY >= startY + stepY * 2 && mouseY <= startY + stepY * 2 + 18) {
                FeatureManager.potionHUD = !FeatureManager.potionHUD;
                return true;
            }
            // KeyStrokes
            if (mouseY >= startY + stepY * 3 && mouseY <= startY + stepY * 3 + 18) {
                FeatureManager.keyStrokes = !FeatureManager.keyStrokes;
                return true;
            }
            // GlowESP
            if (mouseY >= startY + stepY * 4 && mouseY <= startY + stepY * 4 + 18) {
                FeatureManager.glowESP = !FeatureManager.glowESP;
                return true;
            }
            // CustomParticles
            if (mouseY >= startY + stepY * 5 && mouseY <= startY + stepY * 5 + 18) {
                FeatureManager.customParticles = !FeatureManager.customParticles;
                return true;
            }
            // StaffAlert
            if (mouseY >= startY + stepY * 6 && mouseY <= startY + stepY * 6 + 18) {
                FeatureManager.staffAlert = !FeatureManager.staffAlert;
                return true;
            }
            // ItemSwap
            if (mouseY >= startY + stepY * 7 && mouseY <= startY + stepY * 7 + 18) {
                FeatureManager.itemSwapVisual = !FeatureManager.itemSwapVisual;
                return true;
            }
            // Dynamic Island
            if (mouseY >= startY + stepY * 8 && mouseY <= startY + stepY * 8 + 18) {
                FeatureManager.dynamicIsland = !FeatureManager.dynamicIsland;
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
