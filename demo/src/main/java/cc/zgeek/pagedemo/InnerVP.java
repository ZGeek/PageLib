package cc.zgeek.pagedemo;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import java.util.Arrays;

import cc.zgeek.pagelib.Annotation.InjectView;
import cc.zgeek.pagelib.Annotation.PageLayout;
import cc.zgeek.pagelib.IPage;
import cc.zgeek.pagelib.Page;
import cc.zgeek.pagelib.PageActivity;
import cc.zgeek.pagelib.ViewPagerPage;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/29 : Create
 */

@PageLayout(R.layout.inner_vp)
public class InnerVP extends Page {

    @InjectView(R.id.layout_tab_container)
    TabLayout tabLayout;
    @InjectView(R.id.vp_container)
    ViewGroup viewGroup;

    public InnerVP(PageActivity pageActivity) {
        super(pageActivity);
    }

    @Override
    public void onViewInited() {

        ViewPagerPage innerPager = new ViewPagerPage(getContext());
        innerPager.addPages(Arrays.asList(
                new SimplePage(getContext()).setName("内0"),
                new SimplePage(getContext()).setName("内1"),
                new SimplePage(getContext()).setName("内2")));

        tabLayout.setupWithViewPager((ViewPager) innerPager.getRootView());
        viewGroup.addView(innerPager.getRootView());
        addPage(innerPager);
    }
}
