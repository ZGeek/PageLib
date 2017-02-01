package cc.zgeek.pagelib.Utils;

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/20 : Create
 */

public class PageLibDebugUtis {
    private static boolean debug = false;
    public static final String TAG = "PAGE_LIB";

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        PageLibDebugUtis.debug = debug;
    }
}
