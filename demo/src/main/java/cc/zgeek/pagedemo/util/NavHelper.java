package cc.zgeek.pagedemo.util;

import cc.zgeek.pagelib.IPage;
import cc.zgeek.pagelib.NavigationPage;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/30 : Create
 */

public class NavHelper {
    public static NavigationPage findFirstNav(IPage page){
        IPage parent = page;
        while (parent != null){
            if(parent instanceof NavigationPage)
                return (NavigationPage) parent;
            parent = parent.getParentPage();
        }
        return null;
    }
}
