package com.example.laberintodelterror;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

public class PantallaInstrucciones implements Screen {
    private final LaberintoDelTerror juego;
    private SpriteBatch batch;
    private BitmapFont fTitulo, fTexto;
    private float t=0;

    public PantallaInstrucciones(LaberintoDelTerror j){ this.juego=j; }

    @Override public void show() {
        batch=new SpriteBatch();
        fTitulo=new BitmapFont(); fTitulo.getData().setScale(4.5f); fTitulo.setColor(Color.RED);
        fTexto =new BitmapFont(); fTexto .getData().setScale(2.9f); fTexto .setColor(Color.WHITE);
        if(Assets.musicaFondo!=null && !Assets.musicaFondo.isPlaying()) Assets.musicaFondo.play();
    }

    @Override public void render(float delta) {
        t+=delta;
        Gdx.gl.glClearColor(0,0,0,1); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float w=Gdx.graphics.getWidth(), h=Gdx.graphics.getHeight(), cx=w/2f;

        batch.begin();
        batch.draw(Assets.fondoMenu,0,0,w,h);

        fTitulo.draw(batch,"INSTRUCCIONES", cx, h*0.72f, 0, Align.center, false);

        String texto =
            " Inclina el teléfono para mover la bola.\n\n" +
                " Evita chocar con los enemigos.\n\n" +
                " Toma la llave para desbloquear la salida.\n\n" +
                " Cruza la meta ROJA para avanzar.\n\n" +
                " Cada nivel tiene 5 subniveles. ¡Completa todos!";
        fTexto.draw(batch, texto, cx-(w*0.4f), h*0.52f, w*0.8f, Align.center, true);

        // línea parpadeante abajo-derecha
        float alpha = 0.4f + 0.6f * Math.abs((float)Math.sin(t*3));
        fTexto.setColor(1,1,1,alpha);
        fTexto.draw(batch, "Toca la pantalla para comenzar", w*0.95f, h*0.10f, 0, Align.right, false);
        fTexto.setColor(Color.WHITE);
        batch.end();

        if(Gdx.input.justTouched()) juego.setScreen(new PantallaLaberinto(juego));
    }

    @Override public void resize(int width,int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose(){ if(batch!=null) batch.dispose(); if(fTitulo!=null) fTitulo.dispose(); if(fTexto!=null) fTexto.dispose(); }
}
