package com.example.laberintodelterror;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PantallaLaberinto implements Screen {

    private final LaberintoDelTerror juego;
    private final int nivelActual;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camara;
    private Bola bola;
    private NivelManager nivelManager;
    private Pixmap pixmapColisiones;

    private float velocidad = 50f;
    private final int MARGEN = 8;

    // Elementos de terror
    private List<Enemigo> enemigos;
    private Random random = new Random();
    private float tiempoGeneracionEnemigos = 0;
    private boolean juegoPausado = false;

    private boolean debugMode = false;

    public PantallaLaberinto(LaberintoDelTerror juego, int nivel) {
        this.juego = juego;
        this.nivelActual = nivel;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        camara = new OrthographicCamera();
        camara.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        nivelManager = new NivelManager(nivelActual);
        pixmapColisiones = nivelManager.getPixmapNivel();

        int startX = nivelManager.getStartX();
        int startY = nivelManager.getStartY();
        bola = new Bola(startX, startY);

        // 🔊 Asegurar que la música siga sonando
        if (Assets.musicaFondo != null && !Assets.musicaFondo.isPlaying()) {
            Assets.musicaFondo.play();
        }

        // Inicializar enemigos
        enemigos = new ArrayList<>();
        for (int i = 0; i < Math.min(1 + nivelActual, 4); i++) {
            Texture texEnemigo = Assets.screamers[random.nextInt(Assets.screamers.length)];
            enemigos.add(new Enemigo(texEnemigo));
        }

        Gdx.app.log("PantallaLaberinto", "Nivel " + nivelActual + " iniciado correctamente.");
    }


    @Override
    public void render(float delta) {
        if (juegoPausado) return;

        tiempoGeneracionEnemigos += delta;

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 🔹 Movimiento con acelerómetro
        float ax = Gdx.input.getAccelerometerX();
        float ay = Gdx.input.getAccelerometerY();

        // Para landscape
        float dx = ay * velocidad * delta * 3.5f;
        float dy = -ax * velocidad * delta * 3.5f;

        // 🔹 Movimiento dividido para evitar atravesar muros
        float mitadDX = dx / 2f;
        float mitadDY = dy / 2f;

        if (!colisionConMuro(bola.getX() + mitadDX, bola.getY() + mitadDY)) {
            bola.mover(mitadDX, mitadDY);
            if (!colisionConMuro(bola.getX() + mitadDX, bola.getY() + mitadDY)) {
                bola.mover(mitadDX, mitadDY);
            }
        }

        bola.actualizar(delta);

        // 🔹 Generar enemigos progresivamente
        if (tiempoGeneracionEnemigos > (8f - nivelActual)) {
            activarEnemigoAleatorio();
            tiempoGeneracionEnemigos = 0;
        }

        // 🔹 Actualizar enemigos
        for (Enemigo enemigo : enemigos) {
            if (enemigo.estaActivo()) {
                enemigo.actualizar(delta, bola.getX() + bola.getAncho()/2, bola.getY() + bola.getAlto()/2);

                // 👻 Si toca al jugador → screamer inmediato
                if (enemigo.colisionaConJugador(
                    bola.getX() + bola.getAncho()/2,
                    bola.getY() + bola.getAlto()/2,
                    bola.getAncho(), bola.getAlto())) {

                    Gdx.app.log("PantallaLaberinto", "¡FANTASMA DETECTADO! Screamer activado.");
                    juego.setScreen(new PantallaScreamer(juego, nivelActual));
                    return;
                }

                // Efecto de miedo si está cerca
                float distancia = distancia(bola.getX(), bola.getY(), enemigo.getX(), enemigo.getY());
                if (distancia < 200 && !bola.estaAsustada()) {
                    bola.activarParpadeo();
                }
            }
        }

        dibujarEscena();

        // 🔹 Detectar meta roja
        if (llegoMeta(bola.getX(), bola.getY())) {
            Gdx.app.log("PantallaLaberinto", "¡Meta roja detectada en nivel " + nivelActual + "!");
            if (nivelActual < 6)
                juego.setScreen(new PantallaLaberinto(juego, nivelActual + 1));
            else
                juego.setScreen(new PantallaMenu(juego));
        }

        if (debugMode && Gdx.graphics.getFrameId() % 60 == 0) {
            Gdx.app.log("DEBUG", "Bola en: (" + (int)bola.getX() + ", " + (int)bola.getY() + ")");
        }
    }

    private void dibujarEscena() {
        batch.begin();
        batch.setColor(0.3f, 0.3f, 0.4f, 1f);
        batch.draw(nivelManager.getImagenNivel(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        // 🔹 Luz alrededor de la bola
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.8f, 0.8f, 1f, 0.1f);
        shapeRenderer.circle(bola.getX() + bola.getAncho()/2, bola.getY() + bola.getAlto()/2, bola.getRadioLuz());
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();

        // 🔹 Enemigos y bola
        for (Enemigo enemigo : enemigos)
            if (enemigo.estaActivo()) enemigo.dibujar(batch);

        bola.dibujar(batch);
        batch.end();
    }

    private void activarEnemigoAleatorio() {
        for (Enemigo enemigo : enemigos) {
            if (!enemigo.estaActivo()) {
                float angulo = random.nextFloat() * MathUtils.PI2;
                float distancia = 300 + random.nextFloat() * 100;
                float x = bola.getX() + MathUtils.cos(angulo) * distancia;
                float y = bola.getY() + MathUtils.sin(angulo) * distancia;

                x = MathUtils.clamp(x, 50, Gdx.graphics.getWidth() - 50);
                y = MathUtils.clamp(y, 50, Gdx.graphics.getHeight() - 50);

                if (!colisionConMuro(x, y)) {
                    enemigo.activar(x, y);
                    Gdx.app.log("PantallaLaberinto", "Enemigo activado en: (" + (int)x + ", " + (int)y + ")");
                    break;
                }
            }
        }
    }

    private float distancia(float x1, float y1, float x2, float y2) {
        return (float)Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
    }

    private boolean colisionConMuro(float x, float y) {
        int ancho = (int) bola.getAncho();
        int alto = (int) bola.getAlto();

        float scaleX = pixmapColisiones.getWidth() / (float) Gdx.graphics.getWidth();
        float scaleY = pixmapColisiones.getHeight() / (float) Gdx.graphics.getHeight();

        int[][] puntos = {
            {MARGEN, MARGEN},
            {ancho - MARGEN, MARGEN},
            {MARGEN, alto - MARGEN},
            {ancho - MARGEN, alto - MARGEN},
            {ancho/2, MARGEN},
            {ancho/2, alto - MARGEN},
            {MARGEN, alto/2},
            {ancho - MARGEN, alto/2}
        };

        for (int[] p : puntos) {
            int px = (int)((x + p[0]) * scaleX);
            int py = (int)((y + p[1]) * scaleY);

            if (px < 0 || py < 0 || px >= pixmapColisiones.getWidth() || py >= pixmapColisiones.getHeight())
                return true;

            int pixel = pixmapColisiones.getPixel(px, pixmapColisiones.getHeight() - 1 - py);
            int r = (pixel >> 24) & 0xff;
            int g = (pixel >> 16) & 0xff;
            int b = (pixel >> 8) & 0xff;

            if (r < 100 && g < 100 && b < 100) return true;
        }
        return false;
    }

    // 🎯 Detectar meta roja
    private boolean llegoMeta(float x, float y) {
        int ancho = (int) bola.getAncho();
        int alto  = (int) bola.getAlto();

        float scaleX = pixmapColisiones.getWidth()  / (float) Gdx.graphics.getWidth();
        float scaleY = pixmapColisiones.getHeight() / (float) Gdx.graphics.getHeight();

        int cx = (int)((x + ancho/2) * scaleX);
        int cy = (int)((y + alto/2)  * scaleY);

        if (cx < 0 || cy < 0 || cx >= pixmapColisiones.getWidth() || cy >= pixmapColisiones.getHeight())
            return false;

        int pixel = pixmapColisiones.getPixel(cx, pixmapColisiones.getHeight() - 1 - cy);
        int r = (pixel >> 24) & 0xff;
        int g = (pixel >> 16) & 0xff;
        int b = (pixel >> 8) & 0xff;

        return r > 180 && g < 100 && b < 100;
        //return r > 230 && g < 50 && b < 50; // 🔴 Detectar rojo fuerte
    }

    @Override public void resize(int width, int height) {
        camara.setToOrtho(false, width, height);
        camara.update();
        batch.setProjectionMatrix(camara.combined);
        shapeRenderer.setProjectionMatrix(camara.combined);
    }

    @Override public void pause() { juegoPausado = true; }
    @Override public void resume() { juegoPausado = false; }
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (nivelManager != null) nivelManager.dispose();
        if (bola != null) bola.dispose();
        if (pixmapColisiones != null) pixmapColisiones.dispose();
    }
}
