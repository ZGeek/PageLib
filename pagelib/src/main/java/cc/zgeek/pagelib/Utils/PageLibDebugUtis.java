package cc.zgeek.pagelib.Utils;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/20 : Create
 */

public class PageLibDebugUtis {
    static boolean debug = false;

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        PageLibDebugUtis.debug = debug;
    }
}
