package com.genymobile.scrcpy;

import com.genymobile.scrcpy.wrappers.ActivityThread;

import android.annotation.TargetApi;
import android.content.AttributionSource;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Process;

import java.lang.reflect.Method;

public final class FakeContext extends ContextWrapper {

    public static final String PACKAGE_NAME = "com.android.shell";
    public static final int ROOT_UID = 0; // Like android.os.Process.ROOT_UID, but before API 29

    private static final FakeContext INSTANCE = new FakeContext();

    private static Context retrieveSystemContext() {
        try {
            Class<?> activityThreadClass = ActivityThread.getActivityThreadClass();
            Object activityThread = ActivityThread.getActivityThread();

            Method getSystemContextMethod = activityThreadClass.getDeclaredMethod("getSystemContext");
            return (Context) getSystemContextMethod.invoke(activityThread);
        } catch (Exception e) {
            Ln.e("Cannot retrieve system context", e);
            return null;
        }
    }

    public static FakeContext get() {
        return INSTANCE;
    }

    private FakeContext() {
        super(retrieveSystemContext());
    }

    @Override
    public String getPackageName() {
        return PACKAGE_NAME;
    }

    @Override
    public String getOpPackageName() {
        return PACKAGE_NAME;
    }

    @TargetApi(Build.VERSION_CODES.S)
    @Override
    public AttributionSource getAttributionSource() {
        AttributionSource.Builder builder = new AttributionSource.Builder(Process.SHELL_UID);
        builder.setPackageName(PACKAGE_NAME);
        return builder.build();
    }

    // @Override to be added on SDK upgrade for Android 14
    @SuppressWarnings("unused")
    public int getDeviceId() {
        return 0;
    }
}
