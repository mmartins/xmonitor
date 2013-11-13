package edu.brown.cs.systems.xmonitor;

import android.os.Binder;
import android.util.Log;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

import java.util.ArrayList;
import java.util.List;

/**
 * User: martins
 * Date: 11/10/13
 * Time: 11:32 PM
 */
public class XAudioService extends XHook {
    protected XAudioService(String methodName) {
        super(methodName);
    }

    public static List<XHook> getInstances() {
        List<XHook> listHook = new ArrayList<XHook>();

        listHook.add(new XAudioService("playSoundEffect"));
        listHook.add(new XAudioService("playSoundEffectVolume"));

        return listHook;
    }

    @Override
    public String getClassName() {
        return "android.media.AudioService";
    }

    @Override
    protected void before(XC_MethodHook.MethodHookParam param) throws Throwable {
        String methodName = param.method.getName();

        if (methodName.equals("playSoundEffect") || methodName.equals
                ("playSoundEffectVolume")) {
            int uid = Binder.getCallingUid();
            XposedBridge.log(String.format("SystemMediaCall: %d", uid));
            // Signature: (event-type, uid)
            Utils.getInstance().shareEvent(null, Events.SYSTEM_MEDIA_CALL,
                    uid);
        } else
            Log.w(String.format("XMonitor/%s", this.getClass().getSimpleName
                    ()), "Unknown method=" + methodName);
    }

    @Override
    protected void after(XC_MethodHook.MethodHookParam param) throws Throwable {
        // do nothing
    }
}
