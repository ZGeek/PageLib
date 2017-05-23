package cc.zgeek.pagelib.Annotation

import android.support.annotation.IdRes
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/15 : Create
 */


@Retention(RetentionPolicy.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class InjectViewByName(val value: String)
