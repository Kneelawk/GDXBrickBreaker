package com.kneelawk.simplebrickbreaker.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.kneelawk.simplebrickbreaker.SimpleBrickBreakerGameMain;
import com.kneelawk.simplebrickbreaker.game.RunningGameWorld;

public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(1280, 720);
        config.setForegroundFPS(60);
        config.setIdleFPS(15);
        config.setTitle("Simple Brick Breaker");
        new Lwjgl3Application(new SimpleBrickBreakerGameMain(), config);
    }
}
