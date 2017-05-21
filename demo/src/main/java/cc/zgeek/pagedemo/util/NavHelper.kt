package cc.zgeek.pagedemo.util

import cc.zgeek.pagelib.IPage
import cc.zgeek.pagelib.NavigationPage

/**
 * Created by flyop.
 * Change History:
 * 2017/1/30 : Create
 */

object NavHelper {
    fun findFirstNav(page: IPage): NavigationPage? {
        var parent: IPage? = page
        while (parent != null) {
            if (parent is NavigationPage)
                return parent
            parent = parent.parentPage
        }
        return null
    }
}
