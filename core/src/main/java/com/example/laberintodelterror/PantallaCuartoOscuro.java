package com.example.laberintodelterror;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class PantallaCuartoOscuro implements Screen {
    private final LaberintoDelTerror juego;
    private SpriteBatch batch;
    private ShapeRenderer sr;
    private BitmapFont hud;

    private Pixmap pix;
    private Texture texLaberinto, texRuna, texMeta, texBoss, texLuz;
    private Bola bola;
    private boolean[][] colisionMapa;

    private boolean runaTomada = false;
    private boolean metaActiva = false;
    private boolean atrapado = false;

    private float runaX, runaY;
    private float metaX, metaY;
    private float bossX, bossY;
    private float bossVel = 65;
    private float bossW = 140, bossH = 140;
    private float bossDirX = 1, bossDirY = 0;
    private float cambioDirTimer = 0;
    private boolean persiguiendo = false;

    private Sound grito;
    private float tiempo = 0;
    private float radioLuz = 220;
    private float radioActual = 220;
    private boolean enAlerta = false;
    private float alertaTimer = 0;

    private final Random rnd = new Random();
    private boolean mapaCargado = false; // control para evitar renderizar mientras carga

    public PantallaCuartoOscuro(LaberintoDelTerror juego) {
        this.juego = juego;
    }

    @Override
    public void show() {
        Gdx.graphics.setVSync(true);
        batch = new SpriteBatch();
        sr = new ShapeRenderer();
        hud = new BitmapFont();
        hud.getData().setScale(1.3f);

        texRuna = new Texture("runa_amarilla.png");
        texMeta = new Texture("meta_verde.png");
        texBoss = new Texture("enemigo/fantasma_boos.png");
        texLuz = crearTexturaLuz(156); // luz generada por código

        if (Gdx.files.internal("audios/boss_grito.mp3").exists())
            grito = Gdx.audio.newSound(Gdx.files.internal("audios/boss_grito.mp3"));

        // 🔄 Cargar mapa oscuro en segundo plano
        new Thread(() -> {
            try {
                int index = 1 + rnd.nextInt(6);
                String mapa = "niveles/cuarto_oscuro" + index + ".png";
                if (Gdx.files.internal(mapa).exists()) {
                    pix = new Pixmap(Gdx.files.internal(mapa));
                    texLaberinto = new Texture(mapa);
                } else {
                    Pixmap p = new Pixmap(80, 60, Pixmap.Format.RGBA8888);
                    p.setColor(Color.BLACK);
                    p.fill();
                    texLaberinto = new Texture(p);
                    pix = p;
                }

                generarMapaColisiones();

                // ⚙️ Generar posiciones seguras en el hilo principal
                Gdx.app.postRunnable(() -> {
                    generarPosicionesSegurasYSeparadas();
                    mapaCargado = true;
                });
            } catch (Exception e) {
                Gdx.app.log("ERROR", "Error al cargar el cuarto oscuro: " + e.getMessage());
            }
        }).start();

        Gdx.input.vibrate(200);
    }

    /** Precalcula las zonas sólidas para colisión rápida */
    private void generarMapaColisiones() {
        int w = pix.getWidth(), h = pix.getHeight();
        colisionMapa = new boolean[w][h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int rgba = pix.getPixel(x, y);
                int r = (rgba >>> 24) & 0xff;
                int g = (rgba >>> 16) & 0xff;
                int b = (rgba >>> 8) & 0xff;
                colisionMapa[x][y] = (r < 70 && g < 70 && b < 70);
            }
        }
    }

    /** Luz circular con degradado cálido */
    private Texture crearTexturaLuz(int size) {
        Pixmap p = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        float cx = size / 2f, cy = size / 2f, radio = size / 2f;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                float dx = x - cx, dy = y - cy;
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                float f = MathUtils.clamp(1f - dist / radio, 0f, 1f);
                float a = (float) Math.pow(f, 2.5f);
                p.setColor(1f, 0.95f, 0.75f, a * 0.9f);
                p.drawPixel(x, y);
            }
        }
        Texture tex = new Texture(p);
        p.dispose();
        return tex;
    }

    /** Genera posiciones seguras y separadas entre objetos */
    private void generarPosicionesSegurasYSeparadas() {
        float[] posBola = buscarLibreSeguro();
        bola = new Bola(posBola[0], posBola[1]);

        float[] posRuna;
        do { posRuna = buscarLibreSeguro(); }
        while (distancia(posRuna[0], posRuna[1], bola.getX(), bola.getY()) < 500);
        runaX = posRuna[0]; runaY = posRuna[1];

        float[] posMeta;
        do { posMeta = buscarLibreSeguro(); }
        while (distancia(posMeta[0], posMeta[1], runaX, runaY) < 400 ||
            distancia(posMeta[0], posMeta[1], bola.getX(), bola.getY()) < 400);
        metaX = posMeta[0]; metaY = posMeta[1];

        float[] posBoss;
        do { posBoss = buscarLibreSeguro(); }
        while (distancia(posBoss[0], posBoss[1], bola.getX(), bola.getY()) < 700 ||
            distancia(posBoss[0], posBoss[1], runaX, runaY) < 600);
        bossX = posBoss[0]; bossY = posBoss[1];
    }

    @Override
    public void render(float dt) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!mapaCargado) {
            // pantalla negra con texto de carga
            batch.begin();
            hud.draw(batch, "Cargando el cuarto oscuro...", Gdx.graphics.getWidth() / 2f - 120, Gdx.graphics.getHeight() / 2f);
            batch.end();
            return;
        }

        if (atrapado) {
            juego.setScreen(new PantallaScreamer(juego));
            dispose();
            return;
        }

        tiempo += dt;

        // 🎮 Movimiento del jugador
        float ax = Gdx.input.getAccelerometerX();
        float ay = Gdx.input.getAccelerometerY();
        float dx = ay * 55f * dt * 3.2f;
        float dy = -ax * 55f * dt * 3.2f;
        moverSiLibre(dx, dy);

        moverBoss(dt);

        // 📡 Luz dinámica
        float distBoss = distancia(bola.getX(), bola.getY(), bossX, bossY);
        enAlerta = distBoss < 270;
        if (enAlerta) alertaTimer += dt;
        radioActual = MathUtils.lerp(radioActual, enAlerta ? 160 : radioLuz, 3f * dt);
        float pulso = 1f + 0.06f * MathUtils.sin(tiempo * 2.5f);
        float radioPulsante = radioActual * pulso;

        // 🧿 Interacciones
        if (!runaTomada && colision(bola.getRect(), runaX, runaY, 64, 64)) {
            runaTomada = true;
            metaActiva = true;
            Gdx.input.vibrate(250);
        }

        if (metaActiva && colision(bola.getRect(), metaX, metaY, 64, 64)) {
            Gdx.input.vibrate(300);
            juego.setScreen(new PantallaLaberinto(juego));
            dispose();
            return;
        }

        if (colision(bola.getRect(), bossX, bossY, bossW, bossH)) {
            atrapado = true;
            if (grito != null) grito.play(1.0f);
            Gdx.input.vibrate(400);
        }

        // 🖼️ Render
        batch.begin();
        batch.draw(texLaberinto, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (!runaTomada) {
            float alpha = 0.8f + 0.2f * MathUtils.sin(tiempo * 4f);
            batch.setColor(1f, 1f, 0.7f, alpha);
            batch.draw(texRuna, runaX, runaY, 64, 64);
            batch.setColor(Color.WHITE);
        }

        if (metaActiva) batch.draw(texMeta, metaX, metaY, 64, 64);
        batch.draw(texBoss, bossX, bossY, bossW, bossH);

        // 🌕 Luz translúcida centrada en el jugador
        float cx = bola.getX() + bola.getW() / 2;
        float cy = bola.getY() + bola.getH() / 2;
        batch.setColor(1f, 1f, 1f, 0.8f);
        batch.draw(texLuz, cx - radioPulsante / 2, cy - radioPulsante / 2, radioPulsante, radioPulsante);
        batch.setColor(Color.WHITE);

        bola.dibujar(batch);

        hud.draw(batch, "🌑 Cuarto Oscuro", 16, Gdx.graphics.getHeight() - 16);
        batch.end();

        // Alerta roja suave
        if (enAlerta) {
            float intensidad = 0.05f + 0.03f * MathUtils.sin(alertaTimer * 2f);
            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setColor(1, 0, 0, intensidad);
            sr.circle(bossX + bossW / 2, bossY + bossH / 2, 90 + 10 * MathUtils.sin(tiempo * 3f));
            sr.end();
        }
    }

    // ================= MOVIMIENTOS =================
    private void moverBoss(float dt) {
        float dx = bola.getX() - bossX;
        float dy = bola.getY() - bossY;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist < 250) {
            persiguiendo = true;
            bossX += (dx / dist) * bossVel * dt;
            bossY += (dy / dist) * bossVel * dt;
        } else {
            persiguiendo = false;
            cambioDirTimer -= dt;
            if (cambioDirTimer <= 0) {
                bossDirX = MathUtils.random(-1f, 1f);
                bossDirY = MathUtils.random(-1f, 1f);
                cambioDirTimer = 2 + MathUtils.random(2f);
            }
            float nx = bossX + bossDirX * bossVel * 0.4f * dt;
            float ny = bossY + bossDirY * bossVel * 0.4f * dt;
            if (!colisionaConMuro(nx, ny)) {
                bossX = nx; bossY = ny;
            } else cambioDirTimer = 0;
        }
    }

    private void moverSiLibre(float dx, float dy) {
        float nx = bola.getX() + dx, ny = bola.getY() + dy;
        if (!colisionaConMuro(nx, ny)) bola.mover(dx, dy);
        bola.actualizar(Gdx.graphics.getDeltaTime());
    }

    // ================= COLISIONES RÁPIDAS =================
    private boolean colisionaConMuro(float x, float y) {
        int w = 48, h = 48;
        float sx = pix.getWidth() / (float) Gdx.graphics.getWidth();
        float sy = pix.getHeight() / (float) Gdx.graphics.getHeight();
        int[][] pts = {{2,2},{w-2,2},{2,h-2},{w-2,h-2}};
        for (int[] p : pts) {
            int px = (int)((x + p[0]) * sx);
            int py = (int)((y + p[1]) * sy);
            if (px < 0 || py < 0 || px >= pix.getWidth() || py >= pix.getHeight()) return true;
            if (colisionMapa[px][pix.getHeight() - 1 - py]) return true;
        }
        return false;
    }

    private float[] buscarLibreSeguro() {
        float sx = Gdx.graphics.getWidth() / (float) pix.getWidth();
        float sy = Gdx.graphics.getHeight() / (float) pix.getHeight();
        for (int i = 0; i < 9000; i++) {
            int x = rnd.nextInt(pix.getWidth());
            int y = rnd.nextInt(pix.getHeight());
            if (estaZonaLibre(x, y)) return new float[]{x * sx, y * sy};
        }
        return new float[]{100, 100};
    }

    private boolean estaZonaLibre(int x, int y) {
        int margen = 8;
        for (int i = -margen; i <= margen; i++) {
            for (int j = -margen; j <= margen; j++) {
                int px = MathUtils.clamp(x + i, 0, pix.getWidth() - 1);
                int py = MathUtils.clamp(y + j, 0, pix.getHeight() - 1);
                if (colisionMapa[px][py]) return false;
            }
        }
        return true;
    }

    private boolean colision(Rectangle rect, float x, float y, float w, float h) {
        return rect.overlaps(new Rectangle(x, y, w, h));
    }

    private float distancia(float x1, float y1, float x2, float y2) {
        return (float)Math.sqrt((x2 - x1)*(x2 - x1)+(y2 - y1)*(y2 - y1));
    }

    // ================= GDX DEFAULT =================
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (sr != null) sr.dispose();
        if (hud != null) hud.dispose();
        if (pix != null) pix.dispose();
        if (texLaberinto != null) texLaberinto.dispose();
        if (texRuna != null) texRuna.dispose();
        if (texMeta != null) texMeta.dispose();
        if (texBoss != null) texBoss.dispose();
        if (texLuz != null) texLuz.dispose();
        if (grito != null) grito.dispose();
    }
}
