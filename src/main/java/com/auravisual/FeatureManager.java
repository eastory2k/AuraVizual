package com.auravisual;

public class FeatureManager {
    public static boolean triggerBot = false;
    public static boolean targetHUD = false;
    public static boolean armorHUD = false;
    public static boolean itemSwapVisual = false;

    public static int clientColor = 0xFF00FFCC; // По умолчанию красивый цвет Аква
    private static int colorIndex = 1;

    public static String getColorName() {
        if (colorIndex == 0) return "Розовый";
        if (colorIndex == 1) return "Аква";
        return "Зеленый";
    }

    public static void toggleColor() {
        colorIndex++;
        if (colorIndex > 2) colorIndex = 0;

        if (colorIndex == 0) clientColor = 0xFFFF0055;
        if (colorIndex == 1) clientColor = 0xFF00FFCC;
        if (colorIndex == 2) clientColor = 0xFF00FF66;
    }
}
