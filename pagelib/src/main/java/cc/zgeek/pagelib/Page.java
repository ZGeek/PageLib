package cc.zgeek.pagelib;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

import cc.zgeek.pagelib.Annotation.PageState;
import cc.zgeek.pagelib.Utils.AnnotationUtils;
import cc.zgeek.pagelib.Utils.ListUtil;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/10 : Create
 */

public abstract class Page extends ViewWrapper implements IPage {

//    private static final String SAVED_PAGE_BUNDLE = "SAVED_PAGE_BUNDLE";
//    private static final String SAVED_SUB_PAGE = "SAVED_SUB_PAGE";


    private LinkedList<IPage> pageList = new LinkedList<>();
    private IPage parentPage = null;
    private String name = null;


    public Page(PageActivity pageActivity) {
        super(pageActivity);
    }

    @NonNull
    @Override
    public View getRootView() {
        if (rootView == null) {
            synchronized (this) {
                if (rootView == null) {
                    super.getRootView();
                    this.onViewInited();
                }
            }
        }
        return rootView;
    }

    @Override
    public void onViewInited() {

    }

    @Override
    public final boolean isViewInited() {
        return rootView != null;
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
    public boolean removePage(IPage page) {
        if (page.getParentPage() != this) {
            throw new IllegalStateException("Page Can Not Remove Because It Not Belong Of This");
        }
        page.setParentPage(null);
        return pageList.remove(page);
    }

    @Override
    public boolean removePage(int index) {
//        if (page.getParentPage() != this) {
//            throw new IllegalStateException("Page Can Not Remove Because It Not Belong Of This");
//        }
        IPage page = pageList.remove(index);
        page.setParentPage(null);
        return page != null;
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
        for (int i = 0; i < getChildPageCount(); i++) {
            if (getChildPageAt(i) == page)
                return i;
        }
        return -1;
    }

    @Override
    public IPage getChildPageAt(int index) {
        //进行首位判断，用于加速查找过程，因为LinkedList查找首位比较快
        if (index == getChildPageCount() - 1)
            return pageList.getLast();
        if (index == 0)
            return pageList.getFirst();

        return pageList.get(index);
    }

    @Override
    public boolean isAttachToActivity() {
        IPage rootPage = mContext.getRootPage();
        if (rootPage == null)
            return false;
        if (rootPage == this)
            return true;
        IPage parent = this.getParentPage();
        while (parent != null) {
            if (parent == rootPage)
                return true;
            parent = parent.getParentPage();
        }
        return false;
    }


    @Override
    public void onShow() {
        for (int i = getChildPageCount() - 1; i >= 0; i--) {
            getChildPageAt(i).onShow();
        }
    }

    @Override
    public void onShown() {
        for (int i = getChildPageCount() - 1; i >= 0; i--) {
            getChildPageAt(i).onShown();
        }
    }

    @Override
    public void onHide() {
        for (int i = getChildPageCount() - 1; i >= 0; i--) {
            getChildPageAt(i).onHide();
        }
    }

    @Override
    public void onHidden() {
        for (int i = getChildPageCount() - 1; i >= 0; i--) {
            getChildPageAt(i).onHidden();
        }
    }

    @Override
    public void onDestroy() {
        for (int i = getChildPageCount() - 1; i >= 0; i--) {
            IPage page = getChildPageAt(i);
            if (page.isViewInited())
                page.onDestroy();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0 && this.onMenuPressed()) {
            return true;
        }
        for (int i = getChildPageCount() - 1; i >= 0; i--) {
            if (getChildPageAt(i).onKeyDown(keyCode, event))
                return true;
        }
        return false;
    }

    public boolean onMenuPressed() {
        for (int i = getChildPageCount() - 1; i >= 0; i--) {
            if (getChildPageAt(i).onMenuPressed())
                return true;
        }
        return false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        for (int i = getChildPageCount() - 1; i >= 0; i--) {
            if (getChildPageAt(i).onKeyUp(keyCode, event))
                return true;
        }
        return false;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        for (int i = 0; i < getChildPageCount(); ++i) {
            IPage p = getChildPageAt(i);
            p.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public boolean onBackPressed() {
        for (int i = getChildPageCount() - 1; i >= 0; i--) {
            if (getChildPageAt(i).onBackPressed())
                return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (int i = getChildPageCount() - 1; i >= 0; i--) {
            getChildPageAt(i).onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onLowMemory() {
        for (int i = getChildPageCount() - 1; i >= 0; i--) {
            getChildPageAt(i).onLowMemory();
        }
    }

    @Override
    public IPage getParentPage() {
        return parentPage;
    }

    @Override
    public void setParentPage(IPage parentPage) {
        this.parentPage = parentPage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
