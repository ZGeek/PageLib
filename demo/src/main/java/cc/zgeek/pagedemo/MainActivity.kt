package cc.zgeek.pagedemo


import cc.zgeek.pagelib.IPage
import cc.zgeek.pagelib.NavigationPage
import cc.zgeek.pagelib.PageActivity
import cc.zgeek.pagelib.Utils.PageLibDebugUtis

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/13 : Create
 */

class MainActivity : PageActivity() {


    public override fun initRootPage(): IPage {
        //        final ViewPagerPage pageA = new ViewPagerPage(this);
        //        pageA.addPages(
        //                new SimplePage(this, 0).setPageName("A"),
        //                new SimplePage(this, 1).setPageName("A"),
        //                new SimplePage(this, 2).setPageName("A"),
        //                new SimplePage(this, 3).setPageName("A"));
        //
        //        ViewPagerPage pageB = new ViewPagerPage(this);
        //        pageB.addPages(
        //                new SimplePage(this, 0).setPageName("B"),
        //                new SimplePage(this, 1).setPageName("B"),
        //                new SimplePage(this, 2).setPageName("B"),
        //                new SimplePage(this, 3).setPageName("B"));
        //
        //        NavigationPage navigationPage =  new NavigationPage(this, pageA);
        //        navigationPage.pushPage(pageB);
        //
        //        postDelayed(new Runnable() {
        //            @Override
        //            public void run() {
        //                pageA.switchToPage(2);
        //            }
        //        }, 5000);
        PageLibDebugUtis.isDebug = true
        val page = NavigationPage(this)
        page.pushPage(HomePage(this))
        page.name = "Root Page"
        return page
        //        return new NavigationPage(this, new HomePage(this));
        //        return new NavigationPage(this, new SimplePage(this));
    }


}
