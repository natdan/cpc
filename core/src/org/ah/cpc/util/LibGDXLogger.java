package org.ah.cpc.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

public class LibGDXLogger {

    public static enum Level {
        TRACE(1), DEBUG(2), INFO(3), WARNING(4), ERROR(5);

        private int level;

        private Level(int level) {
            this.level = level;
        }

        public boolean isEnabled(Level level) {
            return level.level >= this.level;
        }
    }


    private Level level = Level.DEBUG;
    private String tag;

    public LibGDXLogger(String tag) {
        setTag(tag);
    }

    public void setLevel(Level level) {
        this.level = level;
        if (level == Level.TRACE) {
            Gdx.app.setLogLevel(Application.LOG_DEBUG);
        } else if (level == Level.DEBUG) {
            Gdx.app.setLogLevel(Application.LOG_DEBUG);
        } else if (level == Level.WARNING) {
            Gdx.app.setLogLevel(Application.LOG_INFO);
        } else if (level == Level.INFO) {
            Gdx.app.setLogLevel(Application.LOG_INFO);
        } else if (level == Level.ERROR) {
            Gdx.app.setLogLevel(Application.LOG_ERROR);
        }
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }

    public boolean isTraceEnabled() {
        return level.isEnabled(Level.TRACE);
    }

    public void trace(String msg) {
        if (isTraceEnabled()) {
            Gdx.app.debug(getTag(), msg);
        }
    }

    public void trace(String msg, Throwable t) {
        if (isTraceEnabled()) {
            Gdx.app.debug(getTag(), msg);
            Gdx.app.debug(getTag(), exceptionToString(t));
        }
    }

    public boolean isDebugEnabled() {
        return level.isEnabled(Level.DEBUG);
    }

    public void debug(String msg) {
        if (isDebugEnabled()) {
            Gdx.app.debug(getTag(), msg);
        }
    }

    public void debug(String msg, Throwable t) {
        if (isDebugEnabled()) {
            Gdx.app.debug(getTag(), msg);
            Gdx.app.debug(getTag(), exceptionToString(t));
        }
    }

    public boolean isInfoEnabled() {
        return level.isEnabled(Level.INFO);
    }

    public void info(String msg) {
        if (isInfoEnabled()) {
            Gdx.app.log(getTag(), msg);
        }
    }

    public void info(String msg, Throwable t) {
        if (isInfoEnabled()) {
            Gdx.app.log(getTag(), msg);
            Gdx.app.log(getTag(), exceptionToString(t));
        }
    }

    public boolean isWarnEnabled() {
        return level.isEnabled(Level.WARNING);
    }

    public void warn(String msg) {
        if (isWarnEnabled()) {
            Gdx.app.log(getTag(), msg);
        }
    }

    public void warn(String msg, Throwable t) {
        if (isWarnEnabled()) {
            Gdx.app.log(getTag(), msg);
            Gdx.app.log(getTag(), exceptionToString(t));
        }
    }

    public boolean isErrorEnabled() {
        return level.isEnabled(Level.ERROR);
    }

    public void error(String msg) {
        if (isErrorEnabled()) {
            if (Gdx.app == null) {
                System.err.println(getTag() + ": " + msg);
            } else {
                Gdx.app.error(getTag(), msg);
            }
        }
    }

    public void error(String msg, Throwable t) {
        if (isErrorEnabled()) {
            Gdx.app.error(getTag(), msg);
            Gdx.app.error(getTag(), exceptionToString(t));
        }
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    private String exceptionToString(Throwable t) {
        StringWriter w = new StringWriter();
        t.printStackTrace(new PrintWriter(w));
        return w.toString();
    }
}
