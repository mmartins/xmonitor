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
            if (msg.what == 2) {
                int uid = Process.myUid();
                int id = player.hashCode();
                XposedBridge.log(String.format("StopMedia: %d, %d", uid, id));
                // Signature: (event-type, uid, id)
                Utils.getInstance().shareEvent(null, Events.STOP_MEDIA, uid,
                        id);
            } else if (msg.what == 5) {// MEDIA_SET_VIDEO_SIZE
                int uid = Process.myUid();
                int id = player.hashCode();
                int width = msg.arg1;
                int height = msg.arg2;
                XposedBridge.log(String.format("VideoSize: %d, %d, " +
                                "%d, %d", uid, id, width, height));
                // Signature: (event-type, uid, id, width, height)
                Utils.getInstance().shareEvent(null, Events.VIDEO_SIZE, uid,
                        id, width, height);
            }
         } else
            Log.w(String.format("XMonitor/%s", this.getClass().getSimpleName
                    ()), "Unknown method=" + methodName);
    }

    @Override
    protected void after(XC_MethodHook.MethodHookParam param) throws Throwable {
        // do nothing
    }
}
