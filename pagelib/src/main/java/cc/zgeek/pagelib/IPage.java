package cc.zgeek.pagelib;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/10 : Create
 */

public interface IPage {
    boolean onBackPressed();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    boolean onKeyDown(int keyCode, KeyEvent event);

    boolean onKeyUp(int keyCode, KeyEvent event);

    boolean onTouchEvent(MotionEvent event);

    void onConfigurationChanged(Configuration newConfig);

    IPage getParentPage();
    void setParentPage(IPage parentPage);

    boolean isAttachToActivity();

    @NonNull View getRootView();

//    void onSaveInstanceState(Bundle outState);
//
//    void onRestoreInstanceState(Bundle savedInstanceState);

    //Life Cycle Relation

    void addPage(IPage page);

    void removePage(IPage page);

    IPage getChildPageAt(int index);
    int getChildPageIndex(@NonNull IPage page);
    List<IPage> getSubChildPages(int beginIndex, int count);

    int getChildPageCount();

    //将要显示时的回调
    void onShow();
    //view已经显示时的回调
    void onShown();
    //将要隐藏时的回调
    void onHide();
    //已经隐藏时的回调
    void onHidden();
    //view将被销毁时调用
    void onDestroy();

    boolean onMenuPressed();

    void onLowMemory();
}
