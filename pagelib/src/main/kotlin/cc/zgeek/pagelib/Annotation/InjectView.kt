package cc.zgeek.pagelib.Annotation

import android.support.annotation.IdRes
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/14 : Create
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class InjectView(@IdRes val value: Int)
