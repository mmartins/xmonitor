package edu.brown.cs.systems.xmonitor;

import android.os.Process;
import android.util.Log;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

import java.util.ArrayList;
import java.util.List;

/**
 * User: martins
 * Date: 11/10/13
 * Time: 11:53 PM
 */
public class XNotificationManager extends XHook {

    protected XNotificationManager(String methodName) {
        super(methodName);
    }

    public static List<XHook> getInstances() {
        List<XHook> listHook = new ArrayList<XHook>();
        listHook.add(new XNotificationManager("notify"));
        return listHook;
    }

    @Override
    public String getClassName() {
        return "android.app.NotificationManager";
    }

    @Override
    protected void before(XC_MethodHook.MethodHookParam param) throws Throwable {
        // do nothing
    }

    @Override
    protected void after(XC_MethodHook.MethodHookParam param) throws Throwable {
        String methodName = param.method.getName();

        if (methodName.equals("notify")) {
            XposedBridge.log(String.format("SystemMediaCall: %d",
                    Process.myUid()));
        } else
            Log.w(String.format("XMonitor/%s", this.getClass().getSimpleName
                    ()), "Unknown method=" + methodName);
    }
}
