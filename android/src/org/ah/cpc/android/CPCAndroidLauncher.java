package org.ah.cpc.android;

import org.ah.cpc.CPCounter;
import org.ah.cpc.DefaultPlatformSpecific;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import android.os.Bundle;

public class CPCAndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.hideStatusBar = true;
        config.useImmersiveMode = true;
        initialize(new CPCounter(new DefaultPlatformSpecific() {
        }), config);
    }
}
