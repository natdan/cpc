package org.ah.cpc.desktop;

import org.ah.cpc.CPCounter;
import org.ah.cpc.PlatformSpecific;

import com.badlogic.gdx.backends.jogamp.JoglNewtApplication;
import com.badlogic.gdx.backends.jogamp.JoglNewtApplicationConfiguration;

public class StartCPCDesktopFrontendJOGL {

    static boolean initialised = false;

    public static void main(String[] arg) {

        System.out.println("---------------------------------------------------------------");

        final String osName = System.getProperty("os.name");
        final String osArch = System.getProperty("os.arch");
        final String osVersion = System.getProperty("os.version");
        final String xDisplayNum = System.getenv("DISPLAY");

        final boolean isLinux = "Linux".equals(osName);
        final boolean isArm = "arm".equals(osArch);
        final boolean noXwindows = xDisplayNum == null;

        final boolean adjustMouse = isLinux && isArm && noXwindows;

        final RPIPlatformSpecificImpl platformSpecific = new RPIPlatformSpecificImpl(adjustMouse);

        platformSpecific.initFromSystemProperties();

        System.out.println("OS:     '"+ osName + "'");
        System.out.println("Arch:   '"+ osArch + "'");
        System.out.println("Ver:    '"+ osVersion + "'");
        System.out.println("DISPLAY: " + xDisplayNum);
        System.out.println("--------------------------------------------------------------");
        System.out.println("IsLinux: " + isLinux);
        System.out.println("IsArm:   " + isArm);
        System.out.println("No X:    " + noXwindows);
        System.out.println("Adjust mouse is " + adjustMouse);
        System.out.println("--------------------------------------------------------------");
        for (String paramName : PlatformSpecific.KNOWN_CONFIG_PARAMS) {
            System.out.println(paramName + " = " + platformSpecific.getConfigParameter(paramName));
        }
        System.out.println("--------------------------------------------------------------");

        JoglNewtApplicationConfiguration config = new JoglNewtApplicationConfiguration();
        config.width = 1280;
        config.height = 800;
        config.x = 0;
        config.y = 0;

        new JoglNewtApplication(new CPCounter(platformSpecific), config);

        initialised = true;
    }
}
