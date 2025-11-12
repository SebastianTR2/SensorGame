package com.example.laberintodelterror;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PantallaLaberinto implements Screen {
    private final LaberintoDelTerror juego;
    private SpriteBatch batch;
    private ShapeRenderer sr;
    private BitmapFont hud;

    private NivelManager nivelMgr;
    private Pixmap pix;
    private Texture texNivel;

    private Bola bola;
    private final List<Enemigo> enemigos = new ArrayList<>();
    private final Llave llave = new Llave();
    private final Puerta puerta = new Puerta();

    private final Random rnd = new Random();
    private float vel = 55f;
    private float tiempoEnemigos = 0;
    private float tiempoNivel = 0;

    // --- Texturas y sprites para las metas ---
    private Texture texMetaRoja, texMetaVerde, texMetaAzul, texLogo;
    private Sprite metaRoja, metaVerde, metaAzul, logoSprite;
    private float tiempoBrillo = 0;

    // Solo guarda el último punto al retroceder
    private static float ultimoSpawnX = 0;
    private static float ultimoSpawnY = 0;

    public PantallaLaberinto(LaberintoDelTerror juego) {
        this.juego = juego;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        sr = new ShapeRenderer();
        hud = new BitmapFont();
        hud.getData().setScale(1.4f);

        int n = juego.progreso.getNivel();
        int s = juego.progreso.getSubnivel();

        nivelMgr = new NivelManager(n, s);
        pix = nivelMgr.getPixmap();
        texNivel = nivelMgr.getTexture();

        // 📍 Posición inicial de la bola
        if (s == 1) {
            switch (n) {
                case 1: bola = new Bola(1100, 50); break;
                case 2: bola = new Bola(1100, 50); break;
                case 3: bola = new Bola(1200, 50); break;
                case 4: bola = new Bola(1200, 50); break;
                case 5: bola = new Bola(1200, 50); break;
            }
        } else {
            // Solo si se retrocedió, reaparece donde estaba la meta roja
            if (ultimoSpawnX != 0 || ultimoSpawnY != 0)
                bola = new Bola(ultimoSpawnX, ultimoSpawnY);
            else
                bola = new Bola(1200, 50);
        }

        // Llave y puerta
        float[] meta = buscarMetaRoja();
        puerta.colocar(meta[0] - 20, meta[1] - 20);


        float[] libre = buscarLibreAleatorio();
        // 🔑 Configurar tipo de llave y puerta por nivel/subnivel
        if ((n == 1 && s == 1) || (n == 2 && s == 5) || (n == 3 && s == 5) || (n == 5 && s == 4)) {
            llave.setColor("morada"); // 🔮 llave especial
        } else {
            llave.setColor("amarilla");
        }
        llave.colocar(libre[0], libre[1]);
        // 💡 Mostrar puerta sólo si es un nivel donde existe
        boolean puertaActiva = (n != 5 || s != 5);
        if (puertaActiva) {
            //float[] meta = buscarMetaRoja();
            puerta.colocar(meta[0] - 20, meta[1] - 20);
        } else {
            puerta.abierta = true; // en niveles finales no hay puerta
        }


        generarEnemigos(n, s);

        // Cargar texturas de metas
        texMetaRoja = new Texture("meta_roja.png");
        texMetaVerde = new Texture("meta_verde.png");
        texMetaAzul = new Texture("meta_azul.png");

        metaRoja = new Sprite(texMetaRoja);
        metaVerde = new Sprite(texMetaVerde);
        metaAzul = new Sprite(texMetaAzul);

        // Tamaños
        metaRoja.setSize(210, 80);
        metaVerde.setSize(210, 80);
        metaAzul.setSize(210, 80);

        metaRoja.setOriginCenter();
        metaVerde.setOriginCenter();
        metaAzul.setOriginCenter();

        // Fondo con logo Univalle
        texLogo = new Texture("Logo_univalle.png");
        logoSprite = new Sprite(texLogo);
        logoSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // ocupa toda la pantalla
        logoSprite.setAlpha(0.25f); // transparencia para que no moleste al gameplay

        // 📍 Posiciones por nivel y subnivel
        switch (n) {
            case 1:
                switch (s) {
                    case 1: metaRoja.setPosition(1100, 1000); break;
                    case 2: metaRoja.setPosition(940, 1000); metaAzul.setPosition(1250, -20); break;
                    case 3: metaRoja.setPosition(1100, 1000); metaAzul.setPosition(1100, -20); break;
                    case 4: metaRoja.setPosition(950, 1000); metaAzul.setPosition(1250, -20); break;
                    case 5: metaVerde.setPosition(1100, 1000); metaAzul.setPosition(1100, -20); break;
                }
                break;
            case 2:
                switch (s) {
                    case 1: metaRoja.setPosition(950, 1020); break;
                    case 2: metaRoja.setPosition(1100, 1020); metaAzul.setPosition(1100, -20); break;
                    case 3: metaRoja.setPosition(970, 1020); metaAzul.setPosition(1220, -20); break;
                    case 4: metaRoja.setPosition(1100, 1020); metaAzul.setPosition(1100, -20); break;
                    case 5: metaVerde.setPosition(970, 1060); metaAzul.setPosition(1220, -20); break;
                }
                break;
            case 3:
                switch (s) {
                    case 1: metaRoja.setPosition(980, 1020); break;
                    case 2: metaRoja.setPosition(1090, 1020); metaAzul.setPosition(1100, -20); break;
                    case 3: metaRoja.setPosition(970, 1020); metaAzul.setPosition(1150, -20); break;
                    case 4: metaRoja.setPosition(1090, 1020); metaAzul.setPosition(1120, -20); break;
                    case 5: metaVerde.setPosition(990, 1020); metaAzul.setPosition(1200, -20); break;
                }
                break;
            case 4:
                switch (s) {
                    case 1: metaRoja.setPosition(990, 1030); break;
                    case 2: metaRoja.setPosition(1100, 1020); metaAzul.setPosition(1100, -20); break;
                    case 3: metaRoja.setPosition(990, 1030); metaAzul.setPosition(1150, -20); break;
                    case 4: metaRoja.setPosition(1060, 1020); metaAzul.setPosition(1130, -20); break;
                    case 5: metaVerde.setPosition(990, 1030); metaAzul.setPosition(1140, -20); break;
                }
                break;
            case 5:
                switch (s) {
                    case 1: metaRoja.setPosition(1060, 1020); break;
                    case 2: metaRoja.setPosition(980, 1030); metaAzul.setPosition(1200, -20); break;
                    case 3: metaRoja.setPosition(1060, 1030); metaAzul.setPosition(1050, -30); break;
                    case 4: metaRoja.setPosition(1000, 1020); metaAzul.setPosition(1170, -30); break;
                    case 5: metaVerde.setPosition(1050, 1030); metaAzul.setPosition(1210, -30); break;
                }
                break;
        }

        // Música
        if (Assets.musicaFondo != null && !Assets.musicaFondo.isPlaying()) {
            Assets.musicaFondo.setLooping(true);
            Assets.musicaFondo.play();
        }
    }

    private void generarEnemigos(int nivel, int subnivel) {
        enemigos.clear();
        int cantidad = Math.min(2 + nivel, 6);
        for (int i = 0; i < cantidad; i++) {
            String tipo;
            if (nivel < 2) tipo = "errante";
            else if (nivel == 2) tipo = (i % 2 == 0 ? "guardia" : "errante");
            else if (nivel == 3) tipo = (i % 3 == 0 ? "cazador" : "guardia");
            else tipo = (i % 4 == 0 ? "fantasma" : "cazador");

            float[] pos = buscarLibreAleatorio();
            enemigos.add(new Enemigo(tipo, pos[0], pos[1], 0.5f + nivel * 0.1f));
        }
    }

    @Override
    public void render(float dt) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        tiempoBrillo += dt;
        tiempoNivel += dt;

        // Movimiento por acelerómetro
        float ax = Gdx.input.getAccelerometerX();
        float ay = Gdx.input.getAccelerometerY();
        float dx = ay * vel * dt * 3.2f;
        float dy = -ax * vel * dt * 3.2f;
        moverSiLibre(dx / 2, dy / 2);
        moverSiLibre(dx / 2, dy / 2);

        // Enemigos
        tiempoEnemigos += dt;
        if (tiempoEnemigos > Math.max(2.5f, 7f - juego.progreso.getNivel())) {
            activarEnemigo();
            tiempoEnemigos = 0;
        }

        for (Enemigo e : enemigos) {
            e.mover(dt, bola.getX(), bola.getY());
            if (e.getRect().overlaps(bola.getRect())) {
                juego.setScreen(new PantallaScreamer(juego));
                dispose();
                return;
            }
        }

        // Llave
        if (llave.intentarTomar(bola.getX(), bola.getY(), bola.getW(), bola.getH()))
            puerta.abrir();

        // Metas
        int s = juego.progreso.getSubnivel();
        if (puerta.abierta) {
            Rectangle rectBola = new Rectangle(bola.getX(), bola.getY(), bola.getW(), bola.getH());
            Rectangle rectRoja = new Rectangle(metaRoja.getX(), metaRoja.getY(), metaRoja.getWidth(), metaRoja.getHeight());
            Rectangle rectVerde = new Rectangle(metaVerde.getX(), metaVerde.getY(), metaVerde.getWidth(), metaVerde.getHeight());
            Rectangle rectAzul = new Rectangle(metaAzul.getX(), metaAzul.getY(), metaAzul.getWidth(), metaAzul.getHeight());

            // 🟥 Avanzar subnivel → sin guardar posición
            if (s < 5 && rectBola.overlaps(rectRoja)) {
                ultimoSpawnX = 0;
                ultimoSpawnY = 0;
                juego.progreso.avanzar();
                reiniciarPantalla();
                return;
            }

            // 🟦 Retroceder subnivel → guarda la posición actual de la meta roja
            if (s > 1 && s < 5 && rectBola.overlaps(rectAzul)) {
                ultimoSpawnX = metaRoja.getX();
                ultimoSpawnY = metaRoja.getY();
                juego.progreso.retroceder();
                reiniciarPantalla();
                return;
            }


            // Solo se activa si la puerta está abierta y la llave morada fue tomada
            if (puerta.abierta && "morada".equalsIgnoreCase(llave.getColor()) && llave.tomada) {
                //Rectangle rectBola = new Rectangle(bola.getX(), bola.getY(), bola.getW(), bola.getH());
                Rectangle rectPuerta = new Rectangle(puerta.getX(), puerta.getY(), puerta.getW(), puerta.getH());

                if (rectBola.overlaps(rectPuerta)) {
                    Gdx.app.log("EVENTO", "Entrando al Cuarto Oscuro desde nivel " + juego.progreso.getNivel());
                    Gdx.input.vibrate(300);
                    juego.setScreen(new PantallaCuartoOscuro(juego));
                    dispose();
                    return;
                }
            }




            // 🟩 Terminar nivel
            if (s == 5 && rectBola.overlaps(rectVerde)) {
                if (Assets.musicaFondo != null && Assets.musicaFondo.isPlaying())
                    Assets.musicaFondo.stop();

                int nivelActual = juego.progreso.getNivel();

                // 🔮 ENTRADA AL CUARTO OSCURO
                // Si el jugador está en el nivel 5, subnivel 4, y tiene la llave morada,
                // se abre automáticamente la pantalla secreta del Cuarto Oscuro.
                if (nivelActual == 5 && juego.progreso.getSubnivel() == 4 && llave.tomada) {
                    juego.setScreen(new PantallaCuartoOscuro(juego));
                    dispose();
                    return;
                }

                // 🧩 Si ya terminó el último nivel → PantallaFinal
                if (nivelActual == 5) {
                    juego.setScreen(new PantallaFinal(juego));
                } else {
                    juego.progreso.desbloquearSiguienteNivel(nivelActual + 1);
                    juego.setScreen(new PantallaVictoria(juego, nivelActual, tiempoNivel));
                }
                dispose();
                return;
            }

        }

        // --- Dibujo ---
        batch.begin();
        batch.draw(texNivel, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        logoSprite.draw(batch);
        llave.dibujar(batch);
        puerta.dibujar(batch);

        float alpha = puerta.abierta ? 0.5f + 0.5f * (float) Math.sin(tiempoBrillo * 3) : 0.3f;
        metaRoja.setAlpha(alpha);
        metaVerde.setAlpha(alpha);
        metaAzul.setAlpha(alpha);

        int sNivel = juego.progreso.getSubnivel();
        if (sNivel < 5) metaRoja.draw(batch);
        if (sNivel == 5) metaVerde.draw(batch);
        if (sNivel >= 2) metaAzul.draw(batch);

        for (Enemigo e : enemigos) e.dibujar(batch);
        bola.dibujar(batch);

        // HUD
        hud.draw(batch,
            "Nivel " + juego.progreso.getNivel() +
                " - Subnivel " + juego.progreso.getSubnivel() +
                (llave.tomada ? "  [Llave ✓]" : "  [Llave ✗]"),
            16, Gdx.graphics.getHeight() - 16);

        hud.draw(batch, String.format("⏱ %.1f s", tiempoNivel),
            Gdx.graphics.getWidth() - 120, Gdx.graphics.getHeight() - 16);

        batch.end();
    }

    private void reiniciarPantalla() {
        if (Assets.musicaFondo != null && Assets.musicaFondo.isPlaying())
            Assets.musicaFondo.stop();
        juego.setScreen(new PantallaLaberinto(juego));
        dispose();
    }

    private void moverSiLibre(float dx, float dy) {
        float nx = bola.getX() + dx, ny = bola.getY() + dy;
        if (!colisionaConMuro(nx, ny)) bola.mover(dx, dy);
        bola.actualizar(Gdx.graphics.getDeltaTime());
    }

    private boolean colisionaConMuro(float x, float y) {
        int w = (int) bola.getW(), h = (int) bola.getH();
        float sx = pix.getWidth() / (float) Gdx.graphics.getWidth();
        float sy = pix.getHeight() / (float) Gdx.graphics.getHeight();
        int[][] pts = {{2,2},{w-2,2},{2,h-2},{w-2,h-2},{w/2,2},{w/2,h-2},{2,h/2},{w-2,h/2}};
        for (int[] p : pts) {
            int px = (int)((x+p[0])*sx), py=(int)((y+p[1])*sy);
            if(px<0||py<0||px>=pix.getWidth()||py>=pix.getHeight()) return true;
            int rgba = pix.getPixel(px,pix.getHeight()-1-py);
            int r=(rgba>>>24)&0xff,g=(rgba>>>16)&0xff,b=(rgba>>>8)&0xff;
            if(r<100&&g<100&&b<100) return true;
        }
        return false;
    }

    private float[] buscarLibreAleatorio() {
        float sx = Gdx.graphics.getWidth()/(float)pix.getWidth();
        float sy = Gdx.graphics.getHeight()/(float)pix.getHeight();
        for(int i=0;i<5000;i++){
            int x=rnd.nextInt(pix.getWidth());
            int y=rnd.nextInt(pix.getHeight());
            int rgba=pix.getPixel(x,pix.getHeight()-1-y);
            int r=(rgba>>>24)&0xff,g=(rgba>>>16)&0xff,b=(rgba>>>8)&0xff;
            if(r>200&&g>200&&b>200) return new float[]{x*sx,y*sy};
        }
        return new float[]{80,80};
    }

    private float[] buscarMetaRoja() {
        float sx = Gdx.graphics.getWidth()/(float)pix.getWidth();
        float sy = Gdx.graphics.getHeight()/(float)pix.getHeight();
        for(int y=0;y<pix.getHeight();y+=3){
            for(int x=0;x<pix.getWidth();x+=3){
                int rgba=pix.getPixel(x,pix.getHeight()-1-y);
                int r=(rgba>>>24)&0xff,g=(rgba>>>16)&0xff,b=(rgba>>>8)&0xff;
                if(r>200&&g<80&&b<80) return new float[]{x*sx,y*sy};
            }
        }
        return new float[]{Gdx.graphics.getWidth()-80,Gdx.graphics.getHeight()-80};
    }

    private void activarEnemigo() {
        for (Enemigo e : enemigos) {
            float ang = rnd.nextFloat()*MathUtils.PI2, dist = 250+rnd.nextFloat()*150;
            float x = bola.getX()+MathUtils.cos(ang)*dist;
            float y = bola.getY()+MathUtils.sin(ang)*dist;
            x=MathUtils.clamp(x,40,Gdx.graphics.getWidth()-40);
            y=MathUtils.clamp(y,40,Gdx.graphics.getHeight()-40);
            if(!colisionaConMuro(x,y)){ e.x=x; e.y=y; }
        }
    }

    @Override public void resize(int width,int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if(batch!=null) batch.dispose();
        if(sr!=null) sr.dispose();
        if(hud!=null) hud.dispose();
        if(nivelMgr!=null) nivelMgr.dispose();
        if(texMetaRoja!=null) texMetaRoja.dispose();
        if(texMetaVerde!=null) texMetaVerde.dispose();
        if(texMetaAzul!=null) texMetaAzul.dispose();
        for(Enemigo e:enemigos) e.dispose();
    }
}
