package cc.zgeek.pagelib.Annotation;

import android.support.annotation.IdRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/15 : Create
 */


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InjectViewByName {
    String value();
}
