package cc.zgeek.pagelib;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/18 : Create
 */

//每次只有一个子Page激活的容器
public abstract class SingleActivePage extends Page{
    public SingleActivePage(PageActivity pageActivity) {
        super(pageActivity);
    }

    public abstract IPage getActiviePage();


    @Override
    public void onShow() {
        IPage page = getActiviePage();
        if(page != null){
            page.onShow();
        }
    }

    @Override
    public void onShown() {
        IPage page = getActiviePage();
        if(page != null){
            page.getRootView().bringToFront();
            page.getRootView().setVisibility(View.VISIBLE);
            page.getRootView().requestFocus();
            page.onShown();
        }
    }

    @Override
    public void onHide() {
        IPage page = getActiviePage();
        if(page != null){
            page.onHide();
        }
    }

    @Override
    public void onHidden() {
        IPage page = getActiviePage();
        if(page != null){
            page.onHidden();
        }
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        IPage page = getActiviePage();
        return page != null && page.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onMenuPressed() {
        IPage page = getActiviePage();
        return page != null && page.onMenuPressed();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        IPage page = getActiviePage();
        return page != null && page.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onBackPressed() {
        IPage page = getActiviePage();
        return page != null && page.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IPage page = getActiviePage();
        if(page != null)
            page.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean isChildPageActive(IPage child) {
        return child != null && getActiviePage() == child;
    }
}
