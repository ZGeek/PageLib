package cc.zgeek.page.Lib.Utils;

import android.content.res.Resources;
import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import cc.zgeek.page.Lib.Annotation.DisableInjectView;
import cc.zgeek.page.Lib.Annotation.InjectView;
import cc.zgeek.page.Lib.Annotation.InjectViewByName;
import cc.zgeek.page.Lib.ViewWrapper;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/14 : Create
 */

public class AnnotationUtils {


    public static void injectView(ViewWrapper wrapper) throws IllegalAccessException {
        if (wrapper == null) {
            throw new IllegalArgumentException("ViewWrapper can not be NULL");
        }
        if (wrapper.getRootView() == null) {
            throw new IllegalArgumentException("getRootView() can not be NULL");
        }
        Class clazz = wrapper.getClass();


        do {
            if (!clazz.isAnnotationPresent(DisableInjectView.class)) {
                Field fields[] = clazz.getDeclaredFields();
                for (Field field : fields) {
                    Annotation[] annotations = field.getAnnotations();

                    if (annotations == null || annotations.length == 0) {
                        continue;
                    }

                    for (int j = 0; j < annotations.length; ++j) {
                        Annotation anno = annotations[j];

                        if (InjectView.class.isAssignableFrom(anno.getClass())) {
                            int resId = ((InjectView) anno).value();
                            field.setAccessible(true);
                            field.set(wrapper, wrapper.findViewById(resId));
                            break;
                        } else if (InjectViewByName.class.isAssignableFrom(anno.getClass())) {
                            field.setAccessible(true);
                           field.set(wrapper, wrapper.findViewByName(((InjectViewByName) anno).value()));
                            break;
                        }
                    }
                }

            }
        } while ((clazz = clazz.getSuperclass()) != ViewWrapper.class);
    }
}
