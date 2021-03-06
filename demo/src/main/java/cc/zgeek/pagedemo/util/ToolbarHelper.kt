package cc.zgeek.pagedemo.util

import android.graphics.drawable.Drawable
import android.support.annotation.MenuRes
import android.support.v7.widget.Toolbar
import android.view.View

import cc.zgeek.pagedemo.R


/**
 * Created by neevek on 8/13/16.
 */
object ToolbarHelper {
    fun setupMenu(toolbar: Toolbar,
                  @MenuRes menuResId: Int,
                  listener: Toolbar.OnMenuItemClickListener?) {
        if (menuResId > 0) {
            toolbar.inflateMenu(menuResId)
            if (listener != null) {
                toolbar.setOnMenuItemClickListener(listener)
            }
        }
    }

    fun setNavigationIconEnabled(toolbar: Toolbar,
                                 enable: Boolean,
                                 listener: View.OnClickListener?) {
        val navIcon = toolbar.navigationIcon
        if (enable) {
            if (navIcon != null) {
                navIcon.setVisible(true, false)
            } else {
                toolbar.setNavigationIcon(R.drawable.btn_nav_back)
                if (listener != null) {
                    toolbar.setNavigationOnClickListener(listener)
                }
            }
        } else navIcon?.setVisible(false, false)
    }
}
