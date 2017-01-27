package cc.zgeek.pagedemo;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import java.util.Arrays;

import cc.zgeek.pagelib.Annotation.InjectView;
import cc.zgeek.pagelib.Annotation.PageLayout;
import cc.zgeek.pagelib.Page;
import cc.zgeek.pagelib.PageActivity;
import cc.zgeek.pagelib.ViewPagerPage;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/23 : Create
 */

@PageLayout(R.layout.swipable_page)
public class SwipableTabPage extends Page {

    @InjectView(R.id.tab_layout)
    TabLayout tabLayout;

    @InjectView(R.id.contenter)
    ViewGroup contenter;
    private ViewPagerPage pagerPage;


    public SwipableTabPage(PageActivity pageActivity) {
        super(pageActivity);
    }

    @Override
    public void onViewInited() {
        super.onViewInited();
        pagerPage = new ViewPagerPage(mContext);
        ViewPagerPage innerPager = new ViewPagerPage(mContext);
        innerPager.addPages(Arrays.asList(
                new SimplePage(mContext, 0),
                new SimplePage(mContext, 1),
                new SimplePage(mContext, 2)));

        pagerPage.addPages(Arrays.asList(
                new SimplePage(mContext, 0),
                new SimplePage(mContext, 1),
                new SimplePage(mContext, 2),
                innerPager));
        addPage(pagerPage);
        contenter.addView(pagerPage.getRootView());
        setupTabLayout();
    }

    private void setupTabLayout() {
        tabLayout.setupWithViewPager((ViewPager) pagerPage.getRootView());
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        tabLayout.getTabAt(0).setIcon(R.mipmap.ic_launcher);
        tabLayout.getTabAt(1).setIcon(R.mipmap.ic_launcher);
        tabLayout.getTabAt(2).setIcon(R.mipmap.ic_launcher);
        tabLayout.getTabAt(3).setIcon(R.mipmap.ic_launcher);
        getRootView().requestLayout();
    }

    @Override
    public void onShown() {
        super.onShown();
    }
}
