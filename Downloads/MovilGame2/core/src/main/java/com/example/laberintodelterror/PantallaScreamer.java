package com.example.laberintodelterror;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.Random;

public class PantallaScreamer implements Screen {

    private final LaberintoDelTerror juego;
    private final int nivel;
    private SpriteBatch batch;
    private Texture screamer;
    private Sound grito;
    private float tiempo;
    private Random random = new Random();

    public PantallaScreamer(LaberintoDelTerror juego, int nivel) {
        this.juego = juego;
        this.nivel = nivel;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        screamer = Assets.screamers[random.nextInt(Assets.screamers.length)];
        grito = Assets.gritos[random.nextInt(Assets.gritos.length)];

        // 🔇 Pausar música de fondo
        if (Assets.musicaFondo != null && Assets.musicaFondo.isPlaying()) {
            Assets.musicaFondo.pause();
        }

        // 😱 Reproducir grito
        grito.play();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(screamer, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        tiempo += delta;
        if (tiempo > 3f) {
            // 🎵 Reanudar música cuando vuelva al menú
            if (Assets.musicaFondo != null && !Assets.musicaFondo.isPlaying()) {
                Assets.musicaFondo.play();
            }
            juego.setScreen(new PantallaMenu(juego));
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { batch.dispose(); }
}
