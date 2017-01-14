package cc.zgeek.page.Lib;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/10 : Create
 */

public abstract class Page extends ViewWrapper implements IPage {

    private static final String SAVED_PAGE_BUNDLE = "SAVED_PAGE_BUNDLE";
    private static final String SAVED_SUB_PAGE = "SAVED_SUB_PAGE";

    protected LinkedList<IPage> pageList = new LinkedList<>();

    public Page(PageActivity pageActivity) {
        super(pageActivity);
    }

    protected void addPage(IPage page) {
        pageList.add(page);
    }

    protected boolean removePage(IPage page) {
        return pageList.remove(page);
    }


    @Override
    public void onShow() {
        for (int i = pageList.size() - 1; i >= 0; i--) {
            pageList.get(i).onShow();
        }
    }

    @Override
    public void onShown() {
        for (int i = pageList.size() - 1; i >= 0; i--) {
            pageList.get(i).onShown();
            ;
        }
    }

    @Override
    public void onHide() {
        for (int i = pageList.size() - 1; i >= 0; i--) {
            pageList.get(i).onHide();
            ;
        }
    }

    @Override
    public void onHidden() {
        for (int i = pageList.size() - 1; i >= 0; i--) {
            pageList.get(i).onHidden();
        }
    }

    @Override
    public void onDestroy() {
        for (int i = pageList.size() - 1; i >= 0; i--) {
            pageList.get(i).onDestroy();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0 && this.onMenuPressed()) {
            return true;
        }
        return false;
    }

    public boolean onMenuPressed() {
        return false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        for (int i = 0; i < pageList.size(); ++i) {
            IPage p = pageList.get(i);
            p.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (int i = pageList.size() - 1; i >= 0; i--) {
            pageList.get(i).onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onLowMemory() {
        for (int i = pageList.size() - 1; i >= 0; i--) {
            pageList.get(i).onLowMemory();
        }
    }

    protected int findPageIndex(IPage page) {
        for (int i = 0; i < pageList.size(); i++) {
            if (pageList.get(i) == page)
                return i;
        }
        return -1;
    }

}
