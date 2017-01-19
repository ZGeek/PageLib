package cc.zgeek.pagedemo;


import android.support.v4.util.LongSparseArray;
import android.support.v4.view.ViewPager;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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

    List<IPage> pages = new LinkedList<>();

    @Override
    public IPage initRootPage() {
        pages.add(new SimplePage(this, 0));
        pages.add(new SimplePage(this, 1));
        pages.add(new SimplePage(this, 2));
        pages.add(new SimplePage(this, 3));
        pages.add(new SimplePage(this, 4));
        pages.add(new SimplePage(this, 5));
        pages.add(new SimplePage(this, 6));
        pages.add(new SimplePage(this, 7));
        pages.add(new SimplePage(this, 8));
        pages.add(new SimplePage(this, 9));
        pages.add(new SimplePage(this, 10));


        final ViewPagerPage pagerPage = new ViewPagerPage(this);


        postDelayed(new Runnable() {
            @Override
            public void run() {
                pagerPage.addPages(pages);
            }
        }, 3000);
        postDelayed(new Runnable() {
            @Override
            public void run() {
               pagerPage.switchToPage(2);
            }
        }, 5000);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                pagerPage.removePage(pages.get(2));
            }
        }, 8000);
        return pagerPage;




    }

}
