package com.auravisual;

public class FeatureManager {
    // Вкладка: Бой
    public static boolean triggerBot = false;

    // Вкладка: Визуалы
    public static boolean targetHUD = false;
    public static boolean armorHUD = false;
    public static boolean potionHUD = false;
    public static boolean glowESP = false;

    // Вкладка: Косметика
    public static boolean showWings = false;
    public static boolean showHat = false;

    // Системные настройки отображения меню
    public static int clientColor = 0xFFFF0055; // Насыщенный розово-красный цвет интерфейса
    public static int targetHudMode = 1;
    private static int colorIndex = 0;

    public static String getColorName() {
        switch (colorIndex) {
            case 0: return "Розовый";
            case 1: return "Аква";
            case 2: return "Зеленый";
            default: return "Розовый";
        }
    }

    public static void toggleColor() {
        colorIndex++;
        if (colorIndex > 2) colorIndex = 0;

        if (colorIndex == 0) clientColor = 0xFFFF0055;
        if (colorIndex == 1) clientColor = 0xFF00FFCC;
        if (colorIndex == 2) clientColor = 0xFF00FF66;
    }
}
