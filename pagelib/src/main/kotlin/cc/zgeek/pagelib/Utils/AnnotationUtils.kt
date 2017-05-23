package cc.zgeek.pagelib.Utils


import android.content.res.Resources
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v4.util.ArrayMap
import android.support.v4.util.LongSparseArray
import android.util.Log
import android.view.View
import java.lang.reflect.Field

import cc.zgeek.pagelib.Annotation.DisableInjectView
import cc.zgeek.pagelib.Annotation.InjectView
import cc.zgeek.pagelib.Annotation.InjectViewByName
import cc.zgeek.pagelib.Annotation.PageLayout
import cc.zgeek.pagelib.Annotation.PageLayoutName
import cc.zgeek.pagelib.ViewWrapper

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/14 : Create
 */

object AnnotationUtils {


    private val filedCache = ArrayMap<String, LongSparseArray<Field>>()
    private val layoutCache = ArrayMap<String, Int>()

    fun injectView(wrapper: ViewWrapper?) {
        if (wrapper == null) {
            throw IllegalArgumentException("ViewWrapper can not be NULL")
        }
        var clazz: Class<*> = wrapper.javaClass

        while (clazz != ViewWrapper::class.java) {
            var fields: LongSparseArray<Field>? = filedCache[clazz.name]
            if (fields == null) {
                fields = findAllFileds(clazz, wrapper.resources, wrapper.packageName)
                filedCache.put(clazz.name, fields)
            }

            for (i in 0..fields.size() - 1) {
                @IdRes val resId = fields.keyAt(i)
                try {
                    val view = wrapper.findViewById(resId.toInt())
                    if (view == null && PageLibDebugUtils.isDebug) {
                        Log.w(PageLibDebugUtils.TAG, wrapper.javaClass.name + " with property (" + fields.valueAt(i).name + ") will be NULL")
                    }
                    fields.valueAt(i).set(wrapper, view)
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }

            }
            clazz = clazz.superclass
        }
    }

    internal fun findAllFileds(clazz: Class<*>, res: Resources, packageName: String): LongSparseArray<Field> {
        if (!clazz.isAnnotationPresent(DisableInjectView::class.java)) {
            val fieldLongSparseArray = LongSparseArray<Field>(3)
            val fields = clazz.declaredFields
            for (field in fields) {
                val annotations = field.annotations

                if (annotations == null || annotations.size == 0) {
                    continue
                }

                for (j in annotations.indices) {
                    val anno = annotations[j]
                    if (InjectView::class.java.isAssignableFrom(anno.javaClass)) {
                        val resId = (anno as InjectView).value
                        field.isAccessible = true
                        fieldLongSparseArray.put(resId.toLong(), field)
                        break
                    } else if (InjectViewByName::class.java.isAssignableFrom(anno.javaClass)) {
                        val resId = res.getIdentifier((anno as InjectViewByName).value, "id", packageName)
                        field.isAccessible = true
                        fieldLongSparseArray.put(resId.toLong(), field)
                        break
                    }
                }
            }

            return fieldLongSparseArray

        } else {
            return LongSparseArray(0)
        }
    }

    fun injectLayout(wrapper: ViewWrapper): View? {
        var clazz: Class<*> = wrapper.javaClass
        while (clazz != ViewWrapper::class.java) {
            val id = findLayoutId(clazz, wrapper.resources, wrapper.packageName)
            layoutCache.put(clazz.name, id)
            if (id > 0) {
                return wrapper.layoutInflater.inflate(id, null, false)
            }
            clazz = clazz.superclass
        }
        return null
    }


    @LayoutRes
    fun findLayoutId(clazz: Class<*>, res: Resources, packageName: String): Int {

        val cacheId = layoutCache[clazz.name]
        if (cacheId != null)
            return cacheId

        var layoutId = -1

        if (clazz.getAnnotation(PageLayout::class.java) != null)
            layoutId = clazz.getAnnotation(PageLayout::class.java).value
        if (clazz.getAnnotation(PageLayoutName::class.java) != null) {
            val id = res.getIdentifier(clazz.getAnnotation(PageLayoutName::class.java).value, "id", packageName)
            if (id > 0)
                layoutId = id
        }
        layoutCache.put(clazz.name, layoutId)
        return layoutId
    }
}
