package com.kneelawk.simplebrickbreaker.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.kneelawk.simplebrickbreaker.SimpleBrickBreakerGame;

public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(1280, 720);
        config.setTitle("Simple Brick Breaker");
        config.useOpenGL3(true, 3, 2);
        new Lwjgl3Application(new SimpleBrickBreakerGame(), config);
    }
}
