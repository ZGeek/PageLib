package cc.zgeek.pagedemo

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.FrameLayout

import cc.zgeek.pagedemo.util.ToolbarHelper
import cc.zgeek.pagelib.Annotation.InjectView
import cc.zgeek.pagelib.Annotation.PageLayout
import cc.zgeek.pagelib.IPage
import cc.zgeek.pagelib.NavigationPage
import cc.zgeek.pagelib.PageActivity
import cc.zgeek.pagelib.SubSwitchPage

/**
 * Created by flyop.
 * Change History:
 * 2017/1/29 : Create
 */

@PageLayout(R.layout.swipable_page)
class SwitchTabPage(pageActivity: PageActivity) : SubSwitchPage(pageActivity), TabLayout.OnTabSelectedListener {

    @InjectView(R.id.tb_header_bar)
    internal var toolbar: Toolbar? = null
    @InjectView(R.id.tab_layout)
    internal var tabLayout: TabLayout? = null
    @InjectView(R.id.contenter)
    internal var frameLayout: FrameLayout? = null

    override fun onViewInitialized(isRestore: Boolean, ars: Bundle) {
        setTabContainerLayout(frameLayout!!)
        super.onViewInitialized(isRestore, ars)
        toolbar!!.title = "PageLib · SwitchTabPage"
        ToolbarHelper.setNavigationIconEnabled(toolbar!!, true, View.OnClickListener{ (context.rootPage as NavigationPage).popPage() })
        if (!isRestore) {
            addPage(SimplePage.newInstance(context).setPageName("TAB0"))
            addPage(SimplePage.newInstance(context).setPageName("TAB1"))
            addPage(SimplePage.newInstance(context).setPageName("TAB2"))
            addPage(SimplePage.newInstance(context).setPageName("TAB3"))
        }
        for (i in 0..childPageCount - 1) {
            val tab = tabLayout!!.newTab().setIcon(R.mipmap.ic_launcher).setText(getChildPageAt(i).name).setTag(getChildPageAt(i))
            tabLayout!!.addTab(tab)
        }
        tabLayout!!.addOnTabSelectedListener(this)
        syncTabIndex()

        //        switchToPage(0);
    }

    protected fun syncTabIndex() {
        val index = showIndex
        if (index >= 0 && index < childPageCount && tabLayout!!.selectedTabPosition != index) {
            tabLayout!!.getTabAt(index)!!.select()
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        switchToPage((tab.tag as IPage?)!!)
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {

    }

    override fun onTabReselected(tab: TabLayout.Tab) {

    }
}
