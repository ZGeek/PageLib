package cc.zgeek.pagelib;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

import cc.zgeek.pagelib.Utils.ListUtil;
import cc.zgeek.pagelib.Utils.PageLibDebugUtis;
import cc.zgeek.pagelib.Utils.PageUtil;

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/10 : Create
 */

public abstract class Page extends ViewWrapper implements IPage {
    private final String SAVED_PAGE_LIST_DATA = "LD_";
    private final String SAVED_PAGE_LIST_CLASS = "CS_";
    private final String PAGE_NAME = "NAME_g4#r%d+7";

//    private static final String SAVED_PAGE_BUNDLE = "SAVED_PAGE_BUNDLE";
//    private static final String SAVED_SUB_PAGE = "SAVED_SUB_PAGE";


    private LinkedList<IPage> pageList = new LinkedList<>();
    private IPage parentPage = null;
    private String name = null;
    private Bundle args;


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
                    this.onViewInited(PageUtil.isBundleFromSaveInstance(getArgs()), args);
                }
            }
        }
        return rootView;
    }

    @Override
    public final boolean isViewInited() {
        return rootView != null;
    }


    protected void addPage(IPage page) {
        if (page.getParentPage() != null) {
            throw new IllegalStateException("Page Can Not added Beacase Aready Have A Parent");
        }
        page.setParentPage(this);
        pageList.add(page);
    }

    protected boolean removePage(IPage page) {
        if (page.getParentPage() != this) {
            throw new IllegalStateException("Page Can Not Remove Because It Not Belong Of This");
        }
        page.setParentPage(null);
        return pageList.remove(page);
    }


    protected IPage removePage(int index) {
//        if (page.getParentPage() != this) {
//            throw new IllegalStateException("Page Can Not Remove Because It Not Belong Of This");
//        }
        IPage page = pageList.remove(index);
        page.setParentPage(null);
        return page;
    }

    @Override
    public final int getChildPageCount() {
        return pageList.size();
    }

    @Override
    public final List<IPage> getSubChildPages(int beginIndex, int count) {
        return ListUtil.subList(pageList, beginIndex, count);
    }

    @Override
    public final int getChildPageIndex(@NonNull IPage page) {
        for (int i = 0; i < getChildPageCount(); i++) {
            if (getChildPageAt(i) == page)
                return i;
        }
        return -1;
    }

    @Override
    public final IPage getChildPageAt(int index) {
        //进行首位判断，用于加速查找过程，因为LinkedList查找首位比较快
        if (index == getChildPageCount() - 1)
            return pageList.getLast();
        if (index == 0)
            return pageList.getFirst();

        return pageList.get(index);
    }

    @Override
    public Bundle onSaveInstanceState(boolean isViewInited) {
        Bundle outState = null;
        if(isViewInited){
            outState = new Bundle();
            final String[] clsArray = new String[getChildPageCount()];
            for (int i = 0; i < getChildPageCount(); ++i) {
                final IPage p = getChildPageAt(i);
                Bundle pBundle = p.onSaveInstanceState(p.isViewInited());

                final String clsName = p.getClass().getName();
                clsArray[i] = clsName;

                final String key = SAVED_PAGE_LIST_DATA + i;
                outState.putBundle(key, pBundle);
            }
            outState.putStringArray(SAVED_PAGE_LIST_CLASS, clsArray);
            outState.putString(PAGE_NAME, getName());
            PageUtil.setSaveInsanceFlag(outState);
        }else{
            outState =  getArgs();
            outState.putString(PAGE_NAME, getName());
        }

        return outState;
    }

    @Override
    public void onViewInited(boolean isRestore, Bundle args) {
        if(isRestore){
            name = args.getString(PAGE_NAME);
        }
       if(isRestore){
           final String[] clsArray = args.getStringArray(SAVED_PAGE_LIST_CLASS);
           if (clsArray == null) {
               return;
           }

           for (int i = 0; i < clsArray.length; ++i) {
               final String clsName = clsArray[i];
               final String key = SAVED_PAGE_LIST_DATA + i;
               Bundle bundle = args.getBundle(key);
               final IPage p = PageUtil.restorePage(getContext(), clsName, bundle);
               p.setArgs(bundle);
//            pageList.add(p);
               p.setParentPage(this);
               pageList.add(p);
               if(PageLibDebugUtis.isDebug()){
                   Log.d(PageLibDebugUtis.TAG, "restore child page " +p.toString() + " of "+this.toString());
               }
           }
       }

    }

    @Override
    public void setArgs(Bundle args) {
        this.args = args;
        if(this.args != null){
            String tmpName = args.getString(PAGE_NAME);
            if(!TextUtils.isEmpty(tmpName)){
                name = tmpName;
            }
        }
    }

    @Override
    public Bundle getArgs() {
        if(args== null){
            args = new Bundle();
        }
        return args;
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
//        if(!isViewInited() && PageUtil.isBundleFromSaveInstance(getArgs())){
//            return getArgs().getString(PAGE_NAME);
//        }
        return name;
    }

    public IPage setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public boolean isChildPageActive(IPage child) {
        return pageList != null && pageList.indexOf(child) >= 0;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + getName() + ")@" + Integer.toHexString(hashCode());
    }
}
