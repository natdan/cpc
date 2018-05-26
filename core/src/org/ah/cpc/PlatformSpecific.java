package org.ah.cpc;

public interface PlatformSpecific {

    public static final String CONFIG_BACKLIGHT_HANDING_REQUIRED = "backlight.handling";
    public static final String CONFIG_MOUSE_ADJUST = "mouse.adjust";
    public static final String CONFIG_MOUSE_SHOW = "mouse.show";
    public static final String CONFIG_MOUSE_DEBUG = "mouse.debug";
    public static final String CONFIG_IS_DESKTOP = "desktop";
    public static final String CONFIG_IS_DEBUG = "debug";
    public static final String CONFIG_UI_INPUT_DEBUG = "ui.input.debug";
    public static final String CONFIG_DISPLAY_TIMEOUT = "display.timeout";


    public static final String[] KNOWN_CONFIG_PARAMS = new String[] {
        CONFIG_BACKLIGHT_HANDING_REQUIRED,
        CONFIG_DISPLAY_TIMEOUT,
        CONFIG_MOUSE_ADJUST,
        CONFIG_MOUSE_SHOW,
        CONFIG_MOUSE_DEBUG,
        CONFIG_IS_DESKTOP,
        CONFIG_IS_DEBUG,
        CONFIG_UI_INPUT_DEBUG
    };

    String getConfigParameter(String name);

    int getConfigParameterAsInt(String name);

    boolean getConfigParameterAsBool(String name);
}
