package com.example.laberintodelterror;

import com.badlogic.gdx.Game;

public class LaberintoDelTerror extends Game {
    public ProgresoManager progreso;

    @Override public void create() {
        Assets.cargar();
        progreso = new ProgresoManager(); // inicia en (1,1) si no hay datos

        //  SALTA DIRECTO A UN NIVEL/SUBNIVEL PARA PROBAR
        //progreso.setNivel(1);
        //progreso.setSubnivel(1);

        setScreen(new PantallaMenu(this));
    }

    @Override public void dispose() {
        super.dispose();
        Assets.dispose();
    }
}
