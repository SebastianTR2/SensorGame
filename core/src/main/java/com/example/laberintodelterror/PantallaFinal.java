package com.example.laberintodelterror;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

public class PantallaFinal implements Screen {

    private final LaberintoDelTerror juego;
    private SpriteBatch batch;
    private Texture fondo;
    private BitmapFont fontTitulo, fontTexto, fontParpadeo;
    private float tiempo = 0;
    private Sound sonidoVictoria;

    public PantallaFinal(LaberintoDelTerror juego) {
        this.juego = juego;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        // Fondo (si no hay imagen, usa el del menú)
        fondo = Assets.fondoMenu;

        // Fuentes
        fontTitulo = new BitmapFont();
        fontTitulo.getData().setScale(3.5f);
        fontTitulo.setColor(Color.RED);

        fontTexto = new BitmapFont();
        fontTexto.getData().setScale(2.0f);
        fontTexto.setColor(Color.WHITE);

        fontParpadeo = new BitmapFont();
        fontParpadeo.getData().setScale(1.5f);
        fontParpadeo.setColor(Color.LIGHT_GRAY);

        // Sonido de victoria (si tienes uno llamado victoria.mp3)
        if (Gdx.files.internal("victoria.mp3").exists()) {
            sonidoVictoria = Gdx.audio.newSound(Gdx.files.internal("victoria.mp3"));
            sonidoVictoria.play(0.7f);
        }

        // Detiene música de fondo
        if (Assets.musicaFondo != null && Assets.musicaFondo.isPlaying())
            Assets.musicaFondo.stop();

        // Reinicia progreso al mostrar esta pantalla
        juego.progreso.reiniciar();
    }

    @Override
    public void render(float delta) {
        tiempo += delta;
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float ancho = Gdx.graphics.getWidth();
        float alto = Gdx.graphics.getHeight();
        float centroX = ancho / 2f;

        batch.begin();
        batch.draw(fondo, 0, 0, ancho, alto);

        // Título
        fontTitulo.draw(batch, "¡HAS ESCAPADO!", centroX, alto * 0.75f, 0, Align.center, false);

        // Texto
        String texto =
            "Lograste superar los 5 niveles del Laberinto del Terror.\n\n" +
                "Has demostrado valentía, reflejos y sangre fría.\n\n" +
                "Pero cuidado... el laberinto nunca duerme.\n\n" +
                "Podrías volver a entrar... ¿te atreves?";
        fontTexto.draw(batch, texto, centroX - (ancho * 0.4f), alto * 0.55f, ancho * 0.8f, Align.center, true);

        // Texto parpadeante
        float alpha = 0.4f + 0.6f * (float)Math.abs(Math.sin(tiempo * 3));
        fontParpadeo.setColor(1, 1, 1, alpha);
        fontParpadeo.draw(batch, "Toca la pantalla para volver al menú", centroX, alto * 0.15f, 0, Align.center, true);
        batch.end();

        if (Gdx.input.justTouched()) {
            juego.setScreen(new PantallaMenu(juego));
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
        if (fontTitulo != null) fontTitulo.dispose();
        if (fontTexto != null) fontTexto.dispose();
        if (fontParpadeo != null) fontParpadeo.dispose();
        if (sonidoVictoria != null) sonidoVictoria.dispose();
    }
}
