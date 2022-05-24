package com.kneelawk.simplebrickbreaker.util;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

@FunctionalInterface
public interface FChangeListener extends EventListener {
    @Override
    default boolean handle(Event event) {
        if (!(event instanceof ChangeListener.ChangeEvent)) return false;
        changed((ChangeListener.ChangeEvent) event, event.getTarget());
        return false;
    }

    void changed(ChangeListener.ChangeEvent event, Actor actor);
}
