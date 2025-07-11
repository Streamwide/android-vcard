/*
 *
 * 	StreamWIDE (Team on The Run)
 *
 * @createdBy  AndroidTeam on Thu, 16 May 2024 09:42:23 +0100
 * @copyright  Copyright (c) 2024 StreamWIDE UK Ltd (Team on the Run)
 * @email      support@teamontherun.com
 *
 * 	© Copyright 2024 StreamWIDE UK Ltd (Team on the Run). StreamWIDE is the copyright holder
 * 	of all code contained in this file. Do not redistribute or
 *  	re-use without permission.
 *
 * @lastModifiedOn Thu, 2 May 2024 20:52:37 +0100
 */

package main.java.com.streamwide.smartms.lib.vcard.logger;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * SW camera library logger.
 * You can provide your own logger delegate implementation, to be able to log in
 * a different way.
 * By default the log level is set to DEBUG when the build type is debug, and
 * OFF in release.
 * The default logger implementation logs in Android's LogCat.
 */
public class Logger {

    public enum LogLevel {
                          DEBUG, INFO, ERROR, OFF
    }

    public interface LoggerDelegate {

        void error(@Nullable String tag, @NonNull String message);

        void error(@Nullable String tag, @NonNull String message, @Nullable Throwable exception);

        void debug(@Nullable String tag, @NonNull String message);

        void info(@Nullable String tag, @NonNull String message);
    }

    private LogLevel mLogLevel = LogLevel.DEBUG;

    private LoggerDelegate mDelegate = null;

    Logger()
    {
    }

    private static class SingletonHolder {

        private static final Logger instance = new Logger();

        static Logger getInstance() {
            return instance;
        }
    }

    public static void setLoggerDelegate(@Nullable LoggerDelegate delegate)
    {
        if (delegate == null)
            throw new IllegalArgumentException("delegate MUST not be null!");

        synchronized (Logger.class) {
            SingletonHolder.getInstance().mDelegate = delegate;
        }
    }

    public static void setLogLevel(@NonNull LogLevel level)
    {
        synchronized (Logger.class) {
            SingletonHolder.getInstance().mLogLevel = level;
        }
    }

    public static void error(@Nullable String tag, @NonNull String message)
    {
        if (SingletonHolder.getInstance().mLogLevel.compareTo(LogLevel.ERROR) <= 0) {
            SingletonHolder.getInstance().mDelegate.error(tag, message);
        }
    }

    public static void error(@Nullable String tag, @NonNull String message, @Nullable Throwable exception)
    {
        if (SingletonHolder.getInstance().mLogLevel.compareTo(LogLevel.ERROR) <= 0) {
            SingletonHolder.getInstance().mDelegate.error(tag, message, exception);
        }
    }

    public static void info(@Nullable String tag, @NonNull String message)
    {
        if (SingletonHolder.getInstance().mLogLevel.compareTo(LogLevel.INFO) <= 0) {
            SingletonHolder.getInstance().mDelegate.info(tag, message);
        }
    }

    public static void debug(@Nullable String tag, @NonNull String message)
    {
        if (SingletonHolder.getInstance().mLogLevel.compareTo(LogLevel.DEBUG) <= 0) {
            SingletonHolder.getInstance().mDelegate.debug(tag, message);
        }
    }
}
