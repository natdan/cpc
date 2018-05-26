package org.ah.cpc.desktop;

import org.ah.cpc.DefaultPlatformSpecific;
import org.ah.cpc.PlatformSpecific;

public final class RPIPlatformSpecificImpl extends DefaultPlatformSpecific {

    public RPIPlatformSpecificImpl(boolean adjustMouse) {
        super();

        setConfigParam(PlatformSpecific.CONFIG_MOUSE_ADJUST, Boolean.toString(adjustMouse));
        setConfigParam(PlatformSpecific.CONFIG_MOUSE_SHOW, Boolean.toString(adjustMouse));
        setConfigParam(PlatformSpecific.CONFIG_MOUSE_DEBUG, "false");
        setConfigParam(PlatformSpecific.CONFIG_IS_DESKTOP, "false");
    }
}