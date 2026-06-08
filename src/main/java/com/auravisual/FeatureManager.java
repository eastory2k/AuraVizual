package com.auravisual;

public class FeatureManager {
    // Боевые и инфо-панели
    public static boolean targetHUD = true;
    public static int targetHudMode = 1; // 1: SoupAPI, 2: 3D, 3: Micro
    public static boolean itemSwapVisual = true;
    public static boolean armorHUD = true;
    public static boolean potionHUD = true;

    // Визуал и Мир
    public static boolean glowESP = false;
    public static boolean customParticles = true;

    // Вкладка: КОСМЕТИКА
    public static boolean showWings = false;
    public static boolean showHat = false;
    public static boolean showDemonicRays = false;

    // СИСТЕМА ЦВЕТА (Color Picker)
    // 0: Фиолетовый (Aura), 1: Красный (Flame), 2: Зелёный (Acid), 3: Голубой (Ice)
    public static int currentColorIndex = 0; 
    public static int clientColor = 0xFF9900FF; // Дефолтный фиолетовый

    // Метод для переключения цвета в ClickGUI
    public static void toggleColor() {
        currentColorIndex++;
        if (currentColorIndex > 3) currentColorIndex = 0;

        switch (currentColorIndex) {
            case 0 -> clientColor = 0xFF9900FF; // Фиолетовый неон
            case 1 -> clientColor = 0xFFFF0033; // Огненно-красный
            case 2 -> clientColor = 0xFF00FF66; // Кислотно-зелёный
            case 3 -> clientColor = 0xFF00CCFF; // Ледяной голубой
        }
    }

    // Метод для получения названия текущего цвета в меню
    public static String getColorName() {
        return switch (currentColorIndex) {
            case 0 -> "Фиолетовый";
            case 1 -> "Красный";
            case 2 -> "Зелёный";
            case 3 -> "Голубой";
            default -> "Кастомный";
        };
    }
}
