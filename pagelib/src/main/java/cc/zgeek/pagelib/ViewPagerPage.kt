package cc.zgeek.pagelib


import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup

import java.util.ArrayList
import java.util.Arrays

import cc.zgeek.pagelib.Utils.PageUtil

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/14 : Create
 */

class ViewPagerPage(pageActivity: PageActivity) : SingleActivePage(pageActivity), ViewPager.OnPageChangeListener {
    private val adapterWrapper: IPageAdapterWrapper
    private var currentShowIndex = 0
    private var mOnPageChangeListeners: ArrayList<ViewPager.OnPageChangeListener>? = null

    init {
        adapterWrapper = IPageAdapterWrapper()
    }


    override val rootView: View by lazy {
        super.rootView
        onViewInited(PageUtil.isBundleFromSaveInstance(args), args)
        return@lazy super.rootView
    }

    override fun onViewInited(isRestore: Boolean, args: Bundle) {
        super.onViewInited(isRestore, args)
        val viewPager = rootView as ViewPager
        viewPager.addOnPageChangeListener(this)
        viewPager.adapter = adapterWrapper
        if (isRestore) {
            switchToPage(args.getInt(SHOW_INDEX))
        }
    }

    override fun onSaveInstanceState(isViewInited: Boolean): Bundle {
        val bundle = super.onSaveInstanceState(isViewInited)
        bundle.putInt(SHOW_INDEX, currentShowIndex)
        return bundle
    }

    fun addOnPageChangeListener(listener: ViewPager.OnPageChangeListener) {
        if (mOnPageChangeListeners == null) {
            mOnPageChangeListeners = ArrayList<ViewPager.OnPageChangeListener>()
        }
        mOnPageChangeListeners!!.add(listener)
    }

    fun removeOnPageChangeListener(listener: ViewPager.OnPageChangeListener) {
        if (mOnPageChangeListeners != null) {
            mOnPageChangeListeners!!.remove(listener)
        }
    }


    override val activiePage: IPage?
        get() {
            if (childPageCount == 0)
                return null
            return getChildPageAt(currentShowIndex)
        }


    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (mOnPageChangeListeners == null || mOnPageChangeListeners!!.size == 0)
            return
        for (i in mOnPageChangeListeners!!.indices) {
            mOnPageChangeListeners!![i].onPageScrolled(position, positionOffset, positionOffsetPixels)
        }
    }

    override fun onPageSelected(position: Int) {
        if (PageUtil.isPageActive(this) && position != currentShowIndex) {

            val oldPage = getChildPageAt(currentShowIndex)
            val page = getChildPageAt(position)

            page.onShow()
            oldPage.onHide()
            page.onShown()
            oldPage.onHidden()
        }
        currentShowIndex = position
        if (mOnPageChangeListeners == null || mOnPageChangeListeners!!.size == 0)
            return
        for (i in mOnPageChangeListeners!!.indices) {
            mOnPageChangeListeners!![i].onPageSelected(position)
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
        if (mOnPageChangeListeners == null || mOnPageChangeListeners!!.size == 0)
            return
        for (i in mOnPageChangeListeners!!.indices) {
            mOnPageChangeListeners!![i].onPageScrollStateChanged(state)
        }

    }

    @JvmOverloads fun switchToPage(index: Int, smoothScroll: Boolean = false) {
        (this.rootView as ViewPager).setCurrentItem(index, smoothScroll)
    }

    public override fun addPage(page: IPage) {
        super.addPage(page)
        adapterWrapper.notifyDataSetChanged()
    }

    fun addPages(vararg pages: IPage) {
        addPages(Arrays.asList(*pages))
    }

    fun addPages(pages: List<IPage>) {
        val preCount = childPageCount
        for (i in pages.indices) {
            super.addPage(pages[i])
        }
        val afterCount = childPageCount
        if (preCount == 0 && afterCount > 0) {
            val active = PageUtil.isPageActive(this)
            //此时第一个Page将显示，但并不会调用onPageSelected，所以此时需要对第一个page做处理
            if (active) {
                getChildPageAt(0).onShow()
            }
            adapterWrapper.notifyDataSetChanged()
            if (active) {
                getChildPageAt(0).onShown()
            }
        } else if (pages.size > 0) {
            adapterWrapper.notifyDataSetChanged()
        }
    }

    fun setPageList(pages: List<IPage>) {
        val active = PageUtil.isPageActive(this)
        val cPage = activiePage

        for (i in childPageCount - 1 downTo 0) {
            val tmpPage = getChildPageAt(i)
            if (tmpPage === cPage && active) {
                tmpPage.onHide()
                tmpPage.onHidden()
            }
            if (tmpPage.isViewInited)
                tmpPage.onDestroy()
            super.removePage(i)
        }
        addPages(pages)
    }

    public override fun removePage(page: IPage): Boolean {
        /***
         * 根据测试结果，ViewPage在页面移除的逻辑是
         * 1：当移除当前页时优先使用后面的一页，后面没有用前页，前页也没有显示空白
         * 2：移除非当前页时保持当前页继续显示给用户
         */

        val targetRemovePageIndex = getChildPageIndex(page)
        val active = PageUtil.isPageActive(this)
        if (targetRemovePageIndex < 0)
            return false
        if (targetRemovePageIndex == currentShowIndex) {
            val willShowPage = getWillShowPageIndexWhenRemove(targetRemovePageIndex)
            if (active) {
                willShowPage?.onShow()
                page.onHide()
            }

            super.removePage(page)
            adapterWrapper.notifyDataSetChanged()
            if (active) {
                willShowPage?.onShown()
                page.onHidden()
            }

            if (page.isViewInited) {
                page.onDestroy()
            }

        } else if (targetRemovePageIndex < currentShowIndex) {

            if (page.isViewInited) {
                page.onDestroy()
            }
            super.removePage(page)
            adapterWrapper.notifyDataSetChanged()
            currentShowIndex--
        } else {
            //            index > currentShowIndex
            if (page.isViewInited) {
                page.onDestroy()
            }
            super.removePage(page)
            adapterWrapper.notifyDataSetChanged()
        }

        return true
    }

    fun getWillShowPageIndexWhenRemove(removeIndex: Int): IPage? {
        val childCount = childPageCount
        val hasPre = removeIndex == 0
        val hasAfter = childCount > removeIndex + 1
        if (hasAfter) {
            return getChildPageAt(removeIndex + 1)
        } else {
            if (hasPre)
                return getChildPageAt(removeIndex - 1)
            else
                return null
        }
    }

    internal inner class IPageAdapterWrapper : PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val page = getChildPageAt(position)
            container.addView(page.rootView)
            return page
        }

        override fun getCount(): Int {
            return childPageCount
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return (`object` as IPage).rootView === view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val page = `object` as IPage
            container.removeView(page.rootView)
        }

        override fun getItemPosition(`object`: Any?): Int {
            val index = getChildPageIndex((`object` as IPage?)!!)
            if (index < 0 || index >= childPageCount)
                return PagerAdapter.POSITION_NONE
            return index
        }

        override fun getPageTitle(position: Int): CharSequence {
            return getChildPageAt(position).name ?: ""
        }
    }

    companion object {


        private val SHOW_INDEX = "SHOW_INDEX"
    }

}
