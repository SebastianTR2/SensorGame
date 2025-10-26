// Enemigo.java (nueva clase)
package com.example.laberintodelterror;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import java.util.Random;

public class Enemigo {
    private Texture textura;
    private float x, y;
    private float ancho = 45, alto = 45;
    private float velocidad;
    private boolean activo = false;
    private Random random = new Random();
    private Vector2 posicionObjetivo;
    private float tiempoCambioDireccion = 0;

    public Enemigo(Texture textura) {
        this.textura = textura;
        this.velocidad = 25 + random.nextFloat() * 35;
        this.posicionObjetivo = new Vector2();
    }

    public void activar(float x, float y) {
        this.x = x;
        this.y = y;
        this.activo = true;
        establecerNuevoObjetivo();
    }

    public void desactivar() {
        this.activo = false;
    }

    public boolean estaActivo() {
        return activo;
    }

    public void actualizar(float delta, float jugadorX, float jugadorY) {
        if (!activo) return;

        tiempoCambioDireccion += delta;

        // Cambiar dirección ocasionalmente o cada 2-3 segundos
        if (random.nextFloat() < 0.02f || tiempoCambioDireccion > (2 + random.nextFloat() * 2)) {
            establecerNuevoObjetivo();
            tiempoCambioDireccion = 0;
        }

        // 70% de probabilidad de seguir al jugador
        if (random.nextFloat() < 0.7f) {
            posicionObjetivo.set(jugadorX, jugadorY);
        }

        // Movimiento hacia el objetivo
        Vector2 direccion = new Vector2(posicionObjetivo.x - x, posicionObjetivo.y - y);
        if (direccion.len() > 10) {
            direccion.nor();
            x += direccion.x * velocidad * delta;
            y += direccion.y * velocidad * delta;
        }
    }

    private void establecerNuevoObjetivo() {
        posicionObjetivo.set(x + random.nextFloat() * 200 - 100,
            y + random.nextFloat() * 200 - 100);
    }

    public boolean colisionaConJugador(float jugadorX, float jugadorY, float jugadorAncho, float jugadorAlto) {
        return (Math.abs(x - jugadorX) < (ancho + jugadorAncho) / 2 &&
            Math.abs(y - jugadorY) < (alto + jugadorAlto) / 2);
    }

    public void dibujar(SpriteBatch batch) {
        if (activo) {
            batch.setColor(1, 0.4f, 0.4f, 0.8f); // Rojo fantasmal
            batch.draw(textura, x - ancho/2, y - alto/2, ancho, alto);
            batch.setColor(1, 1, 1, 1); // Restaurar color
        }
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getAncho() { return ancho; }
    public float getAlto() { return alto; }
}
