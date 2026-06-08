package com.auravisual;

public class FeatureManager {
    public static int clientColor = 0xFFB266FF; // Фиолетовый по умолчанию
    
    // Состояния модулей
    public static boolean targetHUD = false;
    public static int targetHudMode = 1;
    public static boolean armorHUD = false;
    public static boolean potionHUD = false;
    public static boolean glowESP = false;
    public static boolean customParticles = false;
    public static boolean itemSwapVisual = false;
    
    // НАША НОВАЯ ПЕРЕМЕННАЯ
    public static boolean soulSight = false; 

    // Косметика
    public static boolean showWings = false;
    public static boolean showHat = false;
    public static boolean showDemonicRays = false;

    // Метод для смены цвета (Color Picker)
    public static void toggleColor() {
        if (clientColor == 0xFFB266FF) clientColor = 0xFFFF3333; // В красный
        else if (clientColor == 0xFFFF3333) clientColor = 0xFF33FF33; // В зеленый
        else if (clientColor == 0xFF33FF33) clientColor = 0xFF33CCFF; // В голубой
        else clientColor = 0xFFB266FF; // Назад в фиолетовый
    }

    public static String getColorName() {
        if (clientColor == 0xFFB266FF) return "Фиолетовый";
        if (clientColor == 0xFFFF3333) return "Красный";
        if (clientColor == 0xFF33FF33) return "Зеленый";
        if (clientColor == 0xFF33CCFF) return "Голубой";
        return "Неизвестно";
    }
}
