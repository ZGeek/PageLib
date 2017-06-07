package cc.zgeek.pagelib

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View


/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/10 : Create
 */

interface IPage {
    /***
     * When the return key press callback
     * @return Whether consume this  point ，若消费则不继续往下传递
     */
    fun onBackPressed(): Boolean

    /***
     * same as Activity#onActivityResult(int, int, Intent)
     * @param requestCode
     * *
     * @param resultCode
     * *
     * @param data
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent)

    fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean

    fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean

    //    boolean onTouchEvent(MotionEvent event);

    fun onConfigurationChanged(newConfig: Configuration)

    /***
     * Get the parent page of  current page
     * @return
     */
    var parentPage: IPage?


    val context: PageActivity
    fun isChildPageActive(child: IPage?): Boolean

    /***
     * Get the root view of this Page
     * @return
     */
    val rootView: View

    fun onSaveInstanceState(isViewInited: Boolean): Bundle
    var args: Bundle


    /***
     * getChildPageAt 得到在index位置处的子page
     */
    fun getChildPageAt(index: Int): IPage

    fun getChildPageIndex(page: IPage): Int
    fun getSubChildPages(beginIndex: Int, count: Int): List<IPage>
    fun getChildPages(): List<IPage>
    val childPageCount: Int

    /***
     * 当视图被创建后调用，每个page只会被调用一次
     */
    fun onViewInit(view:View, isRestore: Boolean, args: Bundle)

    /***
     * 用于判断视图是否被初始化，
     * @return
     */
    val isViewInitialized: Boolean


    /***
     * Life Cycle Relation
     * 将要显示时的回调，对于NavigationPage，其调用时机是在转场动画开始时调用，
     * 对于viewpagerpage，其和onshow的调用时机几乎一致，同样的道理同onHide与onHidden
     * 对于Page的显示与隐藏，其调用的顺序为：
     * willShowPage.onShow()->willHidePage.onHide()->willShowPage.onShown()->willHidePage.onHidden()-> willHidePage.onDestroy()
     * 其中willHidePage.onDestroy()的调用取决于page的view是否初始化，并且page是否真正的从childPageList中移除，而非简单的隐藏
     */
    fun onShow()

    //view已经显示时的回调
    fun onShown()

    //将要隐藏时的回调
    fun onHide()

    //已经隐藏时的回调
    fun onHidden()

    //page将被销毁时调用，如果此page的isViewInited返回false，则不会调用onDestroy,在调用onDestroy时，根据getParent不一定能找到其父Page
    fun onDestroy()

    fun onMenuPressed(): Boolean

    //收到系统的内存警告时调用
    fun onLowMemory()

    var name: String

    fun setPageName(name: String): IPage

    fun isPageActive():Boolean{
        if (!this.context.isActive) {
            return false
        }
        val root = this.context.rootPage
        var child = this
        if (child === root)
            return true
        var parent: IPage? = this.parentPage

        while (parent != null) {
            val isActive = parent.isChildPageActive(child)
            if (!isActive)
                return false
            if (parent === root)
                return true
            child = parent
            parent = parent.parentPage
        }
        return false
    }
}
