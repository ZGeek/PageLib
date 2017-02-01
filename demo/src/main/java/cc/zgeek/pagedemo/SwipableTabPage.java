package cc.zgeek.pagedemo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Arrays;

import cc.zgeek.pagedemo.util.ToolbarHelper;
import cc.zgeek.pagelib.Annotation.InjectView;
import cc.zgeek.pagelib.Annotation.PageLayout;
import cc.zgeek.pagelib.NavigationPage;
import cc.zgeek.pagelib.Page;
import cc.zgeek.pagelib.PageActivity;
import cc.zgeek.pagelib.ViewPagerPage;

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/23 : Create
 */

@PageLayout(R.layout.swipable_page)
public class SwipableTabPage extends Page {


    @InjectView(R.id.tb_header_bar)
    Toolbar toolbar;

    @InjectView(R.id.tab_layout)
    TabLayout tabLayout;

    @InjectView(R.id.contenter)
    ViewGroup contenter;
    private ViewPagerPage pagerPage;


    public SwipableTabPage(PageActivity pageActivity) {
        super(pageActivity);
    }

    @Override
    public void onViewInited(boolean isRestore, Bundle args) {
        super.onViewInited(isRestore, args);
        setupToolbar();
        if (!isRestore) {
            pagerPage = new ViewPagerPage(getContext());

            pagerPage.addPages(Arrays.asList(
                    SimplePage.newInstance(getContext()).setName("外0"),
                    SimplePage.newInstance(getContext()).setName("外1"),
                    SimplePage.newInstance(getContext()).setName("外2"),
                    new InnerVP(getContext()).setName("内部VP")));
            addPage(pagerPage);
        } else {
            pagerPage = (ViewPagerPage) getChildPageAt(0);
        }

        contenter.addView(pagerPage.getRootView());
        setupTabLayout();
    }

    private void setupToolbar() {
        toolbar.setTitle("PageLib · SwipableTabPage");

        ToolbarHelper.setNavigationIconEnabled(toolbar, true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavigationPage) getContext().getRootPage()).popPage();
            }
        });
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
