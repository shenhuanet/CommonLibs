package com.shenhua.commonlibs.utils;

import android.os.Debug;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 日志工具类
 * Created by shenhua on 8/22/2016.
 */
public class LogUtils {

    public static final int V = 0x1;
    private static final String NULL_TIPS = "Log with null object.";
    private static final String PARAM = "Param";
    private static final int MIN_STACK_OFFSET = 3;// starts at this class after two native calls
    private static final int MAX_STACK_TRACE_SIZE = 131071; //128 KB - 1
    private static final int METHOD_COUNT = 2; // show method count in trace
    private static boolean isDebug = true;// 是否调试模式
    private static String debugTag = "shenhua_debug";// LogCat的标记

    public static void v(Object msg) {
        printLog(V, null, msg);
    }

    private static void printLog(int type, String tagStr, Object... objects) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        int index = 5;
        String className = stackTraceElements[index].getFileName();
        String methodName = stackTraceElements[index].getMethodName();
        int lineNum = stackTraceElements[index].getLineNumber();
        String methodShortName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[").append(className).append(":").append(lineNum).append("]").append(" ## ").append(methodShortName);
        String tag = tagStr == null ? className : tagStr;
        String msg = objects == null ? NULL_TIPS : getObjectString(objects);
        String log = stringBuilder.toString() + "  " + msg;
        Log.v(tag, log);
    }

    private static String getObjectString(Object... objects) {
        if (objects.length > 1) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\n");
            for (int i = 0; i < objects.length; i++) {
                Object object = objects[i];
                stringBuilder.append(PARAM).append("[").append(i).append("]").append(" = ");
                if (object == null) stringBuilder.append("null").append("\n");
                else stringBuilder.append(object.toString()).append("\n");
            }
            return stringBuilder.toString();
        } else {
            Object object = objects[0];
            return object == null ? "null" : object.toString();
        }
    }

    /**
     * Verbose.
     *
     * @param message the message
     */
    public static void verbose(String message) {
        verbose("", message);
    }

    /**
     * Verbose.
     *
     * @param object  the object
     * @param message the message
     */
    public static void verbose(Object object, String message) {
        verbose(object.getClass().getSimpleName(), message);
    }

    /**
     * 记录“verbose”级别的信息
     *
     * @param tag     the tag
     * @param message the message
     */
    public static void verbose(String tag, String message) {
        if (isDebug) {
            tag = debugTag + ((tag == null || tag.trim().length() == 0) ? "" : "-") + tag;
            String msg = message + getTraceElement();
            try {
                Log.v(tag, msg);
            } catch (Exception e) {
                System.out.println(tag + ">>>" + msg);
            }
        }
    }

    /**
     * Debug.
     *
     * @param message the message
     */
    public static void debug(String message) {
        debug("", message);
    }

    /**
     * Debug.
     *
     * @param object  the object
     * @param message the message
     */
    public static void debug(Object object, String message) {
        debug(object.getClass().getSimpleName(), message);
    }

    /**
     * 记录“debug”级别的信息
     *
     * @param tag     the tag
     * @param message the message
     */
    public static void debug(String tag, String message) {
        if (isDebug) {
            tag = debugTag + ((tag == null || tag.trim().length() == 0) ? "" : "-") + tag;
            String msg = message + getTraceElement();
            try {
                Log.d(tag, msg);
            } catch (Exception e) {
                System.out.println(tag + ">>>" + msg);
            }
        }
    }

    /**
     * Warn.
     *
     * @param e the e
     */
    public static void warn(Throwable e) {
        warn(toStackTraceString(e));
    }

    /**
     * Warn.
     *
     * @param message the message
     */
    public static void warn(String message) {
        warn("", message);
    }

    /**
     * Warn.
     *
     * @param object  the object
     * @param message the message
     */
    public static void warn(Object object, String message) {
        warn(object.getClass().getSimpleName(), message);
    }

    /**
     * Warn.
     *
     * @param object the object
     * @param e      the e
     */
    public static void warn(Object object, Throwable e) {
        warn(object.getClass().getSimpleName(), toStackTraceString(e));
    }

    /**
     * 记录“warn”级别的信息
     *
     * @param tag     the tag
     * @param message the message
     */
    public static void warn(String tag, String message) {
        if (isDebug) {
            tag = debugTag + ((tag == null || tag.trim().length() == 0) ? "" : "-") + tag;
            String msg = message + getTraceElement();
            try {
                Log.w(tag, msg);
            } catch (Exception e) {
                System.out.println(tag + ">>>" + msg);
            }
        }
    }

    /**
     * Error.
     *
     * @param e the e
     */
    public static void error(Throwable e) {
        error(toStackTraceString(e));
    }

    /**
     * Error.
     *
     * @param message the message
     */
    public static void error(String message) {
        error("", message);
    }

    /**
     * Error.
     *
     * @param object  the object
     * @param message the message
     */
    public static void error(Object object, String message) {
        error(object.getClass().getSimpleName(), message);
    }

    /**
     * Error.
     *
     * @param object the object
     * @param e      the e
     */
    public static void error(Object object, Throwable e) {
        error(object.getClass().getSimpleName(), toStackTraceString(e));
    }

    /**
     * 记录“error”级别的信息
     *
     * @param tag     the tag
     * @param message the message
     */
    public static void error(String tag, String message) {
        if (isDebug) {
            tag = debugTag + ((tag == null || tag.trim().length() == 0) ? "" : "-") + tag;
            String msg = message + getTraceElement();
            try {
                Log.e(tag, msg);
            } catch (Exception e) {
                System.err.println(tag + ">>>" + msg);
            }
        }
    }

    /**
     * 在某个方法中调用生成.trace文件。然后拿到电脑上用DDMS工具打开分析
     *
     * @see #stopMethodTracing()
     */
    public static void startMethodTracing() {
        if (isDebug) {
            Debug.startMethodTracing(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + debugTag + ".trace");
        }
    }

    /**
     * Stop method tracing.
     */
    public static void stopMethodTracing() {
        if (isDebug) {
            Debug.stopMethodTracing();
        }
    }

    /**
     * To stack trace string string.
     * 此方法参见：https://github.com/Ereza/CustomActivityOnCrash
     *
     * @param throwable the throwable
     * @return the string
     */
    public static String toStackTraceString(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        String stackTraceString = sw.toString();
        //Reduce data to 128KB so we don't get a TransactionTooLargeException when sending the intent.
        //The limit is 1MB on Android but some devices seem to have it lower.
        //See: http://developer.android.com/reference/android/os/TransactionTooLargeException.html
        //And: http://stackoverflow.com/questions/11451393/what-to-do-on-transactiontoolargeexception#comment46697371_12809171
        if (stackTraceString.length() > MAX_STACK_TRACE_SIZE) {
            String disclaimer = " [stack trace too large]";
            stackTraceString = stackTraceString.substring(0, MAX_STACK_TRACE_SIZE - disclaimer.length()) + disclaimer;
        }
        return stackTraceString;
    }

    /**
     * 可显示调用方法所在的文件行号，在AndroidStudio的logcat处可点击定位。
     * 此方法参考：https://github.com/orhanobut/logger
     */
    private static String getTraceElement() {
        try {
            int methodCount = METHOD_COUNT;
            StackTraceElement[] trace = Thread.currentThread().getStackTrace();
            int stackOffset = _getStackOffset(trace);
            //corresponding method count with the current stack may exceeds the stack trace. Trims the count
            if (methodCount + stackOffset > trace.length) {
                methodCount = trace.length - stackOffset - 1;
            }
            String level = "    ";
            StringBuilder builder = new StringBuilder();
            for (int i = methodCount; i > 0; i--) {
                int stackIndex = i + stackOffset;
                if (stackIndex >= trace.length) {
                    continue;
                }
                builder.append("\n")
                        .append(level)
                        .append(_getSimpleClassName(trace[stackIndex].getClassName()))
                        .append(".")
                        .append(trace[stackIndex].getMethodName())
                        .append(" ")
                        .append("(")
                        .append(trace[stackIndex].getFileName())
                        .append(":")
                        .append(trace[stackIndex].getLineNumber())
                        .append(")");
                level += "    ";
            }
            return builder.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Determines the starting index of the stack trace, after method calls made by this class.
     *
     * @param trace the stack trace
     * @return the stack offset
     */
    private static int _getStackOffset(StackTraceElement[] trace) {
        for (int i = MIN_STACK_OFFSET; i < trace.length; i++) {
            StackTraceElement e = trace[i];
            String name = e.getClassName();
            if (!name.equals(LogUtils.class.getName())) {
                return --i;
            }
        }
        return -1;
    }

    private static String _getSimpleClassName(String name) {
        int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }

}
