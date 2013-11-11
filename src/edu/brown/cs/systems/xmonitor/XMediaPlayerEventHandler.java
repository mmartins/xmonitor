package edu.brown.cs.systems.xmonitor;

import android.media.MediaPlayer;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

import java.util.ArrayList;
import java.util.List;

import static de.robv.android.xposed.XposedHelpers.getObjectField;

/**
 * User: martins
 * Date: 11/11/13
 * Time: 12:15 AM
 */
public class XMediaPlayerEventHandler extends XHook {

    protected XMediaPlayerEventHandler(String methodName) {
        super(methodName);
    }

    public static List<XHook> getInstances() {
        List<XHook> listHook = new ArrayList<XHook>();
        listHook.add(new XMediaPlayerEventHandler("handleMessage"));
        return listHook;
    }

    @Override
    public String getClassName() {
        return "android.media.MediaPlayer$EventHandler";
    }

    @Override
    protected void before(XC_MethodHook.MethodHookParam param) throws Throwable {
        String methodName = param.method.getName();

        if (methodName.equals("handleMessage")) {
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
         } else
            Log.w(String.format("XMonitor/%s", this.getClass().getSimpleName
                    ()), "Unknown method=" + methodName);
    }

    @Override
    protected void after(XC_MethodHook.MethodHookParam param) throws Throwable {
        // do nothing
    }
}
