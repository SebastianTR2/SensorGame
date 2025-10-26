package com.example.laberintodelterror;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;

public class Assets {
    public static Texture[] screamers;
    public static Sound[] gritos;
    public static Music musicaFondo;

    public static void cargar() {
        // 🔹 Cargar imágenes de screamers
        screamers = new Texture[5];
        for (int i = 0; i < 5; i++) {
            screamers[i] = new Texture(Gdx.files.internal("screamer" + (i + 1) + ".png"));
        }

        // 🔹 Cargar gritos
        gritos = new Sound[3];
        for (int i = 0; i < 3; i++) {
            gritos[i] = Gdx.audio.newSound(Gdx.files.internal("screamer" + (i + 1) + ".mp3"));
        }

        // 🔹 Cargar música de fondo
        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("hambiente.mp3"));
        musicaFondo.setLooping(true);
        musicaFondo.setVolume(0.3f); // volumen bajo para no tapar gritos
    }

    public static void dispose() {
        for (Texture t : screamers)
            if (t != null) t.dispose();

        for (Sound s : gritos)
            if (s != null) s.dispose();

        if (musicaFondo != null)
            musicaFondo.dispose();
    }
}
