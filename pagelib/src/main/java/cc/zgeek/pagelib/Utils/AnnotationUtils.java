package cc.zgeek.pagelib.Utils;


import android.content.res.Resources;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.LongSparseArray;
import android.util.Log;
import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import cc.zgeek.pagelib.Annotation.DisableInjectView;
import cc.zgeek.pagelib.Annotation.InjectView;
import cc.zgeek.pagelib.Annotation.InjectViewByName;
import cc.zgeek.pagelib.Annotation.PageLayout;
import cc.zgeek.pagelib.Annotation.PageLayoutName;
import cc.zgeek.pagelib.R;
import cc.zgeek.pagelib.ViewWrapper;

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/14 : Create
 */

public class AnnotationUtils {


    private static Map<String, LongSparseArray<Field>> filedCache = new ArrayMap<>();
    private static Map<String, Integer> layoutCache = new ArrayMap<>();

    public static void injectView(ViewWrapper wrapper) {
        if (wrapper == null) {
            throw new IllegalArgumentException("ViewWrapper can not be NULL");
        }
        if (wrapper.getRootView() == null) {
            throw new IllegalArgumentException("getRootView() can not be NULL");
        }
        Class clazz = wrapper.getClass();

        while (clazz != ViewWrapper.class) {
            LongSparseArray<Field> fields = filedCache.get(clazz.getName());
            if (fields == null) {
                fields = findAllFileds(clazz, wrapper.getResources(), wrapper.getPackageName());
                filedCache.put(clazz.getName(), fields);
            }

            for (int i = 0; i < fields.size(); i++) {
                @IdRes long resId = fields.keyAt(i);
                try {
                    View view = wrapper.findViewById((int) resId);
                    if(view == null && PageLibDebugUtis.isDebug()){
                        Log.w(PageLibDebugUtis.TAG, wrapper.getClass().getName()+" with property ("+fields.valueAt(i).getName()+") will be NULL");
                    }
                    fields.valueAt(i).set(wrapper, view);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            clazz = clazz.getSuperclass();
        }
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

    public static View injectLayout(ViewWrapper wrapper) {
        if (wrapper == null) {
            throw new IllegalArgumentException("ViewWrapper can not be NULL");
        }
        Class clazz = wrapper.getClass();
        while (clazz != ViewWrapper.class) {
            int id = findLayoutId(clazz, wrapper.getResources(), wrapper.getPackageName());
            layoutCache.put(clazz.getName(), id);
            if (id > 0) {
                return wrapper.getLayoutInflater().inflate(id, null, false);
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }


    public static
    @LayoutRes
    int findLayoutId(Class clazz, Resources res, String packageName) {

        Integer cacheId = layoutCache.get(clazz.getName());
        if (cacheId != null)
            return cacheId;

        int layoutId = -1;

        PageLayout pageLayout = (PageLayout) clazz.getAnnotation(PageLayout.class);
        if (pageLayout != null)
            layoutId = pageLayout.value();
        PageLayoutName pageLayoutName = (PageLayoutName) clazz.getAnnotation(PageLayoutName.class);
        if (pageLayoutName != null) {
            int id = res.getIdentifier(pageLayoutName.value(), "id", packageName);
            if (id > 0)
                layoutId = id;
        }
        layoutCache.put(clazz.getName(), layoutId);
        return layoutId;
    }
}
