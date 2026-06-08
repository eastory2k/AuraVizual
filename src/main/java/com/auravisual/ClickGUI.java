package com.auravisual;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class ClickGUI extends Screen {
    // 0: Основные модули, 1: Косметика
    private int currentTab = 0; 

    public ClickGUI() {
        super(Text.literal("AuraVisual ClickGUI"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        // Размеры главного окна конфигуратора
        int width = 240;
        int height = 250;
        int x = (this.width - width) / 2;
        int y = (this.height - height) / 2;

        // Главный фон панели
        context.fill(x, y, x + width, y + height, 0xED121212); 
        context.fill(x, y, x + width, y + 2, FeatureManager.clientColor); 

        // Заголовок интерфейса
        context.drawText(this.textRenderer, "AURA VISUAL // MENU", x + 12, y + 10, FeatureManager.clientColor, false);
        context.drawText(this.textRenderer, "v1.0.0", x + width - 45, y + 10, 0x50FFFFFF, false);

        // --- ТАБЫ / ВКЛАДКИ ПЕРЕКЛЮЧЕНИЯ ---
        int tabY = y + 24;
        boolean hoverTab0 = mouseX >= x + 12 && mouseX <= x + 110 && mouseY >= tabY && mouseY <= tabY + 14;
        boolean hoverTab1 = mouseX >= x + 120 && mouseX <= x + 218 && mouseY >= tabY && mouseY <= tabY + 14;

        // Отрисовка вкладки "Главное"
        int t0Color = currentTab == 0 ? FeatureManager.clientColor : (hoverTab0 ? 0x80FFFFFF : 0x40FFFFFF);
        context.drawText(this.textRenderer, "[ ГЛАВНОЕ ]", x + 12, tabY + 3, t0Color, false);

        // Отрисовка вкладки "Косметика"
        int t1Color = currentTab == 1 ? FeatureManager.clientColor : (hoverTab1 ? 0x80FFFFFF : 0x40FFFFFF);
        context.drawText(this.textRenderer, "[ КОСМЕТИКА ]", x + 110, tabY + 3, t1Color, false);

        // Разделительная линия под табами
        context.fill(x + 10, y + 42, x + width - 10, y + 43, 0x15FFFFFF);

        // Параметры позиционирования кнопок внутри окна
        int bx = x + 12;
        int bWidth = width - 24;
        int startY = y + 50;
        int stepY = 22;

        // --- ОТРИСОВКА СОДЕРЖИМОГО ВКЛАДКИ 0: ГЛАВНОЕ ---
        if (currentTab == 0) {
            // TargetHUD
            String thModeName = "Классика";
            if (FeatureManager.targetHudMode == 2) thModeName = "3D Модель";
            if (FeatureManager.targetHudMode == 3) thModeName = "Микро";
            drawBtn(context, bx, startY, bWidth, "TargetHUD [" + thModeName + "]", FeatureManager.targetHUD, mouseX, mouseY);

            // Инфо-панели
            drawBtn(context, bx, startY + stepY, bWidth, "ArmorHUD (Броня и Сферы)", FeatureManager.armorHUD, mouseX, mouseY);
            drawBtn(context, bx, startY + stepY * 2, bWidth, "PotionHUD (Эффекты)", FeatureManager.potionHUD, mouseX, mouseY);
            
            // Визуальные эффекты и мир
            drawBtn(context, bx, startY + stepY * 3, bWidth, "Glow / ESP (Подсветка Лута)", FeatureManager.glowESP, mouseX, mouseY);
            drawBtn(context, bx, startY + stepY * 4, bWidth, "Custom Particles (Криты)", FeatureManager.customParticles, mouseX, mouseY);
            
            // Базовые переключатели
            drawBtn(context, bx, startY + stepY * 5, bWidth, "ItemSwap Visual", FeatureManager.itemSwapVisual, mouseX, mouseY);

            // КНОПКА ГЛОБАЛЬНОЙ СМЕНЫ ЦВЕТА КЛИЕНТА (COLOR PICKER)
            drawColorBtn(context, bx, startY + stepY * 7, bWidth, "Цвет Мода: " + FeatureManager.getColorName(), mouseX, mouseY);
        }
        
        // --- ОТРИСОВКА СОДЕРЖИМОГО ВКЛАДКИ 1: КОСМЕТИКА ---
        else if (currentTab == 1) {
            // Кнопка независимого включения динамических крыльев от скорости
            drawBtn(context, bx, startY, bWidth, "Кастомные Крылья (Скоростные)", FeatureManager.showWings, mouseX, mouseY);

            // Кнопка независимого включения нимба/шапки над головой
            drawBtn(context, bx, startY + stepY, bWidth, "Призрачный Нимб", FeatureManager.showHat, mouseX, mouseY);

            // Дополнительный визуальный эффект: огненный взгляд
            drawBtn(context, bx, startY + stepY * 2, bWidth, "Демонический Взгляд", FeatureManager.showDemonicRays, mouseX, mouseY);
            
            // Информационный блок-подсказка в стиле PulseVisual
            int infoY = startY + stepY * 5;
            context.fill(bx, infoY, bx + bWidth, infoY + 35, 0x08FFFFFF);
            context.drawText(this.textRenderer, "Скорость махов крыльев автоматически", bx + 6, infoY + 6, 0x70FFFFFF, false);
            context.drawText(this.textRenderer, "синхронизируется со скоростью бега.", bx + 6, infoY + 18, 0x70FFFFFF, false);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    // Метод отрисовки стандартных кнопок-переключателей
    private void drawBtn(DrawContext ctx, int bx, int by, int bWidth, String name, boolean active, int mx, int my) {
        boolean hovered = mx >= bx && mx <= bx + bWidth && my >= by && my <= by + 18;
        int bgColor = hovered ? 0x25FFFFFF : 0x10FFFFFF;
        
        ctx.fill(bx, by, bx + bWidth, by + 18, bgColor);
        ctx.drawText(this.textRenderer, name, bx + 6, by + 5, 0xFFE0E0E0, false);
        
        String status = active ? "• ON" : "• OFF";
        int statusColor = active ? FeatureManager.clientColor : 0x40FFFFFF;
        ctx.drawText(this.textRenderer, status, bx + bWidth - 40, by + 5, statusColor, false);
    }

    // Отрисовка специальной кнопки смены глобального цвета
    private void drawColorBtn(DrawContext ctx, int bx, int by, int bWidth, String name, int mx, int my) {
        boolean hovered = mx >= bx && mx <= bx + bWidth && my >= by && my <= by + 18;
        int bgColor = hovered ? 0x35FFFFFF : 0x18FFFFFF;
        
        ctx.fill(bx, by, bx + bWidth, by + 18, bgColor);
        ctx.fill(bx, by, bx + 3, by + 18, FeatureManager.clientColor); // Маркер текущего цвета слева
        ctx.drawText(this.textRenderer, name, bx + 10, by + 5, 0xFFFFFFFF, false);
        ctx.drawText(this.textRenderer, "[ СМЕНИТЬ ]", bx + bWidth - 65, by + 5, FeatureManager.clientColor, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int width = 240;
        int x = (this.width - width) / 2;
        int y = (this.height - 250) / 2;
        
        int bx = x + 12;
        int bWidth = width - 24;
        int startY = y + 50;
        int stepY = 22;
        int tabY = y + 24;

        // Переключение вкладок по клику мышкой
        if (mouseY >= tabY && mouseY <= tabY + 14) {
            if (mouseX >= x + 12 && mouseX <= x + 100) {
                currentTab = 0;
                return true;
            }
            if (mouseX >= x + 110 && mouseX <= x + 210) {
                currentTab = 1;
                return true;
            }
        }

        // Клик по элементам внутри выбранного таба
        if (mouseX >= bx && mouseX <= bx + bWidth) {
            // КЛИКИ ДЛЯ ВКЛАДКИ 0: ГЛАВНОЕ
            if (currentTab == 0) {
                // TargetHUD
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
                // GlowESP
                if (mouseY >= startY + stepY * 3 && mouseY <= startY + stepY * 3 + 18) {
                    FeatureManager.glowESP = !FeatureManager.glowESP;
                    return true;
                }
                // CustomParticles
                if (mouseY >= startY + stepY * 4 && mouseY <= startY + stepY * 4 + 18) {
                    FeatureManager.customParticles = !FeatureManager.customParticles;
                    return true;
                }
                // ItemSwap
                if (mouseY >= startY + stepY * 5 && mouseY <= startY + stepY * 5 + 18) {
                    FeatureManager.itemSwapVisual = !FeatureManager.itemSwapVisual;
                    return true;
                }
                // ОБРАБОТЧИК КЛИКА КНОПКИ COLOR PICKER (СМЕНА ЦВЕТА)
                if (mouseY >= startY + stepY * 7 && mouseY <= startY + stepY * 7 + 18) {
                    FeatureManager.toggleColor();
                    return true;
                }
            } 
            
            // КЛИКИ ДЛЯ ВКЛАДКИ 1: КОСМЕТИКА
            else if (currentTab == 1) {
                // Крылья
                if (mouseY >= startY && mouseY <= startY + 18) {
                    FeatureManager.showWings = !FeatureManager.showWings;
                    return true;
                }
                // Шапка / Нимб
                if (mouseY >= startY + stepY && mouseY <= startY + stepY + 18) {
                    FeatureManager.showHat = !FeatureManager.showHat;
                    return true;
                }
                // Демонический взгляд
                if (mouseY >= startY + stepY * 2 && mouseY <= startY + stepY * 2 + 18) {
                    FeatureManager.showDemonicRays = !FeatureManager.showDemonicRays;
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
