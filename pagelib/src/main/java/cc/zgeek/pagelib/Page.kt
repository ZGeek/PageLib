package cc.zgeek.pagelib

import android.content.Intent
import android.content.res.Configuration
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View

import java.util.LinkedList

import cc.zgeek.pagelib.Utils.ListUtil
import cc.zgeek.pagelib.Utils.PageLibDebugUtis
import cc.zgeek.pagelib.Utils.PageUtil

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/10 : Create
 */

abstract class Page(pageActivity: PageActivity) : ViewWrapper(pageActivity), IPage {
    private val SAVED_PAGE_LIST_DATA = "LD_"
    private val SAVED_PAGE_LIST_CLASS = "CS_"
    private val PAGE_NAME = "NAME_g4#r%d+7"

    //    private static final String SAVED_PAGE_BUNDLE = "SAVED_PAGE_BUNDLE";
    //    private static final String SAVED_SUB_PAGE = "SAVED_SUB_PAGE";


    private val pageList = LinkedList<IPage>()
    override var parentPage: IPage? = null
    override //        if(!isViewInited() && PageUtil.isBundleFromSaveInstance(getArgs())){
            //            return getArgs().getString(PAGE_NAME);
            //        }
    var name: String? = null
        private set
    private var args: Bundle? = null

    override fun getRootView(): View {
        if (rootView == null) {
            synchronized(this) {
                if (rootView == null) {
                    super.getRootView()
                    this.onViewInited(PageUtil.isBundleFromSaveInstance(getArgs()), args)
                }
            }
        }
        return rootView
    }

    override val isViewInited: Boolean
        get() = rootView != null


    protected open fun addPage(page: IPage) {
        if (page.parentPage != null) {
            throw IllegalStateException("Page Can Not added Beacase Aready Have A Parent")
        }
        page.parentPage = this
        pageList.add(page)
    }

    protected open fun removePage(page: IPage): Boolean {
        if (page.parentPage !== this) {
            throw IllegalStateException("Page Can Not Remove Because It Not Belong Of This")
        }
        page.parentPage = null
        return pageList.remove(page)
    }


    protected fun removePage(index: Int): IPage {
        //        if (page.getParentPage() != this) {
        //            throw new IllegalStateException("Page Can Not Remove Because It Not Belong Of This");
        //        }
        val page = pageList.removeAt(index)
        page.parentPage = null
        return page
    }

    override val childPageCount: Int
        get() = pageList.size

    override fun getSubChildPages(beginIndex: Int, count: Int): List<IPage> {
        return ListUtil.subList(pageList, beginIndex, count)
    }

    override fun getChildPageIndex(page: IPage): Int {
        for (i in 0..childPageCount - 1) {
            if (getChildPageAt(i) === page)
                return i
        }
        return -1
    }

    override fun getChildPageAt(index: Int): IPage {
        //进行首位判断，用于加速查找过程，因为LinkedList查找首位比较快
        if (index == childPageCount - 1)
            return pageList.last
        if (index == 0)
            return pageList.first

        return pageList[index]
    }

    override fun onSaveInstanceState(isViewInited: Boolean): Bundle {
        var outState: Bundle? = null
        if (isViewInited) {
            outState = Bundle()
            val clsArray = arrayOfNulls<String>(childPageCount)
            for (i in 0..childPageCount - 1) {
                val p = getChildPageAt(i)
                val pBundle = p.onSaveInstanceState(p.isViewInited)

                val clsName = p.javaClass.name
                clsArray[i] = clsName

                val key = SAVED_PAGE_LIST_DATA + i
                outState.putBundle(key, pBundle)
            }
            outState.putStringArray(SAVED_PAGE_LIST_CLASS, clsArray)
            outState.putString(PAGE_NAME, name)
            PageUtil.setSaveInsanceFlag(outState)
        } else {
            outState = getArgs()
            outState.putString(PAGE_NAME, name)
        }

        return outState
    }

    override fun onViewInited(isRestore: Boolean, args: Bundle) {
        if (isRestore) {
            name = args.getString(PAGE_NAME)
        }
        if (isRestore) {
            val clsArray = args.getStringArray(SAVED_PAGE_LIST_CLASS) ?: return

            for (i in clsArray.indices) {
                val clsName = clsArray[i]
                val key = SAVED_PAGE_LIST_DATA + i
                val bundle = args.getBundle(key)
                val p = PageUtil.restorePage(context, clsName, bundle)
                p.args = bundle
                //            pageList.add(p);
                p.parentPage = this
                pageList.add(p)
                if (PageLibDebugUtis.isDebug) {
                    Log.d(PageLibDebugUtis.TAG, "restore child page " + p.toString() + " of " + this.toString())
                }
            }
        }

    }

    override fun setArgs(args: Bundle) {
        this.args = args
        if (this.args != null) {
            val tmpName = args.getString(PAGE_NAME)
            if (!TextUtils.isEmpty(tmpName)) {
                name = tmpName
            }
        }
    }

    override fun getArgs(): Bundle {
        if (args == null) {
            args = Bundle()
        }
        return args
    }


    override fun onShow() {
        for (i in childPageCount - 1 downTo 0) {
            getChildPageAt(i).onShow()
        }
    }

    override fun onShown() {
        for (i in childPageCount - 1 downTo 0) {
            getChildPageAt(i).onShown()
        }
    }

    override fun onHide() {
        for (i in childPageCount - 1 downTo 0) {
            getChildPageAt(i).onHide()
        }
    }

    override fun onHidden() {
        for (i in childPageCount - 1 downTo 0) {
            getChildPageAt(i).onHidden()
        }
    }

    override fun onDestroy() {
        for (i in childPageCount - 1 downTo 0) {
            val page = getChildPageAt(i)
            if (page.isViewInited)
                page.onDestroy()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MENU && event.repeatCount == 0 && this.onMenuPressed()) {
            return true
        }
        for (i in childPageCount - 1 downTo 0) {
            if (getChildPageAt(i).onKeyDown(keyCode, event))
                return true
        }
        return false
    }

    override fun onMenuPressed(): Boolean {
        for (i in childPageCount - 1 downTo 0) {
            if (getChildPageAt(i).onMenuPressed())
                return true
        }
        return false
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        for (i in childPageCount - 1 downTo 0) {
            if (getChildPageAt(i).onKeyUp(keyCode, event))
                return true
        }
        return false
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        for (i in 0..childPageCount - 1) {
            val p = getChildPageAt(i)
            p.onConfigurationChanged(newConfig)
        }
    }

    override fun onBackPressed(): Boolean {
        for (i in childPageCount - 1 downTo 0) {
            if (getChildPageAt(i).onBackPressed())
                return true
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        for (i in childPageCount - 1 downTo 0) {
            getChildPageAt(i).onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onLowMemory() {
        for (i in childPageCount - 1 downTo 0) {
            getChildPageAt(i).onLowMemory()
        }
    }

    override fun setName(name: String): IPage {
        this.name = name
        return this
    }

    override fun isChildPageActive(child: IPage): Boolean {
        return pageList != null && pageList.indexOf(child) >= 0
    }

    override fun toString(): String {
        return javaClass.simpleName + "(" + name + ")@" + Integer.toHexString(hashCode())
    }
}
