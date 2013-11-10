package edu.brown.cs.systems.xmonitor;

import android.media.AudioService;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Message;
import android.os.Process;
import com.android.internal.os.BatteryStatsImpl.Uid;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
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
}
