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
 * Time: 8:59 PM
 */
public class XMediaPlayer extends XHook {

    private XMediaPlayer(String methodName) {
        super(methodName);
    }

    public static List<XHook> getInstances() {
        List<XHook> listHook = new ArrayList<XHook>();

        listHook.add(new XMediaPlayer("start"));
        listHook.add(new XMediaPlayer("stop"));
        listHook.add(new XMediaPlayer("release"));
        listHook.add(new XMediaPlayer("reset"));
        listHook.add(new XMediaPlayer("finalize"));

        return listHook;
    }

    @Override
    public String getClassName() {
        return "android.media.MediaPlayer";
    }

    @Override
    protected void before(XC_MethodHook.MethodHookParam param) throws Throwable {
        String methodName = param.method.getName();

        if (methodName.equals("start")) {
            int uid = Process.myUid();
            int id = param.thisObject.hashCode();
            XposedBridge.log(String.format("StartMedia: %d, %d", uid, id));
            // Signature: (event-type, uid, id)
            Utils.getInstance().shareEvent(null, Events.START_MEDIA, uid, id);
        } else if (methodName.equals("stop") || methodName.equals("release")
                || methodName.equals("reset") || methodName.equals
                ("finalize")) {
            int uid = Process.myUid();
            int id = param.thisObject.hashCode();
            XposedBridge.log(String.format("StopMedia: %d. %d", uid, id));
            // Signature: (event-type, uid, id)
            Utils.getInstance().shareEvent(null, Events.STOP_MEDIA, uid, id);
        } else
            Log.w(String.format("XMonitor/%s", this.getClass().getSimpleName
                    ()), "Unknown method=" + methodName);
    }

    @Override
    protected void after(XC_MethodHook.MethodHookParam param) throws
            Throwable {
        // do nothing
    }
}
