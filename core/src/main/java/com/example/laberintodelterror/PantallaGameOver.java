package com.example.laberintodelterror;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

public class PantallaGameOver implements Screen {
    private final LaberintoDelTerror juego;
    private final int nivelActual;
    private SpriteBatch batch;
    private BitmapFont fontTitulo, fontOpcion;
    private float parpadeo = 0;

    public PantallaGameOver(LaberintoDelTerror juego, int nivelActual) {
        this.juego = juego;
        this.nivelActual = nivelActual;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        fontTitulo = new BitmapFont();
        fontOpcion = new BitmapFont();
        fontTitulo.getData().setScale(3);
        fontOpcion.getData().setScale(2);
    }

    @Override
    public void render(float delta) {
        parpadeo += delta;

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float w = Gdx.graphics.getWidth(), h = Gdx.graphics.getHeight();

        batch.begin();
        fontTitulo.setColor(Color.RED);
        fontTitulo.draw(batch, "GAME OVER", w / 2, h * 0.75f, 0, Align.center, false);

        fontOpcion.setColor(Color.WHITE);
        fontOpcion.draw(batch, "1️⃣ Reintentar nivel", w / 2, h * 0.45f, 0, Align.center, false);
        fontOpcion.draw(batch, "2️⃣ Volver a selección", w / 2, h * 0.35f, 0, Align.center, false);

        // Texto parpadeante
        if ((int)(parpadeo * 2) % 2 == 0)
            fontOpcion.draw(batch, "Toca para elegir", w / 2, h * 0.2f, 0, Align.center, false);

        batch.end();

        // 💡 Controles táctiles
        if (Gdx.input.justTouched()) {
            float y = Gdx.graphics.getHeight() - Gdx.input.getY();
            if (y > h * 0.4f) { // zona superior = reintentar
                juego.setScreen(new PantallaLaberinto(juego));
            } else { // zona inferior = selección
                juego.setScreen(new PantallaSeleccionNivel(juego));
            }
            dispose();
        }
    }

    @Override public void dispose() { batch.dispose(); fontTitulo.dispose(); fontOpcion.dispose(); }
    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
