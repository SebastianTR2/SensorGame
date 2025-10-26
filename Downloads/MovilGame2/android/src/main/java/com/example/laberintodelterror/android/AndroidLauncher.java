package com.example.laberintodelterror.android;

import android.os.Bundle;
import android.view.WindowManager;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.example.laberintodelterror.LaberintoDelTerror;

public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useAccelerometer = true;   // IMPORTANTE
        cfg.useCompass = false;
        cfg.useImmersiveMode = true;

        initialize(new LaberintoDelTerror(), cfg);
    }
}
