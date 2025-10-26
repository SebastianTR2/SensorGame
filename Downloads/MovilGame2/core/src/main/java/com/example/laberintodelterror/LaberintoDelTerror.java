package com.example.laberintodelterror;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class LaberintoDelTerror extends Game {

    @Override
    public void create() {
        Assets.cargar(); // Cargar texturas y sonidos
        setScreen(new PantallaMenu(this)); // Mostrar el menú inicial
    }

    @Override
    public void dispose() {
        super.dispose();
        Assets.dispose();
    }
}
