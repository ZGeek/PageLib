package cc.zgeek.pagelib

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View

import cc.zgeek.pagelib.Utils.AnnotationUtils
import cc.zgeek.pagelib.Utils.myLazy

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/10 : Create
 */

abstract class ViewWrapper(val context: PageActivity) {

    open val isViewInitialized: Boolean
        get() = lazy_rootView.isInitialized()

    private val lazy_rootView = myLazy({
        val tmpView = AnnotationUtils.injectLayout(this@ViewWrapper) ?: throw IllegalStateException("Neither " + javaClass.name + " nor it's supper class Annotation with PageLaout or PageLayoutName")

        return@myLazy tmpView
    }, {
        AnnotationUtils.injectView(this@ViewWrapper)
    })

    open val rootView: View by this@ViewWrapper.lazy_rootView

    init {
        if (PACKAGE_NAME == null)
            PACKAGE_NAME = context.packageName
    }


    fun findViewById(id: Int): View {
        return rootView.findViewById(id)
    }

    fun findViewByName(name: String): View {
        val id = resources.getIdentifier(name, "id", PACKAGE_NAME)
        return rootView.findViewById(id)
    }

    fun getString(resId: Int): String {
        return context.getString(resId)
    }

    fun getString(resId: Int, vararg args: Any): String {
        return context.getString(resId, *args)
    }

    val resources: Resources
        get() = context.resources

    val packageName: String
        get() = checkNotNull(PACKAGE_NAME)

    val layoutInflater: LayoutInflater
        get() = context.layoutInflater

    companion object {
        private var PACKAGE_NAME: String? = null
    }
}
