package com.example.laberintodelterror;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Puerta {
    private float x, y;
    private float w = 80, h = 100; // 🔸 tamaño más grande
    public boolean abierta = false;

    private Texture texCerrada;
    private Texture texAbierta;

    public Puerta() {
        texCerrada = Assets.puertaCerradaTex;
        texAbierta = Assets.puertaAbiertaTex;
    }

    public void colocar(float x, float y) {
        this.x = x;
        this.y = y;
        this.abierta = false;
    }

    public void abrir() {
        abierta = true;
    }

    public void dibujar(SpriteBatch batch) {
        Texture actual = abierta ? texCerrada : texAbierta;
        batch.draw(actual, x - w / 2, y - h / 2, w, h); // centrada también
    }

    public Rectangle getRect() {
        return new Rectangle(x - w / 2, y - h / 2, w, h);
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getW() { return w; }
    public float getH() { return h; }

}
