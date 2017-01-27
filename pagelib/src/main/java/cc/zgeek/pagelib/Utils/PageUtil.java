package cc.zgeek.pagelib.Utils;

import cc.zgeek.pagelib.IPage;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/27 : Create
 */

public class PageUtil {

    public static boolean isPageActive(IPage page) {
        if (!page.getContext().isActive()) {
            return false;
        }
        IPage root = page.getContext().getRootPage();
        IPage child = page;
        if (child == root)
            return true;
        IPage parent = page.getParentPage();

        while (parent != null) {
            boolean isActive = parent.isChildPageActive(child);
            if (!isActive)
                return false;
            if (parent == root)
                return isActive;
            child = parent;
            parent = parent.getParentPage();
        }
        return false;
    }
}
