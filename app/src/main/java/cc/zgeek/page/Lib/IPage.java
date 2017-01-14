package cc.zgeek.page.Lib;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

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

    @NonNull View getRootView();

//    void onSaveInstanceState(Bundle outState);
//
//    void onRestoreInstanceState(Bundle savedInstanceState);

    //Life Cycle Relation

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
