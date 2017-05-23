package cc.zgeek.pagelib

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent

import cc.zgeek.pagelib.Utils.PageUtil

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/10 : Create
 */

abstract class PageActivity : AppCompatActivity() {
    var rootPage: IPage? = null
        internal set

    internal var handler: Handler = Handler(Looper.getMainLooper())

    var isActive = false
        internal set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var rootPageCopy:IPage?  = null


        if (savedInstanceState != null) {
            val clsName = savedInstanceState.getString("rootPage")
            val args = savedInstanceState.getBundle("rootData")
            rootPageCopy = PageUtil.restorePage(this, clsName, args)
        }
        if (rootPageCopy == null){
            rootPageCopy = initRootPage()
        }
        rootPage = rootPageCopy
        setContentView(rootPageCopy.rootView)
    }

    protected abstract fun initRootPage(): IPage

    override fun onBackPressed() {
        if (!checkNotNull(rootPage).onBackPressed()) {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        rootPage!!.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        isActive = true
        rootPage!!.onShow()
        super.onResume()
        rootPage!!.onShown()
    }

    override fun onPause() {
        rootPage!!.onHide()
        super.onPause()
        rootPage!!.onHidden()
        isActive = false
    }

    override fun onDestroy() {
        rootPage!!.onDestroy()
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (rootPage!!.onKeyDown(keyCode, event)) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (rootPage!!.onKeyUp(keyCode, event)) {
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    //    @Override
    //    public boolean onTouchEvent(MotionEvent event) {
    //        if (rootPage.onTouchEvent(event)) {
    //            return true;
    //        }
    //        return super.onTouchEvent(event);
    //    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        rootPage!!.onConfigurationChanged(newConfig)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        rootPage!!.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val bundle = rootPage!!.onSaveInstanceState(rootPage!!.isRootViewInitialized)
        outState.putBundle("rootData", bundle)
        outState.putString("rootPage", rootPage!!.javaClass.name)
    }

    //    @Override
    //    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    //        super.onRestoreInstanceState(savedInstanceState);
    //        rootPage.onRestoreInstanceState(savedInstanceState);
    //    }


    fun postDelayed(runnable: Runnable, delayMillis: Long) {
        if (delayMillis == 0L && Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run()
            return
        }
        handler.postDelayed(runnable, delayMillis)
    }

    fun post(runnable: Runnable) {
        handler.post(runnable)
    }

    fun removeCallbacks(runnable: Runnable) {
        handler.removeCallbacks(runnable)
    }
}
