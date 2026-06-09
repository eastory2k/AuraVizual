package com.auravisual;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class ClickGUI extends Screen {
    private int currentTab = 0; // 0 - Бой, 1 - Визуалы, 2 - Косметика

    public ClickGUI() {
        super(Text.literal("AuraVisual ClickGUI"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Рендер стандартного размытого/затемненного фона (в 1.21.4 требует эти аргументы)
        this.renderBackground(context, mouseX, mouseY, delta);

        // Размеры и центрирование главного окна меню
        int width = 240;
        int height = 190; 
        int x = (this.width - width) / 2;
        int y = (this.height - height) / 2;

        // Отрисовка основного корпуса меню (темный полупрозрачный фон)
        context.fill(x, y, x + width, y + height, 0xF60F0F0F); 
        // Верхняя декоративная полоска фирменного цвета мода
        context.fill(x, y, x + width, y + 2, FeatureManager.clientColor); 

        // Заголовки меню
        context.drawText(this.textRenderer, "AURA VISUAL // MENU", x + 12, y + 10, FeatureManager.clientColor, false);
        context.drawText(this.textRenderer, "v1.1.0 (1.21.4)", x + width - 75, y + 10, 0x50FFFFFF, false);

        // Отрисовка вкладок (Категорий)
        int tabY = y + 26;
        drawTabs(context, x, tabY, mouseX, mouseY);

        // Разделительная линия между вкладками и кнопками
        context.fill(x + 10, y + 42, x + width - 10, y + 43, 0x20FFFFFF);

        // Координаты для отрисовки кнопок функций
        int bx = x + 12;
        int bWidth = width - 24;
        int startY = y + 52;
        int stepY = 22;

        // Отрисовка контента в зависимости от выбранной вкладки
        if (currentTab == 0) {
            // Вкладка: БОЙ
            drawBtn(context, bx, startY, bWidth, "TriggerBot (Только Криты)", FeatureManager.triggerBot, mouseX, mouseY);
        } 
        else if (currentTab == 1) {
            // Вкладка: ВИЗУАЛЫ
            drawBtn(context, bx, startY, bWidth, "TargetHUD (Инфо о цели)", FeatureManager.targetHUD, mouseX, mouseY);
            drawBtn(context, bx, startY + stepY, bWidth, "ArmorHUD (Броня на экране)", FeatureManager.armorHUD, mouseX, mouseY);
            drawBtn(context, bx, startY + stepY * 2, bWidth, "PotionHUD (Эффекты)", FeatureManager.potionHUD, mouseX, mouseY);
            drawBtn(context, bx, startY + stepY * 3, bWidth, "Glow / ESP (Подсветка предметов)", FeatureManager.glowESP, mouseX, mouseY);
            
            // Кнопка смены цвета интерфейса
            drawColorBtn(context, bx, startY + stepY * 5, bWidth, "Цвет Мода: " + FeatureManager.getColorName(), mouseX, mouseY);
        } 
        else if (currentTab == 2) {
            // Вкладка: КОСМЕТИКА
            drawBtn(context, bx, startY, bWidth, "Кастомные Крылья", FeatureManager.showWings, mouseX, mouseY);
            drawBtn(context, bx, startY + stepY, bWidth, "Призрачный Нимб", FeatureManager.showHat, mouseX, mouseY);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    // Логика отрисовки вкладок переключения категорий
    private void drawTabs(DrawContext context, int x, int tabY, int mouseX, int mouseY) {
        String[] names = {"[БОЙ]", "[ВИЗУАЛЫ]", "[КОСМЕТИКА]"};
        int[] offsets = {12, 70, 150};
        int[] widths = {45, 60, 65}; // Хитбоксы ширины для точного клика
        
        for (int i = 0; i < names.length; i++) {
            boolean hovered = mouseX >= x + offsets[i] && mouseX <= x + offsets[i] + widths[i] && mouseY >= tabY && mouseY <= tabY + 12;
            int color = currentTab == i ? FeatureManager.clientColor : (hovered ? 0xFFFFFFFF : 0xBBFFFFFF);
            context.drawText(this.textRenderer, names[i], x + offsets[i], tabY, color, false);
        }
    }

    // Стандартная кнопка включения/выключения функции (ON / OFF)
    private void drawBtn(DrawContext ctx, int bx, int by, int bWidth, String name, boolean active, int mx, int my) {
        boolean hovered = mx >= bx && mx <= bx + bWidth && my >= by && my <= by + 18;
        int bgColor = hovered ? 0x25FFFFFF : 0x12FFFFFF; 
        
        ctx.fill(bx, by, bx + bWidth, by + 18, bgColor);
        ctx.drawText(this.textRenderer, name, bx + 6, by + 5, 0xFFE0E0E0, false);
        
        String status = active ? "ON" : "OFF";
        int statusColor = active ? FeatureManager.clientColor : 0x60FFFFFF;
        
        // Отрисовка статуса с правого края кнопки (текст больше не накладывается)
        ctx.drawText(this.textRenderer, status, bx + bWidth - 30, by + 5, statusColor, false);
    }

    // Специальная кнопка для переключения цвета темы мода
    private void drawColorBtn(DrawContext ctx, int bx, int by, int bWidth, String name, int mx, int my) {
        boolean hovered = mx >= bx && mx <= bx + bWidth && my >= by && my <= by + 18;
        int bgColor = hovered ? 0x35FFFFFF : 0x12FFFFFF;
        
        ctx.fill(bx, by, bx + bWidth, by + 18, bgColor);
        ctx.fill(bx, by, bx + 3, by + 18, FeatureManager.clientColor); // Маленький цветной индикатор слева
        ctx.drawText(this.textRenderer, name, bx + 10, by + 5, 0xFFFFFFFF, false);
        ctx.drawText(this.textRenderer, "[ СМЕНИТЬ ]", bx + bWidth - 65, by + 5, FeatureManager.clientColor, false);
    }

    // Обработка кликов мыши (Сигнатура адаптирована строго под Minecraft 1.21.4)
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false; // Игнорируем всё, кроме ЛКМ (Левой Кнопки Мыши)

        int width = 240;
        int height = 190;
        int x = (this.width - width) / 2;
        int y = (this
