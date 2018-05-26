package org.ah.cpc;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPlatformSpecific implements PlatformSpecific {

    private Map<String, String> configParameters = new HashMap<String, String>();

    public AbstractPlatformSpecific() {
    }

    public void setConfigParam(String name, String value) {
        configParameters.put(name, value);
    }

    protected String tempToString(float temperature) {
        int tmp = (int)temperature * 2;
        return Float.toString(tmp / 2f) + "ºC";
    }

    @Override
    public String getConfigParameter(String name) {
        return configParameters.get(name);
    }

    @Override
    public int getConfigParameterAsInt(String name) {
        String value = getConfigParameter(name);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
            }
        }
        return -1;
    }

    @Override
    public boolean getConfigParameterAsBool(String name) {
        String value = getConfigParameter(name);
        return "true".equalsIgnoreCase(value);
    }
}
