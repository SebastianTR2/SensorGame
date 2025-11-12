package com.example.laberintodelterror;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Bola {
    private float x, y;
    private final float w = 48, h = 48;
    private boolean asustada = false;
    private float t = 0;

    public Bola(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void mover(float dx, float dy) {
        x += dx;
        y += dy;
        if (asustada) {
            x += Math.random() * 2 - 1;
            y += Math.random() * 2 - 1;
        }
    }

    public void actualizar(float delta) {
        if (asustada) {
            t += delta;
            if (t > 0.8f) {
                asustada = false;
                t = 0;
            }
        }
    }

    public void setPosicion(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void asustar() {
        asustada = true;
        t = 0;
    }

    public void dibujar(SpriteBatch b) {
        if (asustada)
            b.setColor(1f, 0.7f, 0.7f, 1f);
        b.draw(Assets.bolaTex, x, y, w, h);
        b.setColor(Color.WHITE);
    }

    //  Getter del área de colisión
    public Rectangle getRect() {
        return new Rectangle(x, y, w, h);
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getW() { return w; }
    public float getH() { return h; }
}
