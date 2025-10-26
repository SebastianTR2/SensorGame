// Bola.java (versión mejorada con efectos de terror)
package com.example.laberintodelterror;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

public class Bola {
    private Texture textura;
    private float x, y;
    private float ancho = 50, alto = 50;
    private float radioLuz = 120;
    private boolean parpadeando = false;
    private float tiempoParpadeo = 0;
    private Color colorBase;
    private boolean asustada = false;
    private int vidas = 3;

    public float getX() { return x; }
    public float getY() { return y; }
    public float getAncho() { return ancho; }
    public float getAlto() { return alto; }
    public float getRadioLuz() {
        if (parpadeando) {
            return radioLuz * (0.6f + 0.4f * MathUtils.sin(tiempoParpadeo * 15f));
        }
        return radioLuz;
    }
    public boolean estaAsustada() { return asustada; }
    public int getVidas() { return vidas; }
    public void perderVida() { vidas--; }

    public Bola(float x, float y) {
        this.x = x;
        this.y = y;
        textura = new Texture("bola.png");
        colorBase = new Color(0.8f, 0.8f, 1f, 1f);
    }

    public void mover(float dx, float dy) {
        x += dx;
        y += dy;

        // Efecto de "miedo" - movimiento más errático cuando está asustada
        if (asustada) {
            x += (Math.random() - 0.5f) * 2f;
            y += (Math.random() - 0.5f) * 2f;
        }
    }

    public void actualizar(float delta) {
        if (parpadeando) {
            tiempoParpadeo += delta;
            if (tiempoParpadeo > 0.8f) {
                parpadeando = false;
                tiempoParpadeo = 0;
            }
        }

        // Recuperarse gradualmente del susto
        if (asustada && !parpadeando) {
            asustada = false;
        }
    }

    public void activarParpadeo() {
        parpadeando = true;
        tiempoParpadeo = 0;
        asustada = true;
    }

    public Color getColor() {
        if (parpadeando) {
            float intensidad = 0.7f + 0.3f * MathUtils.sin(tiempoParpadeo * 20f);
            return new Color(intensidad, 0.3f, 0.3f, 1f);
        } else if (asustada) {
            return new Color(0.9f, 0.9f, 0.2f, 1f);
        }
        return colorBase;
    }

    public boolean colisionaConMuro(Pixmap pixmap) {
        int[][] puntos = {
            {0, 0}, {(int) ancho, 0}, {0, (int) alto}, {(int) ancho, (int) alto},
            {(int) ancho/2, 0}, {(int) ancho/2, (int) alto}, {0, (int) alto/2}, {(int) ancho, (int) alto/2}
        };

        float scaleX = pixmap.getWidth() / (float) Gdx.graphics.getWidth();
        float scaleY = pixmap.getHeight() / (float) Gdx.graphics.getHeight();

        for (int[] p : puntos) {
            int px = (int)((x + p[0]) * scaleX);
            int py = (int)((y + p[1]) * scaleY);

            if (px < 0 || py < 0 || px >= pixmap.getWidth() || py >= pixmap.getHeight())
                continue;

            int pixel = pixmap.getPixel(px, pixmap.getHeight() - 1 - py);
            int alpha = (pixel >> 24) & 0xff;

            if (alpha > 100) return true;
        }
        return false;
    }

    public void dibujar(SpriteBatch batch) {
        Color colorActual = getColor();
        batch.setColor(colorActual);
        batch.draw(textura, x, y, ancho, alto);
        batch.setColor(Color.WHITE); // Resetear color
    }

    public void dispose() {
        if (textura != null) textura.dispose();
    }
}
