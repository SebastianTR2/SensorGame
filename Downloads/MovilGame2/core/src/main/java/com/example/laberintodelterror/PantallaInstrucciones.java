package com.example.laberintodelterror;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

public class PantallaInstrucciones implements Screen {

    private final LaberintoDelTerror juego;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Texture fondo;
    private BitmapFont fontTitulo, fontTexto;
    private float tiempoParpadeo = 0;

    public PantallaInstrucciones(LaberintoDelTerror juego) {
        this.juego = juego;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        fondo = new Texture("fondo_menu.png");

        // 🔠 Fuentes
        fontTitulo = new BitmapFont();
        fontTitulo.getData().setScale(3.2f);
        fontTitulo.setColor(Color.RED);

        fontTexto = new BitmapFont();
        fontTexto.getData().setScale(2.2f);
        fontTexto.setColor(Color.WHITE);

        // 🎵 Música
        if (Assets.musicaFondo != null && !Assets.musicaFondo.isPlaying()) {
            Assets.musicaFondo.setLooping(true);
            Assets.musicaFondo.play();
        }
    }

    @Override
    public void render(float delta) {
        tiempoParpadeo += delta;

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(fondo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        float ancho = Gdx.graphics.getWidth();
        float alto = Gdx.graphics.getHeight();
        float centroX = ancho / 2f;

        // 🩸 Título centrado
        fontTitulo.draw(batch, "INSTRUCCIONES", centroX, alto * 0.7f, 0, Align.center, false);

        // 📜 Texto centrado
        String texto =
            "Inclina el teléfono para mover la bola.\n\n" +
                "Evita chocar con los enemigos del laberinto.\n\n" +
                "Llega a la meta roja para pasar al siguiente nivel.\n\n" +
                "Si te toca un fantasma... ¡prepárate para............ :c  !";

        fontTexto.draw(batch, texto, centroX - (ancho * 0.4f), alto * 0.48f, ancho * 0.8f, Align.center, true);
        batch.end();

        // ✴️ Línea parpadeante en la esquina inferior derecha
        float alpha = (float) (0.4f + 0.6f * Math.abs(Math.sin(tiempoParpadeo * 3)));

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 1, 1, alpha);
        float margenDerecha = ancho * 0.05f;
        float margenInferior = alto * 0.08f;
        shapeRenderer.rect(ancho - margenDerecha - 150, margenInferior, 150, 5); // ← línea horizontal
        shapeRenderer.end();

        // 👉 Avanzar al nivel 1
        if (Gdx.input.justTouched()) {
            juego.setScreen(new PantallaLaberinto(juego, 1));
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
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (fondo != null) fondo.dispose();
        if (fontTitulo != null) fontTitulo.dispose();
        if (fontTexto != null) fontTexto.dispose();
    }
}
