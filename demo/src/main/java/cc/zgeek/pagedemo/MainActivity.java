package cc.zgeek.pagedemo;




import cc.zgeek.pagelib.IPage;
import cc.zgeek.pagelib.NavigationPage;
import cc.zgeek.pagelib.PageActivity;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/13 : Create
 */

public class MainActivity extends PageActivity {


    @Override
    public IPage initRootPage() {
        return new NavigationPage(this, new HomePage(this));
    }
}
