package edu.brown.cs.systems.xmonitor;

import android.util.Log;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

import java.util.ArrayList;
import java.util.List;

/**
 * User: martins
 * Date: 11/13/13
 * Time: 6:07 PM
 */
public class XGpsLocationProvider extends XHook {

    protected XGpsLocationProvider(String methodName) {
        super(methodName);
    }

    public static List<XHook> getInstances() {
        List<XHook> listHook = new ArrayList<XHook>();
        listHook.add(new XGpsLocationProvider("reportStatus"));
        return listHook;
    }

    @Override
    public String getClassName() {
        return "com.android.internal.location.GpsLocationProvider";
    }

    @Override
    protected void before(XC_MethodHook.MethodHookParam param) throws Throwable {
        String methodName = param.method.getName();

        if (methodName.equals("reportStatus")) {
            int status = (Integer) param.args[2];
            XposedBridge.log(String.format("GpsStatus: %d", status));
            // Signature: (event-type, status)
            Utils.getInstance().shareEvent(null, Events.GPS_STATUS, status);
        } else
            Log.w(String.format("XMonitor/%s", this.getClass().getSimpleName
                    ()), "Unknown method=" + methodName);
    }

    @Override
    protected void after(XC_MethodHook.MethodHookParam param) throws Throwable {
        // do nothing
    }
}
