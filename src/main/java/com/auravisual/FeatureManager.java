package com.auravisual;

public class FeatureManager {
    public static int clientColor = 0xFFFF0000; // Красный цвет интерфейса по умолчанию

    // Переключатели функций (все независимы друг от друга)
    public static boolean triggerBot = false;
    public static boolean targetHUD = false;
    public static boolean armorHUD = false;
    public static boolean potionHUD = false;
    public static boolean glowESP = false;
    public static boolean soulSight = false;
    public static boolean customParticles = false;
    public static boolean itemSwapVisual = false;
    
    // Косметика
    public static boolean showWings = false;
    public static boolean showHat = false;
    public static boolean showDemonicRays = false;

    // Вспомогательные настройки режима TargetHUD
    public static int targetHudMode = 1; // 1 - Классика, 2 - 3D, 3 - Микро

    public static String getColorName() {
        if (clientColor == 0xFFFF0000) return "Красный";
        if (clientColor == 0xFF00FF00) return "Зеленый";
        if (clientColor == 0xFF0000FF) return "Синий";
        return "Кастомный";
    }

    public static void toggleColor() {
        if (clientColor == 0xFFFF0000) {
            clientColor = 0xFF00FF00; // переключаем на зеленый
        } else if (clientColor == 0xFF00FF00) {
            clientColor = 0xFF0000FF; // переключаем на синий
        } else {
            clientColor = 0xFFFF0000; // возвращаем красный
        }
    }
}
