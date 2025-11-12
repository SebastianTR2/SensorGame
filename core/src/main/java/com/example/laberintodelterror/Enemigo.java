package com.example.laberintodelterror;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Enemigo {
    public float x, y, w, h, velocidad;
    public Texture textura;
    public String tipo;
    private float tiempo;

    public Enemigo(String tipo, float x, float y, float velocidad) {
        this.tipo = tipo;
        this.x = x;
        this.y = y;
        this.velocidad = velocidad;
        this.w = 64;
        this.h = 64;

        // Cargar textura según tipo
        switch (tipo) {
            case "errante":
                textura = new Texture("enemigo/enemigo1.png");
                break;
            case "guardia":
                textura = new Texture("enemigo/enemigo2.png");
                break;
            case "cazador":
                textura = new Texture("enemigo/enemigo3.png");
                break;
            case "fantasma":
                textura = new Texture("enemigo/enemigo4.png");
                break;
        }
    }

    public void mover(float delta, float jugadorX, float jugadorY) {
        tiempo += delta;

        switch (tipo) {
            case "errante":
                // movimiento aleatorio
                x += MathUtils.sin(tiempo) * velocidad;
                y += MathUtils.cos(tiempo * 0.8f) * velocidad;
                break;

            case "guardia":
                // patrulla horizontal
                x += MathUtils.sin(tiempo) * velocidad * 2f;
                break;

            case "cazador":
                // persigue jugador si está cerca
                float dx = jugadorX - x;
                float dy = jugadorY - y;
                float distancia = (float) Math.sqrt(dx * dx + dy * dy);
                if (distancia < 250) {
                    x += (dx / distancia) * velocidad * 3;
                    y += (dy / distancia) * velocidad * 3;
                }
                break;

            case "fantasma":
                // atraviesa paredes lentamente
                x += MathUtils.sin(tiempo * 0.5f) * velocidad * 0.8f;
                y += MathUtils.cos(tiempo * 0.7f) * velocidad * 0.8f;
                break;
        }
    }

    public void dibujar(SpriteBatch batch) {
        batch.draw(textura, x, y, w, h);
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, w, h);
    }

    public void dispose() {
        if (textura != null) textura.dispose();
    }
}
