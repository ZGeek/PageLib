package cc.zgeek.pagelib

import android.widget.FrameLayout

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/14 : Create
 * 一个TabPage，通过setTabContainerLayout可以设置容纳其他Page的ViewGroup,此ViewGroup必须是FrameLayout或者其子类
 * 通过AddPage来新增Tab的子Page
 * 通过switchPage来切换Tab的子Page
 */

abstract class SubSwitchPage(pageActivity: PageActivity) : SwitchPage(pageActivity) {

    internal var tabContainerLayout: FrameLayout? = null

    fun setTabContainerLayout(tabContainerLayout: FrameLayout) {
        this.tabContainerLayout = tabContainerLayout
    }

    override fun currentContiner(): FrameLayout {
        if (!isViewInitialized)
            super.rootView
        return tabContainerLayout ?: throw RuntimeException("tabContainerLayout is null")
    }
}
