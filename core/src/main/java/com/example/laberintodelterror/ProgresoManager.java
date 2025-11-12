package com.example.laberintodelterror;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class ProgresoManager {
    private int nivel;
    private int subnivel;
    private final int MAX_NIVELES = 5;
    private final int MAX_SUBNIVELES = 5;
    private final Preferences prefs;

    public ProgresoManager() {
        prefs = Gdx.app.getPreferences("ProgresoJuego");

        //  Carga el progreso guardado (si existe)
        nivel = prefs.getInteger("nivel", 1);
        subnivel = prefs.getInteger("subnivel", 1);
    }

    //  Obtiene el nivel actual
    public int getNivel() { return nivel; }

    //  Obtiene el subnivel actual
    public int getSubnivel() { return subnivel; }

    // Establece manualmente el nivel actual
    public void setNivel(int n) {
        this.nivel = Math.min(n, MAX_NIVELES);
        prefs.putInteger("nivel", nivel);
        prefs.flush();
    }

    //  Establece manualmente el subnivel actual
    public void setSubnivel(int s) {
        this.subnivel = Math.min(s, MAX_SUBNIVELES);
        prefs.putInteger("subnivel", subnivel);
        prefs.flush();
    }

    //  Avanza al siguiente subnivel dentro del mismo nivel
    public void avanzar() {
        subnivel++;
        if (subnivel > MAX_SUBNIVELES) {
            subnivel = 1;
            nivel++;
            if (nivel > MAX_NIVELES) {
                nivel = MAX_NIVELES;
                subnivel = MAX_SUBNIVELES;
            }
        }
        guardar();
    }

    //  Avanza al siguiente nivel (reinicia subnivel)
    public void avanzarNivel() {
        nivel++;
        if (nivel > MAX_NIVELES) nivel = MAX_NIVELES;
        subnivel = 1;
        guardar();
    }

    //  Retrocede un subnivel (y si estás en el primero, retrocede nivel)
    public void retroceder() {
        subnivel--;
        if (subnivel < 1) {
            nivel--;
            if (nivel < 1) {
                nivel = 1;
                subnivel = 1;
            } else {
                subnivel = MAX_SUBNIVELES;
            }
        }
        guardar();
    }

    //  Reinicia todo el progreso
    public void reiniciar() {
        nivel = 1;
        subnivel = 1;
        prefs.putInteger("nivelDesbloqueado", 1);
        guardar();
    }

    //  Guarda el nivel actual y subnivel actual
    private void guardar() {
        prefs.putInteger("nivel", nivel);
        prefs.putInteger("subnivel", subnivel);
        prefs.flush();
    }

    //  Desbloquea un nuevo nivel
    public void desbloquearSiguienteNivel(int n) {
        int desbloqueado = prefs.getInteger("nivelDesbloqueado", 1);
        if (n > desbloqueado) {
            prefs.putInteger("nivelDesbloqueado", n);
            prefs.flush();
        }
    }

    //  Obtiene el nivel máximo desbloqueado
    public int getNivelDesbloqueado() {
        return prefs.getInteger("nivelDesbloqueado", 1);
    }
}
