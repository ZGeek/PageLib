package cc.zgeek.pagelib;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import cc.zgeek.pagelib.Utils.AnnotationUtils;

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/10 : Create
 */

public abstract class ViewWrapper {
    private PageActivity context;
    protected volatile View rootView;
    private static String PACKAGE_NAME = null;

    public ViewWrapper(PageActivity pageActivity) {
        context = pageActivity;
        if (PACKAGE_NAME == null)
            PACKAGE_NAME = pageActivity.getPackageName();
    }

    @NonNull
    public View getRootView() {
        rootView = AnnotationUtils.injectLayout(this);
        if (rootView == null) {
            throw new IllegalStateException("Neigher " + getClass().getName() + " nor it's supper class Annotation with PageLaout or PageLayoutName");
        }
        AnnotationUtils.injectView(this);

        return rootView;
    }

    public View findViewById(int id) {
        return rootView.findViewById(id);
    }

    public View findViewByName(String name) {
        int id = getResources().getIdentifier(name, "id", PACKAGE_NAME);
        return rootView.findViewById(id);
    }

    public String getString(int resId) {
        return context.getString(resId);
    }

    public String getString(int resId, Object... args) {
        return context.getString(resId, args);
    }

    public Resources getResources() {
        return context.getResources();
    }

    public String getPackageName() {
        return PACKAGE_NAME;
    }

    public LayoutInflater getLayoutInflater() {
        return context.getLayoutInflater();
    }

    public PageActivity getContext() {
        return context;
    }
}
