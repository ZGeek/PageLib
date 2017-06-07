package cc.zgeek.pagedemo

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.ViewGroup

import java.util.Arrays

import cc.zgeek.pagelib.Annotation.InjectView
import cc.zgeek.pagelib.Annotation.PageLayout
import cc.zgeek.pagelib.Page
import cc.zgeek.pagelib.PageActivity
import cc.zgeek.pagelib.ViewPagerPage

/**
 * Created by flyop.
 * Change History:
 * 2017/1/29 : Create
 */

@PageLayout(R.layout.inner_vp)
class InnerVP(pageActivity: PageActivity) : Page(pageActivity) {

    @InjectView(R.id.layout_tab_container)
    internal lateinit var tabLayout: TabLayout
    @InjectView(R.id.vp_container)
    internal lateinit var viewGroup: ViewGroup

    override fun onViewInitialized(isRestore: Boolean, args: Bundle) {
        super.onViewInitialized(isRestore, args)
        if (!isRestore) {
            val innerPager = ViewPagerPage(context)
            innerPager.addPages(Arrays.asList(
                    SimplePage.newInstance(context),
                    SimplePage.newInstance(context),
                    SimplePage.newInstance(context)))
            //            innerPager.getChildPageAt(0) = .setName("内2")
            //            innerPager.getSubChildPages()
            addPage(innerPager)
        }

        tabLayout.setupWithViewPager(getChildPageAt(0).rootView as ViewPager)
        viewGroup.addView(getChildPageAt(0).rootView)
    }

    //    @Override
    //    public Bundle onSaveInstanceState(boolean isViewInited) {
    //        Bundle bundle = super.onSaveInstanceState(isViewInited);
    //
    //    }
}
