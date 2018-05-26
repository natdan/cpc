package org.ah.cpc;

public class DefaultPlatformSpecific extends AbstractPlatformSpecific {

    public DefaultPlatformSpecific() {
        setConfigParam(CONFIG_IS_DESKTOP, "true");
    }

    public void initFromSystemProperties() {
        for (String paramName : KNOWN_CONFIG_PARAMS) {
            String value = System.getProperty(paramName);
            if (value != null) {
                setConfigParam(paramName, value);
            }
        }
    }
}
