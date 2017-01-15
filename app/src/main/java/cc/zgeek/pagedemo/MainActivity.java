package cc.zgeek.pagedemo;


import cc.zgeek.pagelib.IPage;
import cc.zgeek.pagelib.NavigationPage;
import cc.zgeek.pagelib.PageActivity;
import cc.zgeek.pagelib.ViewPagerPage;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/13 : Create
 */

public class MainActivity extends PageActivity {

    @Override
    public IPage initRootPage() {
        NavigationPage navigationPage =  new NavigationPage(this, new SimplePage(this));
        navigationPage.pushPage(new SimplePage(this));
        navigationPage.pushPage(new SimplePage(this));
        navigationPage.pushPage(new SimplePage(this));
        navigationPage.pushPage(new SimplePage(this));
        navigationPage.pushPage(new SimplePage(this));
        navigationPage.pushPage(new SimplePage(this));
        return navigationPage;

//        ViewPagerPage pagerPage = new ViewPagerPage(this);
//        pagerPage.addPage(new SimplePage(this));
//        pagerPage.addPage(new SimplePage(this));
//        pagerPage.addPage(new SimplePage(this));
//        pagerPage.addPage(new SimplePage(this));
//        return pagerPage;
    }

}
