package com.example.laberintodelterror;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class NivelManager {
    private Texture imagenNivel;
    private Pixmap pixmapNivel;
    private int startX, startY;

    public NivelManager(int nivel) {
        String nombreArchivo = "nivel" + nivel + ".png";

        if (!Gdx.files.internal(nombreArchivo).exists()) {
            Gdx.app.error("NivelManager", "Archivo no encontrado: " + nombreArchivo);
            nombreArchivo = "nivel1.png";
        }

        imagenNivel = new Texture(nombreArchivo);
        pixmapNivel = new Pixmap(Gdx.files.internal(nombreArchivo));

        // Posiciones iniciales más conservadoras
        switch (nivel) {
            case 1: startX = 1000; startY = 100; break;
            case 2: startX = 1200; startY = 80; break;
            case 3: startX = 1100; startY = 60; break;
            case 4: startX = 1150; startY = 70; break;
            case 5: startX = 1100; startY = 50; break;
            case 6: startX = 1100; startY = 50; break;
            default: startX = 100; startY = 100; break;
        }

        Gdx.app.log("NivelManager", "Nivel " + nivel + " - Start: (" + startX + ", " + startY + ")");
    }

    public Texture getImagenNivel() { return imagenNivel; }
    public Pixmap getPixmapNivel() { return pixmapNivel; }
    public int getStartX() { return startX; }
    public int getStartY() { return startY; }

    public void dispose() {
        if (imagenNivel != null) imagenNivel.dispose();
        if (pixmapNivel != null) pixmapNivel.dispose();
    }
}
