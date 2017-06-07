package cc.zgeek.pagelib.Annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class PageLayoutName(// this is used in Library Projects, because in Library project
        // resource ids(R.id.*) will be non-final, we cannot use the id
        // directly, we have to use string representation of the id, and
        // PageLib will use res.getIdentifier(value(), "id", packageName)
        // to get the id
        val value: String)
