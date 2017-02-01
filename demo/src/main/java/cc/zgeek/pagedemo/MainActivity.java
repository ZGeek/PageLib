package cc.zgeek.pagedemo;


import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.util.Printer;

import cc.zgeek.pagelib.IPage;
import cc.zgeek.pagelib.NavigationPage;
import cc.zgeek.pagelib.PageActivity;
import cc.zgeek.pagelib.Utils.PageLibDebugUtis;
import cc.zgeek.pagelib.ViewPagerPage;

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/13 : Create
 */

public class MainActivity extends PageActivity {


    @Override
    public IPage initRootPage() {
//        final ViewPagerPage pageA = new ViewPagerPage(this);
//        pageA.addPages(
//                new SimplePage(this, 0).setName("A"),
//                new SimplePage(this, 1).setName("A"),
//                new SimplePage(this, 2).setName("A"),
//                new SimplePage(this, 3).setName("A"));
//
//        ViewPagerPage pageB = new ViewPagerPage(this);
//        pageB.addPages(
//                new SimplePage(this, 0).setName("B"),
//                new SimplePage(this, 1).setName("B"),
//                new SimplePage(this, 2).setName("B"),
//                new SimplePage(this, 3).setName("B"));
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
        PageLibDebugUtis.setDebug(true);
        NavigationPage page = new NavigationPage(this);
        page.pushPage(new HomePage(this));
        page.setName("Root Page");
        return page;
//        return new NavigationPage(this, new HomePage(this));
//        return new NavigationPage(this, new SimplePage(this));
    }


}
