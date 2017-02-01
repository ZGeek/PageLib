package cc.zgeek.pagedemo;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import cc.zgeek.pagedemo.util.ToolbarHelper;
import cc.zgeek.pagelib.Annotation.InjectView;
import cc.zgeek.pagelib.Annotation.PageLayout;
import cc.zgeek.pagelib.IPage;
import cc.zgeek.pagelib.NavigationPage;
import cc.zgeek.pagelib.PageActivity;
import cc.zgeek.pagelib.SubSwitchPage;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/29 : Create
 */

@PageLayout(R.layout.swipable_page)
public class SwitchTabPage extends SubSwitchPage implements TabLayout.OnTabSelectedListener {

    @InjectView(R.id.tb_header_bar)
    Toolbar toolbar;
    @InjectView(R.id.tab_layout)
    TabLayout tabLayout;
    @InjectView(R.id.contenter)
    FrameLayout frameLayout;

    public SwitchTabPage(PageActivity pageActivity) {
        super(pageActivity);
    }

    @Override
    public void onViewInited(boolean isRestore, Bundle ars) {
        setTabContainerLayout(frameLayout);
        super.onViewInited(isRestore, ars);
        toolbar.setTitle("PageLib · SwitchTabPage");
        ToolbarHelper.setNavigationIconEnabled(toolbar, true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavigationPage) getContext().getRootPage()).popPage();
            }
        });
        if (!isRestore) {
            addPage(SimplePage.newInstance(getContext()).setName("TAB0"));
            addPage(SimplePage.newInstance(getContext()).setName("TAB1"));
            addPage(SimplePage.newInstance(getContext()).setName("TAB2"));
            addPage(SimplePage.newInstance(getContext()).setName("TAB3"));
        }
        for (int i = 0; i < getChildPageCount(); i++) {
            TabLayout.Tab tab =
                    tabLayout.newTab().setIcon(R.mipmap.ic_launcher).setText(getChildPageAt(i).getName()).setTag(getChildPageAt(i));
            tabLayout.addTab(tab);
        }
        tabLayout.addOnTabSelectedListener(this);
        syncTabIndex();

//        switchToPage(0);
    }

    protected void syncTabIndex() {
        int index = getShowIndex();
        if (index >= 0 && index < getChildPageCount() && tabLayout.getSelectedTabPosition() != index) {
            tabLayout.getTabAt(index).select();
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switchToPage((IPage) tab.getTag());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
