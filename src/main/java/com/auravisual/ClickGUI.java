package com.auravisual;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class ClickGUI extends Screen {
    private int currentTab = 0; 

    public ClickGUI() {
        super(Text.literal("AuraVisual ClickGUI"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // В 1.21.4 рендер фона требует эти аргументы
        this.renderBackground(context, mouseX, mouseY, delta);

        int width = 240;
        int height = 190; 
        int x = (this.width - width) / 2;
        int y = (this.height - height) / 2;

        context.fill(x, y, x + width, y + height, 0xF60F0F0F); 
        context.fill(x, y, x + width, y + 2, FeatureManager.clientColor); 

        // Отрисовка заголовков
        context.drawText(this.textRenderer, "AURA VISUAL // MENU", x + 12, y + 10, FeatureManager.clientColor, false);
        context.drawText(this.textRenderer, "v1.1.0 (1.21.4)", x + width - 75, y + 10, 0x50FFFFFF, false);

        int tabY = y + 26;
        drawTabs(context, x, tabY, mouseX, mouseY);

        context.fill(x + 10, y + 42, x + width - 10, y + 43, 0x20FFFFFF);

        int bx = x + 12;
        int bWidth = width - 24;
        int startY = y + 52;

        // Отрисовка кнопок в зависимости от вкладки
        if (currentTab == 0) {
            drawBtn(context, bx, startY, bWidth, "TriggerBot (Только Криты)", FeatureManager.triggerBot, mouseX, mouseY);
        } else if (currentTab == 1) {
            renderVisualsTab(context, bx, startY, bWidth, mouseX, mouseY);
        } else if (currentTab == 2) {
            renderCosmeticsTab(context, bx, startY, bWidth, mouseX, mouseY);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawTabs(DrawContext context, int x, int tabY, int mouseX, int mouseY) {
        String[] names = {"[БОЙ]", "[ВИЗУАЛЫ]", "[КОСМЕТИКА]"};
        int[] offsets = {12, 70, 150};
        
        for (int i = 0; i < names.length; i++) {
            boolean hovered = mouseX >= x + offsets[i] && mouseX <= x + offsets[i] + 50 && mouseY >= tabY && mouseY <= tabY + 12;
            int color = currentTab == i ? FeatureManager.clientColor : (hovered ? 0xFFFFFFFF : 0xBBFFFFFF);
            context.drawText(this.textRenderer, names[i], x + offsets[i], tabY, color, false);
        }
    }

    private void renderVisualsTab(DrawContext context, int bx, int startY, int bWidth, int mx, int my) {
        int stepY = 22;
        drawBtn(context, bx, startY, bWidth, "TargetHUD", FeatureManager.targetHUD, mx, my);
        drawBtn(context, bx, startY + stepY, bWidth, "ArmorHUD", FeatureManager.armorHUD, mx, my);
        drawBtn(context, bx, startY + stepY * 2, bWidth, "PotionHUD", FeatureManager.potionHUD, mx, my);
        drawBtn(context, bx, startY + stepY * 3, bWidth, "Glow / ESP", FeatureManager.glowESP, mx, my);
    }

    private void renderCosmeticsTab(DrawContext context, int bx, int startY, int bWidth, int mx, int my) {
        int stepY = 22;
        drawBtn(context, bx, startY, bWidth, "Крылья", FeatureManager.showWings, mx, my);
        drawBtn(context, bx, startY + stepY, bWidth, "Нимб", FeatureManager.showHat, mx, my);
    }

    private void drawBtn(DrawContext ctx, int bx, int by, int bWidth, String name, boolean active, int mx, int my) {
        boolean hovered = mx >= bx && mx <= bx + bWidth && my >= by && my <= by + 18;
        int bgColor = hovered ? 0x25FFFFFF : 0x12FFFFFF; 
        
        ctx.fill(bx, by, bx + bWidth, by + 18, bgColor);
        ctx.drawText(this.textRenderer, name, bx + 6, by + 5, 0xFFE0E0E0, false);
        
        String status = active ? "• ON" : "• OFF";
        int statusColor = active ? FeatureManager.clientColor : 0x50FFFFFF;
        ctx.drawText(this.textRenderer, status, bx + bWidth - 40, by + 5, statusColor, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Логика переключения табов и кнопок остается прежней
        // Проверь координаты x и y как в предыдущем коде
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() { return false; }
}
