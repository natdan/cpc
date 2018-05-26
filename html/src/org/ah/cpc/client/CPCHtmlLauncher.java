package org.ah.cpc.client;

import org.ah.cpc.CPCounter;
import org.ah.cpc.DefaultPlatformSpecific;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

public class CPCHtmlLauncher extends GwtApplication {

    @Override
    public GwtApplicationConfiguration getConfig() {
        return new GwtApplicationConfiguration(480, 320);
    }

    @Override
    public ApplicationListener getApplicationListener() {
        return new CPCounter(new DefaultPlatformSpecific());
    }

    @Override
    public ApplicationListener createApplicationListener() {
        return null;
    }
}