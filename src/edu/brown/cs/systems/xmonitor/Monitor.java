package edu.brown.cs.systems.xmonitor;

import android.app.AndroidAppHelper;
import android.media.AudioService;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import com.android.internal.os.BatteryStatsImpl.Uid;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

/**
 * Monitor for device usage inside Android Framework. Exports usage
 * information like PowerTutor+
 * User: martins
 * Date: 11/9/13
 * Time: 11:26 PM
 */
public class Monitor implements IXposedHookLoadPackage {


    public void handleLoadPackage(final LoadPackageParam lpparam) throws
            Throwable {
        if (lpparam.packageName.equals("android.app")) {
            findAndHookMethod("android.app.NotificationManager",
                    lpparam.classLoader, "notify", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param)
                        throws Throwable {
                    XposedBridge.log(String.format("SystemMediaCall: %d",
                            Process.myUid()));
                }
            });
        } else if (lpparam.packageName.equals("com.android.internal.os")) {
            findAndHookMethod(Class.forName("com.android.internal.os" +
                    ".BatteryStatsImpl$Uid"), "noteScreenBrightness",
                    new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param)
                        throws Throwable {
                    XposedBridge.log(String.format("ScreenBrightness: %d",
                            param.args[0]));
                }
            });

            findAndHookMethod(Class.forName("com.android.internal.os" +
                    ".BatteryStatsImpl$Uid"), "noteStartWakeLocked",
                    new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param)
                        throws Throwable {
                    XposedBridge.log(String.format("StartWakeLock: %d, %s, %d",
                            param.args[0], param.args[2], param.args[3]));
                }
            });

            findAndHookMethod(Class.forName("com.android.internal.os" +
                    ".BatteryStatsImpl$Uid"), "noteStopWakeLocked",
                    new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param)
                        throws Throwable {
                    Uid uid = (Uid) param.thisObject;
                    XposedBridge.log(String.format("StopWakeLock: %d, %s, %d",
                            uid.getUid(), param.args[1], param.args[2]));
                }
            });

            findAndHookMethod(Class.forName("com.android.internal.os" +
                    ".BatteryStatsImpl$Uid"), "noteStartSensor",
                    new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param)
                        throws Throwable {
                    Uid uid = (Uid) param.thisObject;
                    XposedBridge.log(String.format("StartSensor: %d, %d",
                            uid.getUid(), param.args[0]));
                }
            });

            findAndHookMethod(Class.forName("com.android.internal.os" +
                    ".BatteryStatsImpl$Uid"), "noteStopSensor",
                    new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param)
                        throws Throwable {
                    Uid uid = (Uid) param.thisObject;
                    XposedBridge.log(String.format("StopSensor: %d, %d",
                            uid.getUid(), param.args[0]));
                }
            });

            findAndHookMethod(Class.forName("com.android.internal.os" +
                    ".BatteryStatsImpl$Uid"), "noteStartGps",
                    new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param)
                        throws Throwable {
                    Uid uid = (Uid) param.thisObject;
                    XposedBridge.log(String.format("StartGps: %d",
                            uid.getUid()));
                }
            });

            findAndHookMethod(Class.forName("com.android.internal.os" +
                    ".BatteryStatsImpl$Uid"), "noteStopGps",
                    new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param)
                        throws Throwable {
                    Uid uid = (Uid) param.thisObject;
                    XposedBridge.log(String.format("StopGps: %d", uid.getUid()));
                }
            });
        } else if (lpparam.packageName.equals("android.media")) {
            findAndHookMethod("android.media.AudioService",
                    lpparam.classLoader, "playSoundEffect",
                    new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param)
                        throws Throwable {
                    XposedBridge.log(String.format("SystemMediaCall: %d",
                            Binder.getCallingUid()));
                }
            });

            findAndHookMethod("android.media.AudioService",
                    lpparam.classLoader, "playSoundEffectVolume",
                    new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param)
                        throws Throwable {
                    XposedBridge.log(String.format("SystemMediaCall: %d",
                            Binder.getCallingUid()));
                }
            });

            findAndHookMethod("android.media.MediaPlayer",
                    lpparam.classLoader, "start", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param)
                        throws Throwable {
                    AudioService as = (AudioService) param.thisObject;
                    XposedBridge.log(String.format("StartMedia: %d, %d",
                            Process.myUid(), as.hashCode()));
                }
            });

            findAndHookMethod("android.media.MediaPlayer",
                    lpparam.classLoader, "stop", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param)
                        throws Throwable {
                    AudioService as = (AudioService) param.thisObject;
                    XposedBridge.log(String.format("StopMedia: %d, %d",
                            Process.myUid(), as.hashCode()));
                }
            });

            findAndHookMethod("android.media.MediaPlayer",
                    lpparam.classLoader, "release", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param)
                        throws Throwable {
                    AudioService as = (AudioService) param.thisObject;
                    XposedBridge.log(String.format("StopMedia: %d, %d",
                            Process.myUid(), as.hashCode()));
                }
            });

            findAndHookMethod("android.media.MediaPlayer",
                    lpparam.classLoader, "reset", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param)
                        throws Throwable {
                    AudioService as = (AudioService) param.thisObject;
                    XposedBridge.log(String.format("StopMedia: %d, %d",
                            Process.myUid(), as.hashCode()));
                }
            });

            findAndHookMethod("android.media.MediaPlayer",
                    lpparam.classLoader, "finalize", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param)
                        throws Throwable {
                    AudioService as = (AudioService) param.thisObject;
                    XposedBridge.log(String.format("StopMedia: %d, %d",
                            Process.myUid(), as.hashCode()));
                }
            });

            findAndHookMethod(Class.forName("android.media" +
                    ".MediaPlayer$EventHandler"), "handleMessage",
                    new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param)
                        throws Throwable {
                    Message msg = (Message) param.args[0];
                    MediaPlayer player = (MediaPlayer) getObjectField(param
                            .thisObject, "mMediaPlayer");

                    // MEDIA_PLAYBACK_COMPLETE
                    if (msg.what == 2)
                        XposedBridge.log(String.format("StopMedia: %d, %d",
                                Process.myUid(), player.hashCode()));
                    else if (msg.what == 5) // MEDIA_SET_VIDEO_SIZE
                        XposedBridge.log(String.format("VideoSize: %d, %d, " +
                                "%d, %d", Process.myPid(), player.hashCode(),
                                msg.arg1, msg.arg2));
                }
            });
        }
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
                    if (Modifier.isPublic(constructor.getModifiers()) ? hook.isVisible() : !hook.isVisible())
                        hookSet.add(XposedBridge.hookMethod(constructor, methodHook));
            } else
                for (Method method : hookClass.getDeclaredMethods())
                    if (method.getName().equals(hook.getMethodName())
                            && (Modifier.isPublic(method.getModifiers()) ? hook.isVisible() : !hook.isVisible()))
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
