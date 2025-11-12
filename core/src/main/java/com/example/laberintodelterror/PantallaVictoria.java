package com.example.laberintodelterror;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

public class PantallaVictoria implements Screen {
    private final LaberintoDelTerror juego;
    private final int nivel;
    private final float tiempoTotal;
    private SpriteBatch batch;
    private BitmapFont fontTitulo, fontTexto, fontParpadeo;
    private float tiempoParpadeo = 0;

    public PantallaVictoria(LaberintoDelTerror juego, int nivel, float tiempoTotal) {
        this.juego = juego;
        this.nivel = nivel;
        this.tiempoTotal = tiempoTotal;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        fontTitulo = new BitmapFont();
        fontTexto = new BitmapFont();
        fontParpadeo = new BitmapFont();

        fontTitulo.getData().setScale(3.2f);
        fontTexto.getData().setScale(2.2f);
        fontParpadeo.getData().setScale(1.8f);

        fontTitulo.setColor(Color.GOLD);
        fontTexto.setColor(Color.WHITE);
        fontParpadeo.setColor(Color.LIGHT_GRAY);

        //  Guarda el progreso (desbloquea el siguiente nivel si aplica)
        juego.progreso.desbloquearSiguienteNivel(nivel + 1);
    }

    @Override
    public void render(float delta) {
        tiempoParpadeo += delta;

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float w = Gdx.graphics.getWidth(), h = Gdx.graphics.getHeight();
        batch.begin();

        //  Mensajes principales
        fontTitulo.draw(batch, "¡Felicidades!", w / 2, h * 0.75f, 0, Align.center, false);
        fontTexto.draw(batch, "Completaste el Nivel " + nivel, w / 2, h * 0.55f, 0, Align.center, false);
        fontTexto.draw(batch, "Tiempo total: " + String.format("%.1f segundos", tiempoTotal), w / 2, h * 0.45f, 0, Align.center, false);

        //  Texto parpadeante
        float alpha = (float) (0.5f + 0.5f * Math.sin(tiempoParpadeo * 3));
        fontParpadeo.setColor(1, 1, 1, alpha);
        fontParpadeo.draw(batch, "Toca para continuar", w / 2, h * 0.25f, 0, Align.center, false);

        batch.end();

        //  Avanza al tocar
        if (Gdx.input.justTouched()) {
            if (nivel == 5) {
                juego.setScreen(new PantallaFinal(juego));
            } else {
                juego.setScreen(new PantallaSeleccionNivel(juego));
            }
            dispose();
        }
    }

    @Override public void dispose() {
        batch.dispose();
        fontTitulo.dispose();
        fontTexto.dispose();
        fontParpadeo.dispose();
    }

    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
