package com.ltkernel.managers;

import java.util.*;
import com.badlogic.gdx.Audio.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

/**
 * Created by tommy_000 on 6/4/2014.
 */
public class JukeBox {
    private static HashMap<String, Sound> sounds;

    static {
        sounds = new HashMap<String, Sound>();
    }

    public static void load(String name, String path) {
        Sound sound = Gdx.audio.newSound(Gdx.files.internal(path));
        sounds.put(name, sound);
    }

    public static void play(String name) {
        sounds.get(name).play();
    }

    public static void stop(String name) {
        sounds.get(name).stop();
    }

    public static void loop(String name) {
        sounds.get(name).loop();
    }
}
