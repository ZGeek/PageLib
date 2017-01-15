package cc.zgeek.pagelib.Utils;


import android.content.res.Resources;
import android.support.annotation.IdRes;
import android.support.v4.util.LongSparseArray;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import cc.zgeek.pagelib.Annotation.DisableInjectView;
import cc.zgeek.pagelib.Annotation.InjectView;
import cc.zgeek.pagelib.Annotation.InjectViewByName;
import cc.zgeek.pagelib.ViewWrapper;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/14 : Create
 */

public class AnnotationUtils {


    private static Map<String, LongSparseArray<Field>> filedCache = new HashMap<>();

    public static void injectView(ViewWrapper wrapper) {
        if (wrapper == null) {
            throw new IllegalArgumentException("ViewWrapper can not be NULL");
        }
        if (wrapper.getRootView() == null) {
            throw new IllegalArgumentException("getRootView() can not be NULL");
        }
        Class clazz = wrapper.getClass();

        do {
            LongSparseArray<Field> fields = filedCache.get(clazz.getName());
            if (fields == null) {
                fields = findAllFileds(clazz, wrapper.getResources(), wrapper.getPackageName());
                filedCache.put(clazz.getName(), fields);
            }

            for (int i = 0; i < fields.size(); i++) {
                @IdRes long resId = fields.keyAt(i);
                try {
                    fields.valueAt(i).set(wrapper, wrapper.findViewById((int) resId));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        } while ((clazz = clazz.getSuperclass()) != ViewWrapper.class);
    }

    static LongSparseArray<Field> findAllFileds(Class clazz, Resources res, String packageName) {
        if (!clazz.isAnnotationPresent(DisableInjectView.class)) {
            LongSparseArray<Field> fieldLongSparseArray = new LongSparseArray<>(3);
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
                        fieldLongSparseArray.put(resId, field);
                        break;
                    } else if (InjectViewByName.class.isAssignableFrom(anno.getClass())) {
                        int resId = res.getIdentifier(((InjectViewByName) anno).value(), "id", packageName);
                        field.setAccessible(true);
                        fieldLongSparseArray.put(resId, field);
                        break;
                    }
                }
            }

            return fieldLongSparseArray;

        } else {
            return new LongSparseArray<>(0);
        }
    }
}
