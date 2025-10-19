package com.example.fantasmadetector;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

    private float parpadeo = 0;
    private boolean luzEncendida = true;
    private boolean recursosCargados = false;
    private boolean cargandoRecursos = false;

    // URLs de ejemplo - CAMBIA ESTAS POR TUS URLs REALES
    private final String URL_FANTASMA = "https://i.ytimg.com/vi/QPnytK8nh_0/maxresdefault.jpg";
    private final String URL_AUDIO = "https://drive.google.com/uc?id=1lsp7OgXx_ohMNiUFoIiKciHVcidDs7zm&export=download";

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        layout = new GlyphLayout();
        font.setColor(Color.RED);

        // Cargar fantasma por defecto inmediatamente
        fantasma = crearTexturaFantasmaProgramatico();

        // Iniciar carga de recursos desde internet
        cargarRecursosDesdeInternet();
    }

    private void cargarRecursosDesdeInternet() {
        if (cargandoRecursos) return;

        cargandoRecursos = true;
        System.out.println("Iniciando carga de recursos desde internet...");

        // Cargar imagen
        new Thread(new Runnable() {
            public void run() {
                cargarImagenDesdeURL(URL_FANTASMA);
            }
        }).start();

        // Cargar audio
        new Thread(new Runnable() {
            public void run() {
                cargarAudioDesdeURL(URL_AUDIO);
            }
        }).start();
    }

    private void cargarImagenDesdeURL(String imageUrl) {
        InputStream input = null;
        try {
            System.out.println("Descargando imagen: " + imageUrl);

            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();

            input = connection.getInputStream();

            // Leer todos los bytes manualmente
            byte[] buffer = new byte[1024];
            java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            byte[] imageData = outputStream.toByteArray();

            final Pixmap pixmap = new Pixmap(imageData, 0, imageData.length);

            Gdx.app.postRunnable(new Runnable() {
                public void run() {
                    if (fantasma != null) {
                        fantasma.dispose();
                    }
                    fantasma = new Texture(pixmap);
                    pixmap.dispose();
                    System.out.println("Imagen cargada desde internet exitosamente");
                    verificarRecursosCargados();
                }
            });

        } catch (Exception e) {
            System.out.println("Error cargando imagen desde URL: " + e.getMessage());
        } finally {
            if (input != null) {
                try { input.close(); } catch (Exception e) {}
            }
        }
    }

    private void cargarAudioDesdeURL(String audioUrl) {
        InputStream input = null;
        OutputStream output = null;
        try {
            System.out.println("Descargando audio: " + audioUrl);

            URL url = new URL(audioUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();

            input = connection.getInputStream();

            // Guardar temporalmente
            final com.badlogic.gdx.files.FileHandle tempFile =
                Gdx.files.local("temp_audio.mp3");

            output = tempFile.write(false);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }

            Gdx.app.postRunnable(new Runnable() {
                public void run() {
                    try {
                        scream = Gdx.audio.newSound(tempFile);
                        System.out.println("Audio cargado desde internet exitosamente");
                        verificarRecursosCargados();
                    } catch (Exception e) {
                        System.out.println("Error creando sonido desde URL: " + e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            System.out.println("Error descargando audio desde URL: " + e.getMessage());
        } finally {
            if (input != null) {
                try { input.close(); } catch (Exception e) {}
            }
            if (output != null) {
                try { output.close(); } catch (Exception e) {}
            }
        }
    }

    private void verificarRecursosCargados() {
        if (fantasma != null && scream != null) {
            recursosCargados = true;
            cargandoRecursos = false;
            System.out.println("Todos los recursos cargados desde internet");
        }
    }

    private Texture crearTexturaFantasmaProgramatico() {
        Pixmap pixmap = new Pixmap(150, 200, Pixmap.Format.RGBA8888);

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

            if (scream != null) {
                try {
                    scream.play(1.0f);
                } catch (Exception e) {
                    System.out.println("Error reproduciendo sonido");
                }
            }
        }
    }

    private void dibujarPantallaInicio() {
        // TÍTULO PRINCIPAL - MÁS GRANDE
        font.setColor(Color.RED);
        font.getData().setScale(4.0f); // Aumentado de 2 a 4
        String titulo = "FANTASMA DETECTOR";
        layout.setText(font, titulo);
        float x = (Gdx.graphics.getWidth() - layout.width) / 2;
        font.draw(batch, titulo, x, Gdx.graphics.getHeight() * 0.7f);

        // INSTRUCCIÓN - MÁS GRANDE
        font.getData().setScale(2.5f); // Aumentado de 1.2 a 2.5
        String instruccion = "Acerca tu mano al sensor";
        layout.setText(font, instruccion);
        x = (Gdx.graphics.getWidth() - layout.width) / 2;
        font.draw(batch, instruccion, x, Gdx.graphics.getHeight() * 0.5f);

        // ESTADO DE CARGA - MÁS GRANDE
        font.getData().setScale(1.8f); // Aumentado de 1 a 1.8
        String estadoRecursos;
        if (cargandoRecursos) {
            estadoRecursos = "Cargando recursos de internet...";
            font.setColor(Color.YELLOW);
        } else if (recursosCargados) {
            estadoRecursos = "Recursos cargados desde internet";
            font.setColor(Color.GREEN);
        } else {
            estadoRecursos = "Usando recursos por defecto";
            font.setColor(Color.ORANGE);
        }

        layout.setText(font, estadoRecursos);
        x = (Gdx.graphics.getWidth() - layout.width) / 2;
        font.draw(batch, estadoRecursos, x, Gdx.graphics.getHeight() * 0.4f);

        // BOTÓN COMENZAR - MÁS GRANDE
        font.getData().setScale(2.0f); // Aumentado de 1 a 2
        font.setColor(Color.WHITE);
        String comenzar = "Toca para comenzar";
        layout.setText(font, comenzar);
        x = (Gdx.graphics.getWidth() - layout.width) / 2;
        font.draw(batch, comenzar, x, Gdx.graphics.getHeight() * 0.3f);

        if (Gdx.input.justTouched()) {
            juegoActivo = true;
            puntuacion = 0;
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

        // CONTADOR DE LOCURA - MÁS GRANDE
        font.setColor(Color.RED);
        font.getData().setScale(2.5f); // Aumentado de 1.5 a 2.5
        font.draw(batch, "Locura: " + puntuacion + "/100", 30, Gdx.graphics.getHeight() - 50);

        // INSTRUCCIÓN EN JUEGO - MÁS GRANDE
        font.getData().setScale(2.0f); // Aumentado de 1.5 a 2.0
        font.draw(batch, "Acerca mano al sensor", 30, 100);

        // ESTADO DE AUDIO EN JUEGO - MÁS GRANDE
        font.getData().setScale(1.5f); // Aumentado de 1 a 1.5
        font.setColor(recursosCargados ? Color.GREEN : Color.YELLOW);
        font.draw(batch, "Audio: " + (recursosCargados ? "ACTIVO" : "SILENCIOSO"), 30, 150);

        if (puntuacion >= 100) {
            // MENSAJE FINAL - MÁS GRANDE
            font.setColor(Color.RED);
            font.getData().setScale(3.5f); // Aumentado de 2 a 3.5
            String ganador = "¡CAÍSTE EN LA LOCURA!";
            layout.setText(font, ganador);
            float x = (Gdx.graphics.getWidth() - layout.width) / 2;
            font.draw(batch, ganador, x, Gdx.graphics.getHeight() * 0.6f);

            // INSTRUCCIÓN REINICIAR - MÁS GRANDE
            font.getData().setScale(2.0f); // Aumentado de 1 a 2
            font.setColor(Color.WHITE);
            String reiniciar = "Toca para jugar otra vez";
            layout.setText(font, reiniciar);
            x = (Gdx.graphics.getWidth() - layout.width) / 2;
            font.draw(batch, reiniciar, x, Gdx.graphics.getHeight() * 0.4f);

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
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (fantasma != null) fantasma.dispose();
        if (scream != null) scream.dispose();
        if (font != null) font.dispose();

        // Limpiar archivo temporal
        try {
            com.badlogic.gdx.files.FileHandle tempFile = Gdx.files.local("temp_audio.mp3");
            if (tempFile.exists()) {
                tempFile.delete();
            }
        } catch (Exception e) {
            // Ignorar errores de limpieza
        }
    }
}
