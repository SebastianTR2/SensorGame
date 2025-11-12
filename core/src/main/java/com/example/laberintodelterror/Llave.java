package com.example.laberintodelterror;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Llave {
    private float x, y;
    private float w = 80, h = 80;
    public boolean tomada = false;

    private Texture tex;
    private float tiempoBrillo = 0f;
    private String color = "normal";

    public Llave() {
        tex = Assets.llaveTex;
    }

    public void setColor(String color) {
        this.color = color.toLowerCase();
        if (color.equalsIgnoreCase("morada")) {
            tex = Assets.llaveMoradaTex != null ? Assets.llaveMoradaTex : Assets.llaveTex;
        } else {
            tex = Assets.llaveTex;
        }
    }

    public String getColor() {
        return color;
    }

    public void colocar(float x, float y) {
        this.x = x;
        this.y = y;
        this.tomada = false;
    }

    public boolean intentarTomar(float bx, float by, float bw, float bh) {
        if (tomada) return false;
        Rectangle rectBola = new Rectangle(bx, by, bw, bh);
        Rectangle rectLlave = new Rectangle(x, y, w, h);
        if (rectBola.overlaps(rectLlave)) {
            tomada = true;
            Gdx.input.vibrate(200);
            return true;
        }
        return false;
    }

    public void dibujar(SpriteBatch batch) {
        if (!tomada) {
            if (color.equalsIgnoreCase("morada")) {
                tiempoBrillo += Gdx.graphics.getDeltaTime();
                float alpha = 0.7f + 0.3f * (float) Math.sin(tiempoBrillo * 3);
                batch.setColor(1f, 1f, 1f, alpha);
            }
            batch.draw(tex, x - w / 2, y - h / 2, w, h);
            batch.setColor(1f, 1f, 1f, 1f);
        }
    }
}
