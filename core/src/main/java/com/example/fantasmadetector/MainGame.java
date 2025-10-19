package com.example.fantasmadetector;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

public class MainGame extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture fantasma;
    private Sound scream;
    private BitmapFont font;
    private GlyphLayout layout;

    private boolean mostrarFantasma = false;
    private boolean juegoActivo = false;
    private int puntuacion = 0;
    private float tiempoFantasma = 0;
    private final float TIEMPO_MAX_FANTASMA = 1.5f;

    // Efectos de terror
    private float parpadeo = 0;
    private boolean luzEncendida = true;
    private boolean audioCargado = false;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // 1. PRIMERO intentar cargar la imagen ghost.jpg
        try {
            fantasma = new Texture("ghost.jpg");
            System.out.println("Imagen ghost.jpg cargada correctamente");
        } catch (Exception e) {
            System.out.println("No se pudo cargar ghost.jpg, creando fantasma programatico");
            fantasma = crearTexturaFantasmaProgramatico();
        }

        // 2. LUEGO intentar cargar el audio
        try {
            scream = Gdx.audio.newSound(Gdx.files.internal("scream.mp3"));
            audioCargado = true;
            System.out.println("Audio scream.mp3 cargado correctamente");
        } catch (Exception e) {
            System.out.println("No se pudo cargar scream.mp3 - modo silencioso");
            audioCargado = false;
        }

        font = new BitmapFont();
        layout = new GlyphLayout();
        font.setColor(Color.RED);

        System.out.println("Juego iniciado - Audio: " + (audioCargado ? "ACTIVO" : "SILENCIOSO"));
    }

    // Solo si falla la carga de ghost.jpg
    private Texture crearTexturaFantasmaProgramatico() {
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(150, 200,
            com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);

        pixmap.setColor(0.9f, 0.9f, 1.0f, 0.8f);
        pixmap.fillCircle(75, 120, 40);

        for (int i = 0; i < 150; i += 15) {
            int altura = MathUtils.random(20, 40);
            pixmap.fillRectangle(i, 80, 8, altura);
        }

        pixmap.setColor(Color.RED);
        pixmap.fillCircle(55, 130, 10);
        pixmap.fillCircle(95, 130, 10);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    @Override
    public void render() {
        updateEfectosTerror();

        // Fondo que cambia
        if (luzEncendida) {
            Gdx.gl.glClearColor(0.05f, 0.02f, 0.08f, 1);
        } else {
            Gdx.gl.glClearColor(0.01f, 0.01f, 0.03f, 1);
        }
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        if (!juegoActivo) {
            dibujarPantallaInicio();
        } else {
            dibujarJuego();
        }

        batch.end();
    }

    private void updateEfectosTerror() {
        if (!juegoActivo) return;

        // Parpadeo de luces
        parpadeo += Gdx.graphics.getDeltaTime();
        if (parpadeo > 0.3f) {
            luzEncendida = !luzEncendida;
            parpadeo = 0;
        }

        if (mostrarFantasma) {
            tiempoFantasma += Gdx.graphics.getDeltaTime();
            if (tiempoFantasma >= TIEMPO_MAX_FANTASMA) {
                mostrarFantasma = false;
                tiempoFantasma = 0;
                puntuacion += 10;
                System.out.println("Puntos: " + puntuacion);
            }
        }

        if (puntuacion >= 100) {
            juegoActivo = false;
        }
    }

    public void activarFantasma() {
        if (juegoActivo && !mostrarFantasma) {
            mostrarFantasma = true;
            tiempoFantasma = 0;

            // Reproducir sonido si está disponible
            if (audioCargado && scream != null) {
                try {
                    long soundId = scream.play(1.0f);
                    System.out.println("Sonido reproducido - ID: " + soundId);
                } catch (Exception e) {
                    System.out.println("Error al reproducir sonido");
                }
            } else {
                System.out.println("Fantasma activado (sin sonido)");
            }
        }
    }

    private void dibujarPantallaInicio() {
        font.setColor(Color.RED);
        font.getData().setScale(2);
        String titulo = "FANTASMA DETECTOR";
        layout.setText(font, titulo);
        float x = (Gdx.graphics.getWidth() - layout.width) / 2;
        font.draw(batch, titulo, x, Gdx.graphics.getHeight() * 0.7f);

        font.getData().setScale(1.2f);
        String instruccion = "Acerca tu mano al sensor";
        layout.setText(font, instruccion);
        x = (Gdx.graphics.getWidth() - layout.width) / 2;
        font.draw(batch, instruccion, x, Gdx.graphics.getHeight() * 0.5f);

        // Mostrar estado del audio
        font.getData().setScale(1);
        String estadoAudio = "Audio: " + (audioCargado ? "ACTIVO" : "SILENCIOSO");
        layout.setText(font, estadoAudio);
        x = (Gdx.graphics.getWidth() - layout.width) / 2;
        font.draw(batch, estadoAudio, x, Gdx.graphics.getHeight() * 0.4f);

        font.getData().setScale(1);
        String comenzar = "Toca para comenzar";
        layout.setText(font, comenzar);
        x = (Gdx.graphics.getWidth() - layout.width) / 2;
        font.draw(batch, comenzar, x, Gdx.graphics.getHeight() * 0.3f);

        if (Gdx.input.justTouched()) {
            juegoActivo = true;
            puntuacion = 0;
            System.out.println("Juego iniciado");
        }
    }

    private void dibujarJuego() {
        if (mostrarFantasma) {
            float scale = 1.0f + (float)Math.sin(tiempoFantasma * 15) * 0.2f;
            float width = fantasma.getWidth() * scale;
            float height = fantasma.getHeight() * scale;
            float x = (Gdx.graphics.getWidth() - width) / 2;
            float y = (Gdx.graphics.getHeight() - height) / 2;

            batch.draw(fantasma, x, y, width, height);
        }

        font.setColor(Color.RED);
        font.getData().setScale(1.5f);
        font.draw(batch, "Locura: " + puntuacion + "/100", 20, Gdx.graphics.getHeight() - 30);
        font.draw(batch, "Acerca mano al sensor", 20, 60);

        // Mostrar estado del audio en juego también
        font.getData().setScale(1);
        font.setColor(audioCargado ? Color.GREEN : Color.YELLOW);
        font.draw(batch, "Audio: " + (audioCargado ? "ACTIVO" : "SILENCIOSO"), 20, 90);

        if (puntuacion >= 100) {
            font.setColor(Color.RED);
            font.getData().setScale(2);
            String ganador = "¡CAÍSTE EN LA LOCURA!";
            layout.setText(font, ganador);
            float x = (Gdx.graphics.getWidth() - layout.width) / 2;
            font.draw(batch, ganador, x, Gdx.graphics.getHeight() * 0.5f);

            if (Gdx.input.justTouched()) {
                reiniciarJuego();
            }
        }
    }

    private void reiniciarJuego() {
        juegoActivo = true;
        puntuacion = 0;
        mostrarFantasma = false;
        tiempoFantasma = 0;
        System.out.println("Juego reiniciado");
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (fantasma != null) fantasma.dispose();
        if (scream != null) scream.dispose();
        if (font != null) font.dispose();
        System.out.println("Recursos liberados");
    }
}
