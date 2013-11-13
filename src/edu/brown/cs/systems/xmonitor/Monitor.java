package edu.brown.cs.systems.xmonitor;

import android.app.AndroidAppHelper;
import android.os.Process;
import android.util.Log;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * Monitor for device usage inside Android Framework. Exports usage
 * information like PowerTutor+
 * User: martins
 * Date: 11/9/13
 * Time: 11:26 PM
 */
public class Monitor implements IXposedHookLoadPackage, IXposedHookZygoteInit {

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        Log.i("XMonitor", String.format("load %s", startupParam.modulePath));

        hookAll(XNotificationManager.getInstances());
        hookAll(XBatteryStatsImpl.getInstances());
        hookAll(XBatteryStatsImplUid.getInstances());
        hookAll(XAudioService.getInstances());
        hookAll(XMediaPlayer.getInstances());
        hookAll(XMediaPlayerEventHandler.getInstances());
    }

    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws
            Throwable {
        // do nothing
    }

    private static void hookAll(List<XHook> listHook) {
        for (XHook hook : listHook)
            hook(hook);
    }

    private static void hookAll(List<XHook> listHook,
                                ClassLoader classLoader) {
        for (XHook hook : listHook)
            hook(hook, classLoader);
    }

    private static void hook(XHook hook) {
        hook(hook, null);
    }

    private static void hook(final XHook hook, ClassLoader classLoader) {
        try {
            // Create hook method
            XC_MethodHook methodHook = new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        if (Process.myUid() <= 0)
                            return;
                        hook.before(param);
                    } catch (Throwable ex) {
                        Log.e("XMonitor", ex.toString());
                        ex.printStackTrace();
                        throw ex;
                    }
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (!param.hasThrowable())
                        try {
                            if (Process.myUid() <= 0)
                                return;
                            hook.after(param);
                        } catch (Throwable ex) {
                            Log.e("XMonitor", ex.toString());
                            ex.printStackTrace();
                            throw ex;
                        }
                }
            };

            // Find class
            Class<?> hookClass = findClass(hook.getClassName(), classLoader);
            if (hookClass == null) {
                Log.w(
                        hook.getClass().getSimpleName(),
                        String.format("%s: class not found: %s", AndroidAppHelper.currentPackageName(),
                                hook.getClassName()));
                return;
            }

            // Add hook
            Set<XC_MethodHook.Unhook> hookSet = new HashSet<XC_MethodHook.Unhook>();
            if (hook.getMethodName() == null) {
                for (Constructor<?> constructor : hookClass.getDeclaredConstructors())
                    hookSet.add(XposedBridge.hookMethod(constructor,
                            methodHook));
            } else
                for (Method method : hookClass.getDeclaredMethods())
                    if (method.getName().equals(hook.getMethodName()))
                        hookSet.add(XposedBridge.hookMethod(method, methodHook));

            // Check if found
            if (hookSet.isEmpty()) {
                Log.w(
                        String.format("XMonitor/%s", hook.getClass().getSimpleName()),
                        String.format("%s: method not found: %s.%s", AndroidAppHelper.currentPackageName(),
                                hookClass.getName(), hook.getMethodName()));
            }
        } catch (Throwable ex) {
            Log.e("XMonitor", ex.toString());
        }
    }
}
