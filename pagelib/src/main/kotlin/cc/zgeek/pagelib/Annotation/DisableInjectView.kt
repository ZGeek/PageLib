package cc.zgeek.pagelib.Annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/15 : Create
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class DisableInjectView