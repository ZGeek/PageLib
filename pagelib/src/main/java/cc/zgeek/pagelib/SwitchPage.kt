package cc.zgeek.pagelib

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

import cc.zgeek.pagelib.Utils.PageUtil
import cc.zgeek.pagelib.anim.PageAnimatorProvider
import cc.zgeek.pagelib.anim.SimpleAnimListener

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/14 : Create
 */

abstract class SwitchPage(pageActivity: PageActivity) : SingleActivePage(pageActivity) {
    var showIndex = -1
        private set
    private var mAnimate: Animator? = null

    @JvmOverloads fun switchToPage(page: IPage, provider: PageAnimatorProvider? = null) {
        val index = getChildPageIndex(page)
        if (index >= 0) {
            switchToPage(index, provider)
        }
    }

    @JvmOverloads fun switchToPage(index: Int, provider: PageAnimatorProvider? = null) {
        if (index < 0 || index >= childPageCount) {
            throw IllegalArgumentException("index is Out Of Bound")
        }
        if (showIndex == index)
            return

        ensureEndAnimationExecution()
        val active = PageUtil.isPageActive(this)
        var tmpOld: IPage? = null
        if (showIndex >= 0) {
            tmpOld = getChildPageAt(showIndex)
        }
        val oldPage = tmpOld
        val newPage = getChildPageAt(index)
        prepareForSwitch(newPage, oldPage)
        if (active) {
            newPage.onShow()
            oldPage?.onHide()
        }
        if (provider != null && active) {
            mAnimate = provider.getPageAnimation(currentContiner(), oldPage?.rootView, newPage.rootView)
            mAnimate!!.addListener(object : SimpleAnimListener() {
                override fun onAnimationEnd(animation: Animator) {
                    doFinalWorkForSwitchPage(active, index, newPage, oldPage)
                }
            })
            mAnimate!!.start()
        } else {
            doFinalWorkForSwitchPage(active, index, newPage, oldPage)
        }
    }

    private fun ensureEndAnimationExecution() {
        if (mAnimate != null && mAnimate!!.isRunning) {
            mAnimate!!.end()
            mAnimate = null
        }
    }

    private fun doFinalWorkForSwitchPage(isAttach: Boolean, index: Int, newPage: IPage, oldPage: IPage?) {
        if (isAttach) {
            newPage.onShow()
            oldPage?.onHidden()
        }
        if (oldPage != null)
            currentContiner().removeView(oldPage.rootView)
        if (isAttach) {
            newPage.rootView.requestFocus()
        }

        showIndex = index
    }

    internal fun prepareForSwitch(willShowPage: IPage, willHidePage: IPage?) {
        val willShowView = willShowPage.rootView
        if (willShowView.layoutParams == null) {
            val layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            layoutParams.setMargins(0, 0, 0, 0)
            willShowView.layoutParams = layoutParams
        }
        currentContiner().addView(willShowView)
        if (willHidePage != null)
            willHidePage.rootView.bringToFront()
    }

    public override fun addPage(page: IPage) {
        super.addPage(page)
        //        FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //        layout.setMargins(0, 0, 0, 0);
        //        page.getRootView().setLayoutParams(layout);
        if (showIndex < 0)
            switchToPage(0)
    }

    public override fun removePage(targetPage: IPage): Boolean {
        val removeIndex = getChildPageIndex(targetPage)
        val count = childPageCount
        if (removeIndex == -1) {
            return false
        }
        if (count == 0)
            return false

        if (showIndex != removeIndex) {//如果要显示的page不是当前要显示的page，则直接移除，并调用移除page的onDestroy方法，随后对方法showIndex进行修正
            if (targetPage.isViewInited) {
                targetPage.onDestroy()
            }
            val removePage = super.removePage(removeIndex)
            if (removeIndex < showIndex)
                showIndex--
            return removePage === targetPage
        } else {//如果要移除的page就是目前显示的page
            val hasPrePage = removeIndex != 0
            val hasAfterPage = removeIndex != count
            val willShowIndex = if (hasAfterPage) showIndex + 1 else if (hasPrePage) showIndex - 1 else -1
            if (willShowIndex >= 0) {
                switchToPage(willShowIndex)
                targetPage.onDestroy()
                return super.removePage(targetPage)
            } else {
                if (PageUtil.isPageActive(this)) {
                    targetPage.onHide()
                    targetPage.onHidden()
                }
                if (targetPage.isViewInited) {
                    targetPage.onDestroy()
                }
                showIndex = -1
                return super.removePage(targetPage)
            }
        }

    }

    open fun currentContiner(): ViewGroup {
        return rootView as FrameLayout
    }

    override val activiePage: IPage?
        get() {
            if (showIndex < 0 || showIndex >= childPageCount)
                return null
            return getChildPageAt(showIndex)
        }

    override fun onSaveInstanceState(isViewInited: Boolean): Bundle {
        val bundle = super.onSaveInstanceState(isViewInited)
        bundle.putInt(CURRENT_INDEX, showIndex)
        return bundle
    }

    override fun onViewInited(isRestore: Boolean, args: Bundle) {
        super.onViewInited(isRestore, args)
        if (isRestore) {
            val restoreIndex = args.getInt(CURRENT_INDEX)
            if (restoreIndex >= 0)
                switchToPage(restoreIndex)
        }
    }

    companion object {

        private val CURRENT_INDEX = "CURRENT_INDEX"
    }
}
