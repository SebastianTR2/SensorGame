package com.example.laberintodelterror;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PantallaMenu implements Screen {

    private final LaberintoDelTerror juego;
    private SpriteBatch batch;
    private Texture fondo, botonJugar;
    private float botonX, botonY, botonAncho, botonAlto;

    public PantallaMenu(LaberintoDelTerror juego) {
        this.juego = juego;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        fondo = new Texture("fondo_menu.png");
        botonJugar = new Texture("boton_jugar.png");

        botonAncho = 300;
        botonAlto = 100;
        botonX = (Gdx.graphics.getWidth() - botonAncho) / 2f;
        botonY = Gdx.graphics.getHeight() / 3f;

        // 🎵 Iniciar música de fondo si no está sonando
        if (Assets.musicaFondo != null && !Assets.musicaFondo.isPlaying()) {
            Assets.musicaFondo.play();
        }
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(fondo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(botonJugar, botonX, botonY, botonAncho, botonAlto);
        batch.end();

        if (Gdx.input.justTouched()) {
            float x = Gdx.input.getX();
            float y = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (x > botonX && x < botonX + botonAncho && y > botonY && y < botonY + botonAlto) {
                juego.setScreen(new PantallaInstrucciones(juego));
            }
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { batch.dispose(); fondo.dispose(); botonJugar.dispose(); }
}
