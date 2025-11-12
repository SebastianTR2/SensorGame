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
    private SpriteBatch batch;
    private Texture img;
    private Sound scream;
    private float tiempo = 0;
    private final Random r = new Random();

    private static final float DURACION = 2.5f; // ⏱ tiempo antes del GameOver

    public PantallaScreamer(LaberintoDelTerror j) {
        this.juego = j;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        // 😱 Selecciona una imagen y sonido aleatorio
        img = Assets.screamers[r.nextInt(Assets.screamers.length)];
        scream = Assets.gritos[r.nextInt(Assets.gritos.length)];

        // 🔇 Pausa música ambiente
        if (Assets.musicaFondo != null && Assets.musicaFondo.isPlaying())
            Assets.musicaFondo.pause();

        // 🔊 Reproduce grito
        scream.play();
    }

    @Override
    public void render(float delta) {
        tiempo += delta;

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(img, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        // ⏳ Después de 2.5 segundos → PantallaGameOver
        if (tiempo > DURACION) {
            // Reanuda música ambiente
            if (Assets.musicaFondo != null && !Assets.musicaFondo.isPlaying())
                Assets.musicaFondo.play();

            // 🔁 Ir a Game Over
            juego.setScreen(new PantallaGameOver(juego, juego.progreso.getNivel()));
            dispose();
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (scream != null) scream.stop();
    }
}
