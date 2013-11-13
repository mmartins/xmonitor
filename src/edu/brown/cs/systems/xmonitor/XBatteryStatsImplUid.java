package edu.brown.cs.systems.xmonitor;

import android.util.Log;
import com.android.internal.os.BatteryStatsImpl.Uid;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

import java.util.ArrayList;
import java.util.List;

/**
 * User: martins
 * Date: 11/10/13
 * Time: 11:41 PM
 */
public class XBatteryStatsImplUid extends XHook{
    protected XBatteryStatsImplUid(String methodName) {
        super(methodName);
    }

    public static List<XHook> getInstances() {
        List<XHook> listHook = new ArrayList<XHook>();

        listHook.add(new XBatteryStatsImplUid("noteStartWakeLocked"));
        listHook.add(new XBatteryStatsImplUid("noteStopWakeLocked"));
        listHook.add(new XBatteryStatsImplUid("noteStartSensor"));
        listHook.add(new XBatteryStatsImplUid("noteStopSensor"));
        listHook.add(new XBatteryStatsImplUid("noteStartGps"));
        listHook.add(new XBatteryStatsImplUid("noteStopGps"));

        return listHook;
    }

    @Override
    public String getClassName() {
        return "com.android.internal.os.BatteryStatsImpl$Uid";
    }

    @Override
    protected void before(XC_MethodHook.MethodHookParam param) throws Throwable {
        // do nothing
    }

    @Override
    protected void after(XC_MethodHook.MethodHookParam param) throws Throwable {
        String methodName = param.method.getName();

        if (methodName.equals("noteStartWakeLocked")) {
            Uid uid = (Uid) param.thisObject;
            int type = (Integer) param.args[2];
            String name = (String) param.args[1];
            XposedBridge.log(String.format("StartWakeLock: %d, %d. %s",
                    uid.getUid(), type, name));
            // Signature: (event-type, uid, type, wakelock_name)
            Utils.getInstance().shareEvent(name, Events.START_WAKELOCK,
                    uid.getUid(), type);
        } else if (methodName.equals("noteStopWakeLocked")) {
            Uid uid = (Uid) param.thisObject;
            int type = (Integer) param.args[2];
            String name = (String) param.args[1];
            XposedBridge.log(String.format("StopWakedLock: %d, %d, %s",
                    uid.getUid(), type, name));
            // Signature: (event-type, uid, wakelock_name)
            Utils.getInstance().shareEvent(name, Events.STOP_WAKELOCK,
                    uid.getUid(), type);
        } else if (methodName.equals("noteStartSensor")) {
            Uid uid = (Uid) param.thisObject;
            XposedBridge.log(String.format("StartSensor: %d, %d",
                    uid.getUid(), param.args[0]));
            // Signature: (event-type, uid, sensor)
            Utils.getInstance().shareEvent(null, Events.START_SENSOR,
                    uid.getUid(), (Integer) param.args[0]);
        } else if (methodName.equals("noteStopSensor")) {
            Uid uid = (Uid) param.thisObject;
            XposedBridge.log(String.format("StopSensor: %d, %d",
                    uid.getUid(), param.args[0]));
            // Signature: (event-type, uid, sensor)
            Utils.getInstance().shareEvent(null, Events.STOP_SENSOR,
                    uid.getUid(), (Integer) param.args[0]);
        } else if (methodName.equals("noteStartGps")) {
            Uid uid = (Uid) param.thisObject;
            XposedBridge.log(String.format("StartGps: %d", uid.getUid()));
            // Signature: (event-type, uid)
            Utils.getInstance().shareEvent(null, Events.START_GPS,
                    uid.getUid());
        } else if (methodName.equals("noteStopGps")) {
            Uid uid = (Uid) param.thisObject;
            XposedBridge.log(String.format("StopGps: %d", uid.getUid()));
            // Signature: (event-type, uid)
            Utils.getInstance().shareEvent(null, Events.STOP_GPS,
                    uid.getUid());
        } else
            Log.w(String.format("XMonitor/%s", this.getClass().getSimpleName
                    ()), "Unknown method=" + methodName);
    }
}
