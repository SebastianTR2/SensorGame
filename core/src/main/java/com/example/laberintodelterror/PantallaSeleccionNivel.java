package com.example.laberintodelterror;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class PantallaSeleccionNivel implements Screen {
    private final LaberintoDelTerror juego;
    private SpriteBatch batch;
    private ShapeRenderer shape;
    private BitmapFont font;
    private List<Rectangle> zonas = new ArrayList<>();
    private int niveles = 5;

    public PantallaSeleccionNivel(LaberintoDelTerror juego) {
        this.juego = juego;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shape = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(2);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float w = Gdx.graphics.getWidth(), h = Gdx.graphics.getHeight();

        int desbloqueado = juego.progreso.getNivelDesbloqueado();
        zonas.clear();

        // 🔳 Dibujar los recuadros con texto
        shape.begin(ShapeRenderer.ShapeType.Line);
        for (int i = 1; i <= niveles; i++) {
            float y = h * (0.75f - (i - 1) * 0.12f);
            float boxW = w * 0.5f, boxH = 80;
            float x = (w - boxW) / 2;

            // Borde del recuadro
            shape.setColor(i <= desbloqueado ? Color.GREEN : Color.DARK_GRAY);
            shape.rect(x, y - boxH, boxW, boxH);
            zonas.add(new Rectangle(x, y - boxH, boxW, boxH));
        }
        shape.end();

        // 📝 Texto
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "SELECCIONA UN NIVEL", w / 2, h * 0.9f, 0, Align.center, false);

        for (int i = 1; i <= niveles; i++) {
            float y = h * (0.75f - (i - 1) * 0.12f);
            String texto = (i <= desbloqueado) ? "Nivel " + i : "Nivel " + i + " 🔒";
            font.setColor(i <= desbloqueado ? Color.WHITE : Color.GRAY);
            font.draw(batch, texto, w / 2, y - 20, 0, Align.center, false);
        }
        batch.end();

        // 🖱 Detección táctil
        if (Gdx.input.justTouched()) {
            float yTouch = Gdx.graphics.getHeight() - Gdx.input.getY();
            float xTouch = Gdx.input.getX();

            for (int i = 0; i < zonas.size(); i++) {
                Rectangle r = zonas.get(i);
                int nivel = i + 1;
                if (r.contains(xTouch, yTouch) && nivel <= desbloqueado) {
                    juego.progreso.setNivel(nivel);
                    juego.progreso.setSubnivel(1);
                    juego.setScreen(new PantallaLaberinto(juego));
                    dispose();
                    return;
                }
            }
        }
    }

    @Override public void dispose() { batch.dispose(); shape.dispose(); font.dispose(); }
    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
