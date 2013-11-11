package edu.brown.cs.systems.xmonitor;

import android.media.AudioService;
import android.media.MediaPlayer;
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
            XposedBridge.log(String.format("StartMedia: %d, %d",
                    Process.myUid(), param.thisObject.hashCode()));
        } else if (methodName.equals("stop")) {
            XposedBridge.log(String.format("StopMedia: %d. %d",
                    Process.myUid(), param.thisObject.hashCode()));
        } else if (methodName.equals("release")) {
            XposedBridge.log(String.format("StopMedia: %d. %d",
                    Process.myUid(), param.thisObject.hashCode()));
         } else if (methodName.equals("reset")) {
            XposedBridge.log(String.format("StopMedia: %d. %d",
                    Process.myUid(), param.thisObject.hashCode()));
         } else if (methodName.equals("finalize")) {
            XposedBridge.log(String.format("StopMedia: %d. %d",
                    Process.myUid(), param.thisObject.hashCode()));
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
