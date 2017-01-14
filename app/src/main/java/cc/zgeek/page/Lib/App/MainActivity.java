package cc.zgeek.page.Lib.App;


import cc.zgeek.page.Lib.IPage;
import cc.zgeek.page.Lib.NavigationPage;
import cc.zgeek.page.Lib.PageActivity;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/13 : Create
 */

public class MainActivity extends PageActivity {

    @Override
    public IPage initRootPage() {
        NavigationPage navigationPage =  new NavigationPage(this, new SimplePage(this));
        return navigationPage;
    }

}
