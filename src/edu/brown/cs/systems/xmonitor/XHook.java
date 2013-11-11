package edu.brown.cs.systems.xmonitor;

import android.util.Log;
import de.robv.android.xposed.XC_MethodHook;

/**
 * User: martins
 * Date: 11/10/13
 * Time: 9:59 PM
 */
public abstract class XHook {
    private String mMethodName;

    protected XHook(String methodName) {
        mMethodName = methodName;
    }

    public boolean isVisible() {
        return true;
    }

    abstract public String getClassName();

    public String getMethodName() {
        return mMethodName;
    }

    abstract protected void before(XC_MethodHook.MethodHookParam param) throws Throwable;

    abstract protected void after(XC_MethodHook.MethodHookParam param) throws Throwable;

    protected void info(String message) {
        Log.i(String.format("XMonitor/%s", this.getClass().getSimpleName()),
                message);
    }

    protected void warning(String message) {
        Log.w(String.format("XMonitor/%s", this.getClass().getSimpleName()),
                message);
    }

    protected void error(String message) {
        Log.e(String.format("XMonitor/%s", this.getClass().getSimpleName()),
                message);
    }
}
