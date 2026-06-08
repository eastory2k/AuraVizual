package com.auravisual;

public class FeatureManager {
    public static int clientColor = 0xFFFF0000; // Красный цвет по умолчанию
    
    // Переключатели функций (все независимы)
    public static boolean triggerBot = false;
    public static boolean targetHUD = false;
    public static boolean armorHUD = false;
    public static boolean potionHUD = false;
    public static boolean glowESP = false;
    public static boolean soulSight = false;
    public static boolean customParticles = false;
    public static boolean itemSwapVisual = false;
    public static boolean showWings = false;
    public static boolean showHat = false;
    public static boolean showDemonicRays = false;
    
    public static String getColorName() { return "Красный"; }
    public static void toggleColor() { }
}
