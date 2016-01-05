package com.miracolab.junkcalls.utils;

import android.util.Log;

public class LogUtil {
    private static String TAG = "JunkCalls";
    public static boolean sLogFlagNormal = true;//HtcBuildFlag.Htc_DEBUG_flag; // Normal logs will also been shown if
    // log level is critical
    public static boolean sLogFlagCritical; // Critical logs will not been shown
    // if log level is normal
    
    public static boolean sLogFlagTest = true;

    public static void test(String tag, Object... args) {
        if ( sLogFlagTest ) {
            StringBuilder sb = new StringBuilder();
            for (Object s : args) {
                sb.append(String.valueOf(s));
            }

            Log.v("DNATest", getPrefix(tag) + sb.toString());
        }
    }
    
    public static void critical(String tag, String msg) {
        if (sLogFlagCritical) {
            Log.v(TAG, getPrefix(tag) + msg);
        }
    }

    public static void critical(String tag, String msg, Throwable tr) {
        if (sLogFlagCritical) {
            Log.v(TAG, getPrefix(tag) + msg, tr);
        }
    }
    
    public static void v(String tag, Object... args) {
        if (sLogFlagNormal) {
            StringBuilder sb = new StringBuilder();
            for (Object s : args) {
                sb.append(String.valueOf(s));
            }

            Log.v(TAG, getPrefix(tag) + sb.toString());
        }
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (sLogFlagNormal) {
            Log.v(TAG, getPrefix(tag) + msg, tr);
        }
    }

    public static void d(String tag, Object... args) {
        if (sLogFlagNormal) {
            StringBuilder sb = new StringBuilder();
            for (Object s : args) {
                sb.append(String.valueOf(s));
            }

            Log.d(TAG, getPrefix(tag) + sb.toString());
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (sLogFlagNormal) {
            Log.d(TAG, getPrefix(tag) + msg, tr);
        }
    }

    public static void i(String tag, String msg) {
        Log.i(TAG, getPrefix(tag) + msg);
    }

    public static void i(String tag, String msg, Throwable tr) {
        Log.i(TAG, getPrefix(tag) + msg, tr);
    }

    public static void w(String tag, String msg) {
        Log.w(TAG, getPrefix(tag) + msg);
    }

    public static void w(String tag, String msg, Throwable tr) {
        Log.w(TAG, getPrefix(tag) + msg, tr);
    }

    public static void e(String tag, String msg) {
        Log.e(TAG, getPrefix(tag) + msg);
    }

    public static void e(String tag, String msg, String arg) {
        Log.e(TAG, getPrefix(tag) + msg + arg);
    }

    public static void e(String tag,Throwable tr) {
        Log.e(TAG, getPrefix(tag) + tr.getMessage(), tr);
    }
    
    
    public static void e(String tag, String msg, Throwable tr) {
        Log.e(TAG, getPrefix(tag) + msg, tr);
    }

    private static String getPrefix(String tag) {
        return "<" + tag + "> ";
    }

    public static String whoCalledMe() {
        StackTraceElement[] stackTraceElements = Thread.currentThread()
                .getStackTrace();
        StackTraceElement caller = stackTraceElements[4];
        String classname = caller.getClassName();
        String methodName = caller.getMethodName();
        int lineNumber = caller.getLineNumber();
        return classname + "." + methodName + ":" + lineNumber;
    }

    public static void showCallStack() {
        StackTraceElement[] stackTraceElements = Thread.currentThread()
                .getStackTrace();
        for (int i = 3; i < stackTraceElements.length; i++) {
            StackTraceElement ste = stackTraceElements[i];
            String classname = ste.getClassName();
            String methodName = ste.getMethodName();
            int lineNumber = ste.getLineNumber();
            d(TAG, classname + "." + methodName + ":" + lineNumber);
        }
    }

}
