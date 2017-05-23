package cc.zgeek.pagelib

import android.content.Intent
import android.view.KeyEvent
import android.view.View

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/18 : Create
 */

//每次只有一个子Page激活的容器
abstract class SingleActivePage(pageActivity: PageActivity) : Page(pageActivity) {

    abstract val activiePage: IPage?


    override fun onShow() {
        val page = activiePage
        page?.onShow()
    }

    override fun onShown() {
        val page = activiePage
        if (page != null) {
            page.rootView.bringToFront()
            page.rootView.visibility = View.VISIBLE
            page.rootView.requestFocus()
            page.onShown()
        }
    }

    override fun onHide() {
        val page = activiePage
        page?.onHide()
    }

    override fun onHidden() {
        val page = activiePage
        page?.onHidden()
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val page = activiePage
        return page != null && page.onKeyDown(keyCode, event)
    }

    override fun onMenuPressed(): Boolean {
        val page = activiePage
        return page != null && page.onMenuPressed()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        val page = activiePage
        return page != null && page.onKeyUp(keyCode, event)
    }

    override fun onBackPressed(): Boolean {
        val page = activiePage
        return page != null && page.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        val page = activiePage
        page?.onActivityResult(requestCode, resultCode, data)
    }

    override fun isChildPageActive(child: IPage?): Boolean {
        return child != null && activiePage === child
    }
}
