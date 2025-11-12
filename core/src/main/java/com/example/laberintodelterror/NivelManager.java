package com.example.laberintodelterror;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class NivelManager {
    private final int nivel, subnivel;
    private Texture tex;
    private Pixmap pixmap;

    public NivelManager(int nivel, int subnivel) {
        this.nivel = nivel; this.subnivel = subnivel;
        String ruta = getRutaNivel(nivel, subnivel);
        if (!Gdx.files.internal(ruta).exists()) {
            Gdx.app.error("NivelManager", "No existe: " + ruta + " (usa nivel1_sub1 por defecto)");
            ruta = getRutaNivel(1,1);
        }
        tex = new Texture(ruta);
        pixmap = new Pixmap(Gdx.files.internal(ruta));
    }

    public static String getRutaNivel(int n, int s){
        return "niveles/nivel" + n + "_sub" + s + ".png";
    }

    public Texture getTexture(){ return tex; }
    public Pixmap getPixmap(){ return pixmap; }

    public void dispose() { if(tex!=null) tex.dispose(); if(pixmap!=null) pixmap.dispose(); }
}
