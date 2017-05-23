package cc.zgeek.pagedemo

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup

import java.util.Arrays

import cc.zgeek.pagedemo.util.ToolbarHelper
import cc.zgeek.pagelib.*
import cc.zgeek.pagelib.Annotation.InjectView
import cc.zgeek.pagelib.Annotation.PageLayout

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/23 : Create
 */

@PageLayout(R.layout.swipable_page)
class SwipeableTabPage(pageActivity: PageActivity) : Page(pageActivity) {


    @InjectView(R.id.tb_header_bar)
    internal var toolbar: Toolbar? = null

    @InjectView(R.id.tab_layout)
    internal var tabLayout: TabLayout? = null

    @InjectView(R.id.contenter)
    internal var contenter: ViewGroup? = null
    private var pagerPage: ViewPagerPage? = null

    override fun onViewInitialized(isRestore: Boolean, args: Bundle) {
        super.onViewInitialized(isRestore, args)
        setupToolbar()
        if (!isRestore) {
            val pagerPage = ViewPagerPage(context)

            val page0 = SimplePage.newInstance(context)
            page0.name = "外0"

            val page1 = SimplePage.newInstance(context)
            page1.name = "外1"

            val page2 = SimplePage.newInstance(context)
            page2.name = "外2"


            val page3 = InnerVP(context);
            page3.name = "内部VP"

            pagerPage.addPages(Arrays.asList(page0, page1, page2,page3))
            this.pagerPage = pagerPage
            addPage(pagerPage)
        } else {
            pagerPage = getChildPageAt(0) as ViewPagerPage
        }

        contenter!!.addView(pagerPage!!.rootView)
        setupTabLayout()
    }

    private fun setupToolbar() {
        toolbar!!.title = "PageLib · SwipeableTabPage"

        ToolbarHelper.setNavigationIconEnabled(toolbar!!, true, View.OnClickListener { (context.rootPage as NavigationPage).popPage() })
    }

    private fun setupTabLayout() {
        tabLayout!!.setupWithViewPager(pagerPage!!.rootView as ViewPager)
        tabLayout!!.tabMode = TabLayout.MODE_FIXED

        tabLayout!!.getTabAt(0)!!.setIcon(R.mipmap.ic_launcher)
        tabLayout!!.getTabAt(1)!!.setIcon(R.mipmap.ic_launcher)
        tabLayout!!.getTabAt(2)!!.setIcon(R.mipmap.ic_launcher)
        tabLayout!!.getTabAt(3)!!.setIcon(R.mipmap.ic_launcher)
        rootView.requestLayout()
    }

    override fun onShown() {
        super.onShown()
    }
}
