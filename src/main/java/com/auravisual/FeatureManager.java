package com.auravisual;

public class FeatureManager {
    public static int clientColor = 0xFF8800FF; // Стандартный фиолетовый
    public static int targetHudMode = 1;
    
    // Модули
    public static boolean targetHUD = true;
    public static boolean armorHUD = true;
    public static boolean potionHUD = true;
    public static boolean glowESP = false;
    public static boolean customParticles = true;
    public static boolean itemSwapVisual = true;
    
    // НОВАЯ ПЕРЕМЕННАЯ
    public static boolean soulSight = false; 

    // Косметика
    public static boolean showWings = false;
    public static boolean showHat = false;
    public static boolean showDemonicRays = false;

    public static void toggleColor() {
        if (clientColor == 0xFF8800FF) clientColor = 0xFFFF0000;      // Красный
        else if (clientColor == 0xFFFF0000) clientColor = 0xFF00FF00; // Зеленый
        else if (clientColor == 0xFF00FF00) clientColor = 0xFF00FFFF; // Голубой
        else clientColor = 0xFF8800FF;                               // Назад в фиолетовый
    }

    public static String getColorName() {
        if (clientColor == 0xFFFF0000) return "Красный";
        if (clientColor == 0xFF00FF00) return "Зеленый";
        if (clientColor == 0xFF00FFFF) return "Голубой";
        return "Фиолетовый";
    }
}
