package com.auravisual;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class ClickGUI extends Screen {
    private int currentTab = 0; // 0 - Бой, 1 - Визуалы

    public ClickGUI() {
        super(Text.literal("AuraVisual ClickGUI"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        // Фиксированные размеры, чтобы ничего не размазывалось
        int width = 230;
        int height = 160; 
        int x = (this.width - width) / 2;
        int y = (this.height - height) / 2;

        // Основное окно
        context.fill(x, y, x + width, y + height, 0xF60F0F0F); 
        context.fill(x, y, x + width, y + 2, FeatureManager.clientColor); 

        // Заголовки (используем стандартный textRenderer)
        context.drawText(this.textRenderer, "AURA VISUAL", x + 12, y + 10, FeatureManager.clientColor, false);
        context.drawText(this.textRenderer, "v1.1.4", x + width - 45, y + 10, 0x50FFFFFF, false);

        // Вкладки
        int tabY = y + 26;
        drawTabs(context, x, tabY, mouseX, mouseY);

        // Разделитель
        context.fill(x + 10, y + 38, x + width - 10, y + 39, 0x20FFFFFF);

        int bx = x + 12;
        int bWidth = width - 24;
        int startY = y + 48;
        int stepY = 22;

        // Контент вкладок
        if (currentTab == 0) {
            drawBtn(context, bx, startY, bWidth, "TriggerBot (Криты)", FeatureManager.triggerBot, mouseX, mouseY);
        } 
        else if (currentTab == 1) {
            drawBtn(context, bx, startY, bWidth, "TargetHUD", FeatureManager.targetHUD, mouseX, mouseY);
            drawBtn(context, bx, startY + stepY, bWidth, "ArmorHUD", FeatureManager.armorHUD, mouseX, mouseY);
            drawBtn(context, bx, startY + stepY * 2, bWidth, "ItemSwap Visual", FeatureManager.itemSwapVisual, mouseX, mouseY);
            drawColorBtn(context, bx, startY + stepY * 4, bWidth, "Цвет: " + FeatureManager.getColorName(), mouseX, mouseY);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawTabs(DrawContext context, int x, int tabY, int mouseX, int mouseY) {
        String[] names = {"[БОЙ]", "[ВИЗУАЛЫ]"};
        int[] offsets = {12, 65};
        int[] widths = {40, 55}; 
        
        for (int i = 0; i < names.length; i++) {
            boolean hovered = mouseX >= x + offsets[i] && mouseX <= x + offsets[i] + widths[i] && mouseY >= tabY && mouseY <= tabY + 12;
            int color = currentTab == i ? FeatureManager.clientColor : (hovered ? 0xFFFFFFFF : 0xBBFFFFFF);
            context.drawText(this.textRenderer, names[i], x + offsets[i], tabY, color, false);
        }
    }

    private void drawBtn(DrawContext ctx, int bx, int by, int bWidth, String name, boolean active, int mx, int my) {
        boolean hovered = mx >= bx && mx <= bx + bWidth && my >= by && my <= by + 18;
        int bgColor = hovered ? 0x25FFFFFF : 0x12FFFFFF; 
        
        ctx.fill(bx, by, bx + bWidth, by + 18, bgColor);
        ctx.drawText(this.textRenderer, name, bx + 6, by + 5, 0xFFE0E0E0, false);
        
        String status = active ? "ON" : "OFF";
        int statusColor = active ? FeatureManager.clientColor : 0x60FFFFFF;
        ctx.drawText(this.textRenderer, status, bx + bWidth - 28, by + 5, statusColor, false);
    }

    private void drawColorBtn(DrawContext ctx, int bx, int by, int bWidth, String name, int mx, int my) {
        boolean hovered = mx >= bx && mx <= bx + bWidth && my >= by && my <= by + 18;
        int bgColor = hovered ? 0x35FFFFFF : 0x12FFFFFF;
        
        ctx.fill(bx, by, bx + bWidth, by + 18, bgColor);
        ctx.fill(bx, by, bx + 3, by + 18, FeatureManager.clientColor);
        ctx.drawText(this.textRenderer, name, bx + 10, by + 5, 0xFFFFFFFF, false);
        ctx.drawText(this.textRenderer, "[ СМЕНИТЬ ]", bx + bWidth - 65, by + 5, FeatureManager.clientColor, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false; 

        int width = 230;
        int height = 160;
        int x = (this.width - width) / 2;
        int y = (this.height - height) / 2;
        
        int bx = x + 12;
        int bWidth = width - 24;
        int startY = y + 48;
        int stepY = 22;

        int tabY = y + 26;
        if (mouseY >= tabY && mouseY <= tabY + 14) {
            if (mouseX >= x + 12 && mouseX <= x + 52) { currentTab = 0; return true; }
            if (mouseX >= x + 65 && mouseX <= x + 120) { currentTab = 1; return true; }
        }

        if (mouseX >= bx && mouseX <= bx + bWidth) {
            if (currentTab == 0) {
                if (mouseY >= startY && mouseY <= startY + 18) { 
                    FeatureManager.triggerBot = !FeatureManager.triggerBot; 
                    return true; 
                }
            }
            else if (currentTab == 1) {
                if (mouseY >= startY && mouseY <= startY + 18) { FeatureManager.targetHUD = !FeatureManager.targetHUD; return true; }
                if (mouseY >= startY + stepY && mouseY <= startY + stepY + 18) { FeatureManager.armorHUD = !FeatureManager.armorHUD; return true; }
                if (mouseY >= startY + stepY * 2 && mouseY <= startY + stepY * 2 + 18) { FeatureManager.itemSwapVisual = !FeatureManager.itemSwapVisual; return true; }
                if (mouseY >= startY + stepY * 4 && mouseY <= startY + stepY * 4 + 18) { FeatureManager.toggleColor(); return true; }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() { return false; }
}
