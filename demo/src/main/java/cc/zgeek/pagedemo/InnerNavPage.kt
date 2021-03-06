package cc.zgeek.pagedemo

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout

import cc.zgeek.pagelib.Annotation.InjectView
import cc.zgeek.pagelib.Annotation.PageLayout
import cc.zgeek.pagelib.NavigationPage
import cc.zgeek.pagelib.Page
import cc.zgeek.pagelib.PageActivity

/**
 * Created by flyop.
 * Change History:
 * 2017/1/30 : Create
 */

@PageLayout(R.layout.combin_page)
class InnerNavPage(pageActivity: PageActivity) : Page(pageActivity) {

    @InjectView(R.id.content)
    internal lateinit var frameLayout: FrameLayout

    override fun onViewInit(view: View, isRestore: Boolean, args: Bundle) {
        super.onViewInit(view, isRestore, args)
        if (!isRestore) {
            val nav = NavigationPage(context)
            nav.pushPage(SimplePage.newInstance(context))
            addPage(nav)
        }
        frameLayout.addView(getChildPageAt(0).rootView)
    }

    //    @Override
    //    public Bundle onSaveInstanceState(boolean isViewInited) {
    //        return null;
    //    }
}
