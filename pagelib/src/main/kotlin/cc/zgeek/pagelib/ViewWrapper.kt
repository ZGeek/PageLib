package cc.zgeek.pagelib

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View

import cc.zgeek.pagelib.Utils.AnnotationUtils

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/10 : Create
 */

abstract class ViewWrapper(val context: PageActivity) {

    val isViewInitialized: Boolean
        get() = _rootView != null


    protected var _rootView:View? = null
    open val rootView: View
    get() {
        if(_rootView == null){
            _rootView = AnnotationUtils.injectLayout(this@ViewWrapper) ?: throw IllegalStateException("Neither " + javaClass.name + " nor it's supper class Annotation with PageLaout or PageLayoutName")
            AnnotationUtils.injectView(this@ViewWrapper)
        }
        return checkNotNull(_rootView)
    }

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
