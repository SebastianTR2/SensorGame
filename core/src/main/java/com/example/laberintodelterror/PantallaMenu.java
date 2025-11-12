package com.example.laberintodelterror;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PantallaMenu implements Screen {
    private final LaberintoDelTerror juego;
    private SpriteBatch batch;

    private float bx,by,bw=300,bh=100;

    public PantallaMenu(LaberintoDelTerror juego){ this.juego=juego; }

    @Override public void show() {
        batch=new SpriteBatch();
        bx=(Gdx.graphics.getWidth()-bw)/2f;
        by=Gdx.graphics.getHeight()/3f;
        if(Assets.musicaFondo!=null && !Assets.musicaFondo.isPlaying()) Assets.musicaFondo.play();
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(Assets.fondoMenu,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        batch.draw(Assets.botonJugar,bx,by,bw,bh);
        batch.end();

        if(Gdx.input.justTouched()){
            float x=Gdx.input.getX();
            float y=Gdx.graphics.getHeight()-Gdx.input.getY();
            if(x>bx && x<bx+bw && y>by && y<by+bh) juego.setScreen(new PantallaInstrucciones(juego)); //PantallaInstrucciones
        }
    }

    @Override public void resize(int width,int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose(){ if(batch!=null) batch.dispose(); }
}
