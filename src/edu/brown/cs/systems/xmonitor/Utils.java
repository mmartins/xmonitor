package edu.brown.cs.systems.xmonitor;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * User: martins
 * Date: 11/11/13
 * Time: 9:59 PM
 */
public class Utils {

    private RandomAccessFile memMap;
    private MappedByteBuffer out;
    private static int MEM_MAP_SIZE = 20 * 1024 * 1024;
    static Utils singleton;

    protected Utils() {
        try {
            File file = new File(Environment.getDataDirectory(),
                    "monitor-events");
            // WARNING: Creates security issue!
            file.setWritable(true, false);
            memMap = new RandomAccessFile(file, "rw");
            out = memMap.getChannel().map(FileChannel.MapMode.READ_WRITE, 0,
                    MEM_MAP_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Utils getInstance() {
        if (singleton == null)
            singleton = new Utils();

        return singleton;
    }

    public void shareEvent(String stringArg, int ... intArgs) {
        ByteBuffer b = ByteBuffer.allocate(128);
        for (int arg: intArgs) {
            b.putInt(arg);
        }

        // TODO: Make String field fixed
        //if (stringArg != null)
        //    b.put(stringArg.getBytes());
        if (out != null)
            out.put(b.array());
    }
}
