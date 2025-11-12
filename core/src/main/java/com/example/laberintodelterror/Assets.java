package com.example.laberintodelterror;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

public class Assets {
    public static Texture bolaTex, fondoMenu, botonJugar;
    public static Texture[] screamers;
    public static Sound[] gritos;
    public static Music musicaFondo;

    //  Texturas de llaves y puertas
    public static Texture llaveTex, llaveMoradaTex;
    public static Texture puertaCerradaTex, puertaAbiertaTex;

    public static void cargar() {
        // esenciales
        bolaTex = safeTex("bola.png", () -> solidCircle(64, 64));
        fondoMenu = safeTex("fondo_menu.png", () -> solid(1280, 720, 20, 20, 20, 255));
        botonJugar = safeTex("boton_jugar.png", () -> solid(300, 100, 180, 0, 0, 255));

        // screamers (imagenes)
        screamers = new Texture[5];
        for (int i = 0; i < screamers.length; i++)
            screamers[i] = safeTex("screamer/screamer" + (i + 1) + ".png",
                () -> solid(512, 512, 200, 40, 40, 255));

        // sonidos (gritos)
        gritos = new Sound[3];
        for (int i = 0; i < gritos.length; i++)
            gritos[i] = safeSound("audios/screamer" + (i + 1) + ".mp3");

        // música de fondo
        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("audios/hambiente.mp3"));
        musicaFondo.setLooping(true);
        musicaFondo.setVolume(0.3f);

        //  Llaves y puertas
        llaveTex = safeTex("llave_amarilla.png", () -> solid(36, 16, 255, 215, 0, 255)); // amarilla
        llaveMoradaTex = safeTex("llave_morada.png", () -> solid(36, 16, 180, 0, 255, 255)); // morada (fallback)
        puertaCerradaTex = safeTex("puerta_cerrada.png", () -> solid(48, 48, 120, 20, 20, 255));
        puertaAbiertaTex = safeTex("puerta_abierta.png", () -> alphaRect(48, 48));
    }

    //  Reproducir un screamer aleatorio
    public static void playScreamerAleatorio() {
        if (gritos == null || gritos.length == 0) return;
        int idx = MathUtils.random(gritos.length - 1);
        try {
            gritos[idx].play(1.0f);
            Gdx.app.log("ASSETS", "Screamer reproducido: " + idx);
        } catch (Exception e) {
            Gdx.app.error("ASSETS", "Error al reproducir screamer " + idx, e);
        }
    }

    // 🧹 Liberar memoria
    public static void dispose() {
        disposeSafe(bolaTex);
        disposeSafe(fondoMenu);
        disposeSafe(botonJugar);
        disposeArr(screamers);
        disposeArr(gritos);
        if (musicaFondo != null) musicaFondo.dispose();
        disposeSafe(llaveTex);
        disposeSafe(llaveMoradaTex);
        disposeSafe(puertaCerradaTex);
        disposeSafe(puertaAbiertaTex);
    }

    // --- helpers ---
    private static Texture safeTex(String path, Fallback f) {
        return Gdx.files.internal(path).exists()
            ? new Texture(path)
            : f.make();
    }

    private static Sound safeSound(String path) {
        if (Gdx.files.internal(path).exists())
            return Gdx.audio.newSound(Gdx.files.internal(path));
        else
            return null;
    }

    private static void disposeSafe(Texture t) {
        if (t != null) t.dispose();
    }

    private static void disposeArr(Texture[] a) {
        if (a != null) for (Texture t : a) disposeSafe(t);
    }

    private static void disposeArr(Sound[] a) {
        if (a != null) for (Sound s : a) if (s != null) s.dispose();
    }

    private interface Fallback { Texture make(); }

    private static Texture solid(int w, int h, int r, int g, int b, int a) {
        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        p.setColor(r / 255f, g / 255f, b / 255f, a / 255f);
        p.fill();
        Texture t = new Texture(p);
        p.dispose();
        return t;
    }

    private static Texture alphaRect(int w, int h) {
        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        p.setColor(0, 0, 0, 0);
        p.fill();
        Texture t = new Texture(p);
        p.dispose();
        return t;
    }

    private static Texture solidCircle(int w, int h) {
        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        p.setColor(0.8f, 0.8f, 1f, 1f);
        int cx = w / 2, cy = h / 2, r = Math.min(w, h) / 2;
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++) {
                int dx = x - cx, dy = y - cy;
                if (dx * dx + dy * dy <= r * r) p.drawPixel(x, y);
            }
        Texture t = new Texture(p);
        p.dispose();
        return t;
    }
}
