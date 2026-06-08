package com.auravisual;

import java.util.Random;

public class FeatureManager {
    public static int clientColor = 0xFFB266FF; 
    
    // Состояния модулей
    public static boolean targetHUD = false;
    public static int targetHudMode = 1;
    public static boolean armorHUD = false;
    public static boolean potionHUD = false;
    public static boolean glowESP = false;
    public static boolean customParticles = false;
    public static boolean itemSwapVisual = false;
    public static boolean soulSight = false; 
    
    // НАШ ОБНОВЛЕННЫЙ ТРИГГЕРБOТ
    public static boolean triggerBot = false; 
    public static int triggerDelayTicks = 0; // Текущая задержка в тиках
    public static final Random random = new Random(); // Генератор случайных чисел

    // Косметика
    public static boolean showWings = false;
    public static boolean showHat = false;
    public static boolean showDemonicRays = false;

    public static void toggleColor() {
        if (clientColor == 0xFFB266FF) clientColor = 0xFFFF3333; 
        else if (clientColor == 0xFFFF3333) clientColor = 0xFF33FF33; 
        else if (clientColor == 0xFF33FF33) clientColor = 0xFF33CCFF; 
        else clientColor = 0xFFB266FF; 
    }

    public static String getColorName() {
        if (clientColor == 0xFFB266FF) return "Фиолетовый";
        if (clientColor == 0xFFFF3333) return "Красный";
        if (clientColor == 0xFF33FF33) return "Зеленый";
        if (clientColor == 0xFF33CCFF) return "Голубой";
        return "Неизвестно";
    }
}
