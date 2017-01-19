package cc.zgeek.pagelib;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;

import cc.zgeek.pagelib.Utils.AnnotationUtils;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/10 : Create
 */

public abstract class ViewWrapper {
    protected PageActivity mContext;
    protected volatile View rootView;
    private static String PACKAGE_NAME = null;

    public ViewWrapper(PageActivity pageActivity) {
        mContext = pageActivity;
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
        return mContext.getString(resId);
    }

    public String getString(int resId, Object... args) {
        return mContext.getString(resId, args);
    }

    public Resources getResources() {
        return mContext.getResources();
    }

    public String getPackageName() {
        return PACKAGE_NAME;
    }

    public LayoutInflater getLayoutInflater() {
        return mContext.getLayoutInflater();
    }


}
