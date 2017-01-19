package cc.zgeek.pagedemo;

import cc.zgeek.pagelib.IPage;
import cc.zgeek.pagelib.NavigationPage;
import cc.zgeek.pagelib.PageActivity;

/**
 * Created by flyop(YZQ) on 2017/1/19.
 */

public class NavigationActivity extends PageActivity {
    @Override
    protected IPage initRootPage() {
        NavigationPage navigationPage = new NavigationPage(this, new SimNavChildView(this));
        return navigationPage;
    }
}
