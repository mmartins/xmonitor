package edu.brown.cs.systems.xmonitor;

import android.util.Log;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

import java.util.ArrayList;
import java.util.List;

/**
 * User: martins
 * Date: 11/11/13
 * Time: 1:13 AM
 */
public class XBatteryStatsImpl extends XHook {

    protected XBatteryStatsImpl(String methodName) {
        super(methodName);
    }

    public static List<XHook> getInstances() {
        List<XHook> listHook = new ArrayList<XHook>();
        listHook.add(new XBatteryStatsImpl("noteScreenBrightnessLocked"));
        return listHook;
    }

    @Override
    public String getClassName() {
        return "com.android.internal.os.BatteryStatsImpl";
    }

    @Override
    protected void before(XC_MethodHook.MethodHookParam param) throws Throwable {
        // do nothing
    }

    @Override
    protected void after(XC_MethodHook.MethodHookParam param) throws Throwable {
        String methodName = param.method.getName();

        if (methodName.equals("noteScreenBrightnessLocked")) {
            int brightness = (Integer) param.args[0];
            XposedBridge.log(String.format("ScreenBrightness: %d",
                    brightness));
            // Signature: (event-type, brightness)
            Utils.getInstance().shareEvent(null, Events.SCREEN_BRIGHTNESS,
                    brightness);
        } else
            Log.w(String.format("XMonitor/%s", this.getClass().getSimpleName
                    ()), "Unknown method=" + methodName);
    }
}
