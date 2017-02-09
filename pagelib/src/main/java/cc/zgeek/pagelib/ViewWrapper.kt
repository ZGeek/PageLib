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
    @Volatile protected var rootView: View? = null
        get() {
            if (this.rootView == null) {
                synchronized(this) {
                    if (this.rootView == null) {
                        rootView = AnnotationUtils.injectLayout(this)
                        if (rootView == null) {
                            throw IllegalStateException("Neigher " + javaClass.name + " nor it's supper class Annotation with PageLaout or PageLayoutName")
                        }
                        AnnotationUtils.injectView(this)
                    }
                }
            }
            return this.rootView;
        }

    init {
        if (PACKAGE_NAME == null)
            PACKAGE_NAME = context.packageName
    }

//    open fun getRootView(): View {
//        rootView = AnnotationUtils.injectLayout(this)
//        if (rootView == null) {
//            throw IllegalStateException("Neigher " + javaClass.name + " nor it's supper class Annotation with PageLaout or PageLayoutName")
//        }
//        AnnotationUtils.injectView(this)
//
//        return rootView
//    }

    fun findViewById(id: Int): View {
        return rootView!!.findViewById(id)
    }

    fun findViewByName(name: String): View {
        val id = resources.getIdentifier(name, "id", PACKAGE_NAME)
        return rootView!!.findViewById(id)
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
        get() = PACKAGE_NAME!!

    val layoutInflater: LayoutInflater
        get() = context.layoutInflater

    companion object {
        private var PACKAGE_NAME: String? = null
    }
}
