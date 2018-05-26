package org.ah.cpc.desktop;

import org.ah.cpc.CPCounter;
import org.ah.cpc.DefaultPlatformSpecific;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class StartCPCDesktop {
    public static void main(final String[] args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        if (args.length > 2) {
            config.width = Integer.parseInt(args[1]);
            config.height = Integer.parseInt(args[2]);
        } else {
            config.width = 1280;
            config.height = 800;
        }

        DefaultPlatformSpecific platformSpecific = new DefaultPlatformSpecific();

        platformSpecific.initFromSystemProperties();

        new LwjglApplication(new CPCounter(platformSpecific), config);
    }
}
