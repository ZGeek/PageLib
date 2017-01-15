package cc.zgeek.pagelib;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.View;

import cc.zgeek.pagelib.Utils.AnnotationUtils;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/10 : Create
 */

public abstract class ViewWrapper {
    protected PageActivity mContext;
    protected View rootView;
    private static String PACKAGE_NAME = null;

    public ViewWrapper(PageActivity pageActivity) {
        mContext = pageActivity;
        rootView = initView();
        if(PACKAGE_NAME == null)
            PACKAGE_NAME = pageActivity.getPackageName();
        AnnotationUtils.injectView(this);
    }

    @NonNull
    protected abstract View initView();

    public final View getRootView() {
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

    public String getPackageName(){
        return PACKAGE_NAME;
    }
}
