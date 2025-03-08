/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.realmeparts;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();
    private static volatile Thread sMainThread;
    private static volatile Handler sMainThreadHandler;
    private static volatile ExecutorService sThreadExecutor;

    /**
     * Write a string value to the specified file.
     *
     * @param filename The filename
     * @param value    The value
     */
    public static void writeValue(String filename, String value) {
        if (filename == null || value == null) {
            Log.e(TAG, "Filename or value cannot be null.");
            return;
        }

        File file = new File(filename);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                Log.e(TAG, "Failed to create directories for file: " + filename);
                return;
            }
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(value.getBytes());
            fos.flush();
        } catch (IOException e) {
            Log.e(TAG, "Error writing to file: " + filename, e);
        }
    }

    /**
     * Check if the specified file exists.
     *
     * @param filename The filename
     * @return Whether the file exists or not
     */
    public static boolean fileExists(String filename) {
        return filename != null && new File(filename).exists();
    }

    /**
     * Check if the specified file is writable.
     *
     * @param filename The filename
     * @return Whether the file is writable or not
     */
    public static boolean fileWritable(String filename) {
        if (filename == null) {
            return false;
        }
        File file = new File(filename);
        return file.exists() && file.canWrite();
    }

    /**
     * Set a value to the specified file.
     *
     * @param path  The file path
     * @param value The value to write
     */
    static void setValue(String path, Object value) {
        if (path == null) {
            Log.e(TAG, "Path cannot be null.");
            return;
        }

        if (!fileWritable(path)) {
            Log.e(TAG, "File is not writable: " + path);
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(new File(path))) {
            fos.write(value.toString().getBytes());
            fos.flush();
        } catch (IOException e) {
            Log.e(TAG, "Error writing to file: " + path, e);
        }
    }

    public static String readLine(String filename) {
        if (filename == null) {
            return null;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)), 1024)) {
            return br.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    public static boolean getFileValueAsBoolean(String filename, boolean defValue) {
        String fileValue = readLine(filename);
        return fileValue != null && !fileValue.equals("0");
    }

    public static String getFileValue(String filename, String defValue) {
        String fileValue = readLine(filename);
        return fileValue != null ? fileValue : defValue;
    }

    public static String getLocalizedString(Resources res, String stringName, String stringFormat) {
        String name = stringName.toLowerCase().replace(" ", "_");
        String nameRes = String.format(stringFormat, name);
        return getStringForResourceName(res, nameRes, stringName);
    }

    public static String getStringForResourceName(Resources res, String resourceName, String defaultValue) {
        int resId = res.getIdentifier(resourceName, "string", "com.realmeparts");
        return resId > 0 ? res.getString(resId) : defaultValue;
    }

    public static String ProductName() {
        return Build.PRODUCT;
    }

    public static String InputStreamToString(InputStream inputStream) {
        try {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            return new String(bytes);
        } catch (IOException e) {
            return null;
        }
    }

    static void setStringProp(String prop, String value) {
        SystemProperties.set(prop, value);
    }

    static String getStringProp(String prop, String defaultValue) {
        return SystemProperties.get(prop, defaultValue);
    }

    static void setintProp(String prop, int value) {
        SystemProperties.set(prop, String.valueOf(value));
    }

    static int getintProp(String prop, int defaultValue) {
        return SystemProperties.getInt(prop, defaultValue);
    }

    public static boolean isMainThread() {
        if (sMainThread == null) {
            sMainThread = Looper.getMainLooper().getThread();
        }
        return Thread.currentThread() == sMainThread;
    }

    public static Handler getUiThreadHandler() {
        if (sMainThreadHandler == null) {
            sMainThreadHandler = new Handler(Looper.getMainLooper());
        }
        return sMainThreadHandler;
    }

    public static void ensureMainThread() {
        if (!isMainThread()) {
            throw new RuntimeException("Must be called on the UI thread");
        }
    }

    public static Future postOnBackgroundThread(Runnable runnable) {
        return getThreadExecutor().submit(runnable);
    }

    public static Future postOnBackgroundThread(Callable callable) {
        return getThreadExecutor().submit(callable);
    }

    public static void postOnMainThread(Runnable runnable) {
        getUiThreadHandler().post(runnable);
    }

    private static synchronized ExecutorService getThreadExecutor() {
        if (sThreadExecutor == null) {
            sThreadExecutor = Executors.newFixedThreadPool(
                    Runtime.getRuntime().availableProcessors());
        }
        return sThreadExecutor;
    }

    public static void startService(Context context, Class<?> serviceClass) {
        context.startServiceAsUser(new Intent(context, serviceClass), UserHandle.CURRENT);
        Log.d("DeviceSettings", "Starting " + serviceClass.getCanonicalName());
    }

    public static void stopService(Context context, Class<?> serviceClass) {
        context.stopServiceAsUser(new Intent(context, serviceClass), UserHandle.CURRENT);
        Log.d("DeviceSettings", "Stopping " + serviceClass.getCanonicalName());
    }
}
