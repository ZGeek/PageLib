package cc.zgeek.pagelib;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.MotionEvent;

import java.util.LinkedList;
import java.util.List;

import cc.zgeek.pagelib.Annotation.PageState;
import cc.zgeek.pagelib.Utils.ListUtil;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/10 : Create
 */

public abstract class Page extends ViewWrapper implements IPage {

//    private static final String SAVED_PAGE_BUNDLE = "SAVED_PAGE_BUNDLE";
//    private static final String SAVED_SUB_PAGE = "SAVED_SUB_PAGE";

//    public static final int STATE_SHOWING = 0;
//    public static final int STATE_SHOWN = 1;
//    public static final int STATE_HIDDING = 2;
//    public static final int STATE_HIDDEN= 3;

    private LinkedList<IPage> pageList = new LinkedList<>();
    private IPage parentPage = null;

//    protected @PageState int pageState = STATE_HIDDEN;

    public Page(PageActivity pageActivity) {
        super(pageActivity);
    }

    @Override
    public void addPage(IPage page) {
        if (page.getParentPage() != null) {
            throw new IllegalStateException("Page Can Not Remove Beacase Aready Have A Parent");
        }
        page.setParentPage(this);
        pageList.add(page);
    }

    @Override
    public void removePage(IPage page) {
        if (page.getParentPage() != this) {
            throw new IllegalStateException("Page Can Not Remove Because It Not Belong Of This");
        }
        pageList.remove(page);
    }

    @Override
    public int getChildPageCount() {
        return pageList.size();
    }

    @Override
    public List<IPage> getSubChildPages(int beginIndex, int count) {
        return ListUtil.subList(pageList, beginIndex, count);
    }

    @Override
    public int getChildPageIndex(@NonNull IPage page) {
        return pageList.indexOf(page);
    }

    @Override
    public IPage getChildPageAt(int index) {
        //进行首位判断，用于加速查找过程，因为LinkedList查找首位比较快
        if (index == pageList.size() - 1)
            return pageList.getLast();
        if(index == 0)
            return pageList.getFirst();

        return pageList.get(index);
    }

    @Override
    public boolean isAttachToActivity() {
        IPage rootPage =mContext.getRootPage();
        if(rootPage == null)
            return false;
        if(rootPage == this)
            return true;
        IPage parent = this.getParentPage();
        while (parent != null){
            if(parent == rootPage)
                return true;
            parent = parent.getParentPage();
        }
        return false;
    }

//    public@PageState int getPageState() {
//        return pageState;
//    }

    @Override
    public void onShow() {
//        this.pageState = STATE_SHOWN;
        for (int i = pageList.size() - 1; i >= 0; i--) {
            pageList.get(i).onShow();
        }
    }

    @Override
    public void onShown() {
//        pageState = STATE_SHOWN;
        for (int i = pageList.size() - 1; i >= 0; i--) {
            pageList.get(i).onShown();
        }
    }

    @Override
    public void onHide() {
//        pageState = STATE_HIDDING;
        for (int i = pageList.size() - 1; i >= 0; i--) {
            pageList.get(i).onHide();
        }
    }

    @Override
    public void onHidden() {
//        pageState = STATE_HIDDEN;
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
        for (int i = pageList.size() - 1; i >= 0; i--) {
            if(pageList.get(i).onBackPressed())
                return true;
        }
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

    @Override
    public IPage getParentPage() {
        return parentPage;
    }

    @Override
    public void setParentPage(IPage parentPage) {
        this.parentPage = parentPage;
    }
}
