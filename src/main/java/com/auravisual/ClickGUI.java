package com.auravisual;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class ClickGUI extends Screen {
    // Вкладки: 0 - БОЙ, 1 - ВИЗУАЛЫ, 2 - КОСМЕТИКА
    private int currentTab = 0; 

    public ClickGUI() {
        super(Text.literal("AuraVisual ClickGUI"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        int width = 240;
        int height = 230;
        int x = (this.width - width) / 2;
        int y = (this.height - height) / 2;

        // Основной темный фон меню
        context.fill(x, y, x + width, y + height, 0xFA0F0F0F); 
        context.fill(x, y, x + width, y + 2, FeatureManager.clientColor); 

        // Шапка меню
        context.drawText(this.textRenderer, "AURA VISUAL // MENU", x + 12, y + 10, FeatureManager.clientColor, false);
        context.drawText(this.textRenderer, "v1.1.0", x + width - 45, y + 10, 0x50FFFFFF, false);

        int tabY = y + 26;
        
        // Области наведения на вкладки
        boolean hoverTab0 = mouseX >= x + 12 && mouseX <= x + 60 && mouseY >= tabY && mouseY <= tabY + 14;
        boolean hoverTab1 = mouseX >= x + 70 && mouseX <= x + 140 && mouseY >= tabY && mouseY <= tabY + 14;
        boolean hoverTab2 = mouseX >= x + 150 && mouseX <= x + 230 && mouseY >= tabY && mouseY <= tabY + 14;

        int t0Color = currentTab == 0 ? FeatureManager.clientColor : (hoverTab0 ? 0xC0FFFFFF : 0x60FFFFFF);
        context.drawText(this.textRenderer, "[БОЙ]", x + 12, tabY, t0Color, false);

        int t1Color = currentTab == 1 ? FeatureManager.clientColor : (hoverTab1 ? 0xC0FFFFFF : 0x60FFFFFF);
        context.drawText(this.textRenderer, "[ВИЗУАЛЫ]", x + 70, tabY, t1Color, false);

        int t2Color = currentTab == 2 ? FeatureManager.clientColor : (hoverTab2 ? 0xC0FFFFFF : 0x60FFFFFF);
        context.drawText(this.textRenderer, "[КОСМЕТИКА]", x + 150, tabY, t2Color, false);

        // Тонкая разделительная линия под вкладками
        context.fill(x + 10, y + 42, x + width - 10, y + 43, 0x20FFFFFF);

        int bx = x + 12;
        int bWidth = width - 24;
        int startY = y + 52;
        int stepY = 22;

        // ОТРИСОВКА В ВКЛАДКАХ
        if (currentTab == 0) {
            // Рисуем кнопку TriggerBot (теперь она будет серой и заметной)
            drawBtn(context, bx, startY, bWidth, "TriggerBot (Только Криты)", FeatureManager.triggerBot, mouseX, mouseY);
            
            // Сдвигаем текстовое описание в самый-самый низ окна (на y + 150), чтобы оно не мешало кликам
            int infoY = y + 160;
            context.fill(x + 10, infoY, x + width - 10, infoY + 1, 0x15FFFFFF); // линия над описанием
            context.drawText(this.textRenderer, "TriggerBot автоматически бьет игрока,", x + 14, infoY + 10, 0x50FFFFFF, false);
            context.drawText(this.textRenderer, "когда он находится в вашем прицеле.", x + 14, infoY + 22, 0x50FFFFFF, false);
        }
        else if (currentTab == 1) {
            String thModeName = "Классика";
            if (FeatureManager.targetHudMode == 2) thModeName = "3D Модель";
            if (FeatureManager.targetHudMode == 3) thModeName = "Микро";
            
            drawBtn(context, bx, startY, bWidth, "TargetHUD [" + thModeName + "]", FeatureManager.targetHUD, mouseX, mouseY);
            drawBtn(context, bx, startY + stepY, bWidth, "ArmorHUD (Броня)", FeatureManager.armorHUD, mouseX, mouseY);
            drawBtn(context, bx, startY + stepY * 2, bWidth, "PotionHUD (Эффекты)", FeatureManager.potionHUD, mouseX, mouseY);
            drawBtn(context, bx, startY + stepY * 3, bWidth, "Glow / ESP (Предметы)", FeatureManager.glowESP, mouseX, mouseY);
            drawBtn(context, bx, startY + stepY * 4, bWidth, "SoulSight (Игроки)", FeatureManager.soulSight, mouseX, mouseY);
            drawBtn(context, bx, startY + stepY * 5, bWidth, "Custom Particles (Криты)", FeatureManager.customParticles, mouseX, mouseY);
            drawBtn(context, bx, startY + stepY * 6, bWidth, "ItemSwap Visual", FeatureManager.itemSwapVisual, mouseX, mouseY);
            
            drawColorBtn(context, bx, startY + stepY * 8, bWidth, "Цвет Мода: " + FeatureManager.getColorName(), mouseX, mouseY);
        }
        else if (currentTab == 2) {
            drawBtn(context, bx, startY, bWidth, "Кастомные Крылья", FeatureManager.showWings, mouseX, mouseY);
            drawBtn(context, bx, startY + stepY, bWidth, "Призрачный Нимб", FeatureManager.showHat, mouseX, mouseY);
            drawBtn(context, bx, startY + stepY * 2, bWidth, "Демонический Взгляд", FeatureManager.showDemonicRays, mouseX, mouseY);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawBtn(DrawContext ctx, int bx, int by, int bWidth, String name, boolean active, int mx, int my) {
        boolean hovered = mx >= bx && mx <= bx + bWidth && my >= by && my <= by + 18;
        // ИСПРАВЛЕНО: Сделали видимый цвет подложки кнопок (0x1F222222), чтобы они не сливались с фоном
        int bgColor = hovered ? 0x30FFFFFF : 0x1F222222; 
        
        ctx.fill(bx, by, bx + bWidth, by + 18, bgColor);
        ctx.drawText(this.textRenderer, name, bx + 6, by + 5, 0xFFE0E0E0, false);
        
        String status = active ? "• ON" : "• OFF";
        int statusColor = active ? FeatureManager.clientColor : 0x50FFFFFF;
        ctx.drawText(this.textRenderer, status, bx + bWidth - 40, by + 5, statusColor, false);
    }

    private void drawColorBtn(DrawContext ctx, int bx, int by, int bWidth, String name, int mx, int my) {
        boolean hovered = mx >= bx && mx <= bx + bWidth && my >= by && my <= by + 18;
        int bgColor = hovered ? 0x35FFFFFF : 0x1F222222;
        ctx.fill(bx, by, bx + bWidth, by + 18, bgColor);
        ctx.fill(bx, by, bx + 3, by + 18, FeatureManager.clientColor);
        ctx.drawText(this.textRenderer, name, bx + 10, by + 5, 0xFFFFFFFF, false);
        ctx.drawText(this.textRenderer, "[ СМЕНИТЬ ]", bx + bWidth - 65, by + 5, FeatureManager.clientColor, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int width = 240;
        int height = 230;
        int x = (this.width - width) / 2;
        int y = (this.height - height) / 2;
        
        int bx = x + 12;
        int bWidth = width - 24;
        int startY = y + 52;
        int stepY = 22;

        // Клик по вкладкам
        if (mouseY >= y + 24 && mouseY <= y + 40) {
            if (mouseX >= x + 12 && mouseX <= x + 60) { currentTab = 0; return true; }
            if (mouseX >= x + 70 && mouseX <= x + 140) { currentTab = 1; return true; }
            if (mouseX >= x + 150 && mouseX <= x + 230) { currentTab = 2; return true; }
        }

        // Клик по кнопкам внутри вкладок
        if (mouseX >= bx && mouseX <= bx + bWidth) {
            if (currentTab == 0) {
                // Точная область клика по верхней кнопке TriggerBot
                if (mouseY >= startY && mouseY <= startY + 18) { 
                    FeatureManager.triggerBot = !FeatureManager.triggerBot; 
                    return true; 
                }
            }
            else if (currentTab == 1) {
                if (mouseY >= startY && mouseY <= startY + 18) {
                    if (!FeatureManager.targetHUD) { FeatureManager.targetHUD = true; FeatureManager.targetHudMode = 1; }
                    else if (FeatureManager.targetHudMode == 1) FeatureManager.targetHudMode = 2;
                    else if (FeatureManager.targetHudMode == 2) FeatureManager.targetHudMode = 3;
                    else FeatureManager.targetHUD = false;
                    return true;
                }
                if (mouseY >= startY + stepY && mouseY <= startY + stepY + 18) { FeatureManager.armorHUD = !FeatureManager.armorHUD; return true; }
                if (mouseY >= startY + stepY * 2 && mouseY <= startY + stepY * 2 + 18) { FeatureManager.potionHUD = !FeatureManager.potionHUD; return true; }
                if (mouseY >= startY + stepY * 3 && mouseY <= startY + stepY * 3 + 18) { FeatureManager.glowESP = !FeatureManager.glowESP; return true; }
                if (mouseY >= startY + stepY * 4 && mouseY <= startY + stepY * 4 + 18) { FeatureManager.soulSight = !FeatureManager.soulSight; return true; }
                if (mouseY >= startY + stepY * 5 && mouseY <= startY + stepY * 5 + 18) { FeatureManager.customParticles = !FeatureManager.customParticles; return true; }
                if (mouseY >= startY + stepY * 6 && mouseY <= startY + stepY * 6 + 18) { FeatureManager.itemSwapVisual = !FeatureManager.itemSwapVisual; return true; }
                if (mouseY >= startY + stepY * 8 && mouseY <= startY + stepY * 8 + 18) { FeatureManager.toggleColor(); return true; }
            } 
            else if (currentTab == 2) {
                if (mouseY >= startY && mouseY <= startY + 18) { FeatureManager.showWings = !FeatureManager.showWings; return true; }
                if (mouseY >= startY + stepY && mouseY <= startY + stepY + 18) { FeatureManager.showHat = !FeatureManager.showHat; return true; }
                if (mouseY >= startY + stepY * 2 && mouseY <= startY + stepY * 2 + 18) { FeatureManager.showDemonicRays = !FeatureManager.showDemonicRays; return true; }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() { return false; }
}
