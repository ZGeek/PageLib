package cc.zgeek.pagelib

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout

import cc.zgeek.pagelib.Utils.PageLibDebugUtis
import cc.zgeek.pagelib.Utils.PageUtil
import cc.zgeek.pagelib.anim.PageAnimatorProvider
import cc.zgeek.pagelib.anim.SimpleAnimListener

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/10 : Create
 *
 *
 * NavigationPage，The back stack mechanism for management page，Keep at least one Page，Can only display the top Page at same time
 */
class NavigationPage(pageActivity: PageActivity) : SingleActivePage(pageActivity) {
    private var mNavigationPageHelper: NavigationPageHelper? = null
    private var mAnimatedTransitions: Animator? = null

    override fun onViewInited(isRestore: Boolean, args: Bundle) {
        super.onViewInited(isRestore, args)
        if (isRestore) {
            val tmp = activiePage
            if (tmp != null)
                addViewToContiner(tmp)
        }
        (rootView as NavigationPageHelper.NavigationContainerView).enableSwipeToHide()
    }

    override val rootView: View by lazy {
        val myNav = NavigationPageHelper(this)
        this.mNavigationPageHelper = myNav
        this._rootView = myNav.createContainerView(context)
        onViewInited(PageUtil.isBundleFromSaveInstance(args), args)
        _rootView ?: throw IllegalAccessError("_rootView changed by other thread")
    }

    @JvmOverloads fun pushPage(newPage: IPage, anim: Boolean = true) {
        pushPage(newPage, if (anim) defaultPushAnimator else null)
    }

    fun pushPage(showPage: IPage?, provider: PageAnimatorProvider?) {
        if (showPage == null) {
            throw NullPointerException("the showView can not be NULL")
        }

        val active = PageUtil.isPageActive(this)
        ensureEndAnimationExecution()

        val hidePage = topPage

        addPage(showPage)
        addViewToContiner(showPage)
        //        currentContiner().addView(showPage.getRootView());

        if (active) {
            showPage.onShow()
            hidePage?.onHide()
        }


        if (active && provider != null) {
            val tran = provider.getPageAnimation(currentContiner(), hidePage?.rootView, showPage.rootView)
            tran.addListener(object : SimpleAnimListener() {
                override fun onAnimationEnd(animation: Animator) {
                    doFinalWorkForPushPage(hidePage, showPage, active)
                }
            })
            mAnimatedTransitions = tran
            tran.start()
        } else {
            doFinalWorkForPushPage(hidePage, showPage, active)
        }

        if (PageLibDebugUtis.isDebug) {
            Log.d(PageLibDebugUtis.TAG, String.format(">>>> pushPage, pageStack=%d, %s", childPageCount, showPage))
        }
    }

    private fun addViewToContiner(page: IPage) {
        val view = page.rootView
        if (view.layoutParams == null) {
            val layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            layoutParams.setMargins(0, 0, 0, 0)
            view.layoutParams = layoutParams
        }
        currentContiner().addView(view)
    }


    @Synchronized private fun ensureEndAnimationExecution() {
            mAnimatedTransitions?.end()
            mAnimatedTransitions = null
    }


    private fun doFinalWorkForPushPage(oldPage: IPage?, newPage: IPage, isAttach: Boolean) {
        if (isAttach) {
            if (newPage === topPage) {
                newPage.rootView.bringToFront()
                newPage.rootView.requestFocus()
                newPage.onShown()
            }
        }

        if (oldPage != null) {
            currentContiner().removeView(oldPage.rootView)
            if (isAttach) {
                oldPage.onHidden()
            }
        }

        mAnimatedTransitions = null
    }


    @JvmOverloads fun popPage(anim: Boolean = true) {
        popPage(if (anim) defaultPopAnimator else null)
    }

    fun popPage(provider: PageAnimatorProvider?) {
        popTopNPages(1, provider)
    }

    @JvmOverloads fun popToTargetPage(page: IPage, anim: Boolean = true) {
        popToTargetPage(page, if (anim) defaultPopAnimator else null)
    }

    fun popToTargetPage(page: IPage, provider: PageAnimatorProvider?) {

        val currentIndex = (childPageCount - 1 downTo 0).lastOrNull { getChildPageAt(it) === page } ?: -1
        if (currentIndex <= 0)
            return
        val N = childPageCount - currentIndex - 1
        popTopNPages(N, provider)
    }

    @JvmOverloads fun popToTargetPage(clazz: Class<out IPage>, anim: Boolean = true) {
        popToTargetPage(clazz, if (anim) defaultPopAnimator else null)
    }

    fun popToTargetPage(clazz: Class<out IPage>, provider: PageAnimatorProvider?) {
        var currentIndex = -1
        for (i in childPageCount - 1 downTo 0) {
            if (getChildPageAt(i).javaClass == clazz)
                currentIndex = i
        }
        if (currentIndex <= 0)
            return
        val N = childPageCount - currentIndex - 1
        popTopNPages(N, provider)
    }

    @JvmOverloads fun popToRootPage(anim: Boolean = true) {
        popToRootPage(if (anim) defaultPopAnimator else null)
    }

    fun popToRootPage(provider: PageAnimatorProvider?) {
        popTopNPages(childPageCount - 1)
    }

    @JvmOverloads fun popTopNPages(n: Int, animated: Boolean = true) {
        popTopNPages(n, if (animated) defaultPopAnimator else null)
    }

    fun popTopNPages(n: Int, provider: PageAnimatorProvider?) {
        popTopNPages(n, provider, false)
    }

    private fun popTopNPages(n: Int, provider: PageAnimatorProvider?, isFromSwipe: Boolean) {
//        var provider = provider
        ensureEndAnimationExecution()
        if (n <= 0 || n > childPageCount - 1 || childPageCount <= 1)
        //数据不合法
            return

        if (n <= 0 || childPageCount <= 0) {
            return
        }

        val active = PageUtil.isPageActive(this)
        if (childPageCount == 1)
            return
        this.rootView.isEnabled = false
        val willRemovePages = getSubChildPages(childPageCount - n, n)
        val willShowPage = getChildPageAt(childPageCount - n - 1)
        val topPageCopy = topPage ?: throw RuntimeException()
        if (!isFromSwipe) {
            prepareForPop(willShowPage, topPageCopy)
        }
        if (active) {
            willShowPage.onShow()
            topPageCopy.onHide()
        }

        var providerCopy = provider
        if (isFromSwipe) {
            providerCopy = this.defaultPopAnimator
        }
        if (providerCopy != null && active) {
            val mAnimatedTransitions = providerCopy.getPageAnimation(currentContiner(), topPageCopy.rootView, willShowPage.rootView)
            mAnimatedTransitions.addListener(object : SimpleAnimListener() {
                override fun onAnimationEnd(animation: Animator) {
                    doFinalWorkForPopPage(willRemovePages, willShowPage, active)
                }
            })
            resetMargin(topPageCopy.rootView)
            resetMargin(willShowPage.rootView)
            this.mAnimatedTransitions = mAnimatedTransitions;
            mAnimatedTransitions.start()
        } else {
            doFinalWorkForPopPage(willRemovePages, willShowPage, active)
        }
    }


    internal fun popFromSwipe() {
        popTopNPages(1, null, true)
    }

    private fun doFinalWorkForPopPage(willRemovePages: List<IPage>, willShowPage: IPage, active: Boolean) {
        if (willShowPage.rootView.animation != null) {
            Log.d("Animation Pop End", "" + willShowPage.rootView.animation.hasEnded())
        }
        if (active) {
            willShowPage.rootView.bringToFront()
            willShowPage.rootView.requestFocus()
            willShowPage.onShown()
        }
        val topPage = willRemovePages[willRemovePages.size - 1]
        for (i in willRemovePages.indices.reversed()) {
            val cPage = willRemovePages[i]
            if (cPage === topPage) {
                currentContiner().removeView(cPage.rootView)
                if (active) {
                    cPage.onHidden()
                }
            }
        }

        for (i in willRemovePages.indices.reversed()) {
            val cPage = willRemovePages[i]
            if (cPage.isViewInited) {
                cPage.onDestroy()
            }
            removePage(cPage)
            if (PageLibDebugUtis.isDebug) {
                Log.d(PageLibDebugUtis.TAG, String.format("<<<< popPage, pageStack=%d, %s", childPageCount, cPage))
            }
        }
        //        mViewTransparentMask.bringToFront();
        this.rootView.isEnabled = true
        mAnimatedTransitions = null
    }

    fun deletePage(index: Int): Boolean {
        ensureEndAnimationExecution()
        if (index <= 0 || index >= childPageCount) {
            return false
        }
        if (index == childPageCount - 1) {
            popPage(false)
            return true
        }
        val removedPage = getChildPageAt(index)

        if (removedPage.isViewInited)
            removedPage.onDestroy()
        removePage(removedPage)
        if (PageLibDebugUtis.isDebug) {
            Log.d(PageLibDebugUtis.TAG, String.format(">>>> deletePage(%d), pageStack=%d, %s", index, childPageCount, topPage))
        }
        return true
    }


    val topPage: IPage?
        get() = if (childPageCount == 0) null else getChildPageAt(childPageCount - 1)

    val isTopPageCanSwipeToHide: Boolean
        get() {
            val page = topPage ?: return false
            if (page is SupportSwipToHide && !page.canSwipToHide())
                return false
            return true
        }


    fun currentContiner(): ViewGroup {
        return this.rootView as ViewGroup
    }

    private val defaultPopAnimator: PageAnimatorProvider
        get() = object : PageAnimatorProvider {
            override fun getPageAnimation(container: ViewGroup, fromView: View?, toView: View): Animator {
                if(fromView == null){
                    throw IllegalArgumentException("fromView is null")
                }
                val totleWidth = container.width
                val left = fromView.left
                val time = Math.abs(DEFAULT_ANIMATE_TIME * (totleWidth - left) / totleWidth)

                val animator = ValueAnimator()
                animator.setDuration(time.toLong()).setIntValues(left, totleWidth)
                animator.interpolator = AccelerateInterpolator()
                animator.addUpdateListener { animation ->
                    fromView.translationX = (animation.animatedValue as Int).toFloat()
                    toView.translationX = (animation.animatedValue as Int / 2 - totleWidth / 2).toFloat()
                }
                animator.addListener(object : SimpleAnimListener() {
                    override fun onAnimationEnd(animation: Animator) {
                        fromView.translationX = 0f
                        toView.translationX = 0f
                    }
                })
                return animator
            }
        }


    private val defaultPushAnimator: PageAnimatorProvider
        get() = object : PageAnimatorProvider {
            override fun getPageAnimation(container: ViewGroup, fromView: View?, toView: View): Animator {
                if(fromView == null){
                    throw IllegalArgumentException("fromView is null")
                }
                val totleWidth = container.width

                var left = toView.left
                if (left == 0)
                    left = totleWidth
                val time = Math.abs(DEFAULT_ANIMATE_TIME * left / totleWidth)


                val animator = ValueAnimator()
                animator.setDuration(time.toLong()).setIntValues(left, 0)
                animator.interpolator = DecelerateInterpolator()
                animator.addUpdateListener { animation ->
                    val value = animation.animatedValue as Int
                    fromView.translationX = (value / 2 - totleWidth / 2).toFloat()
                    toView.translationX = value.toFloat()
                }
                animator.addListener(object : SimpleAnimListener() {
                    override fun onAnimationEnd(animation: Animator) {
                        fromView.translationX = 0f
                        toView.translationX = 0f
                    }
                })
                return animator
            }
        }

    internal fun cancelSwipeToHide() {
        val topPage = topPage
        val prePage = getChildPageAt(childPageCount - 2)
        val provider = defaultPushAnimator

        if(topPage == null){
            throw IllegalStateException("topPage can not be null")
        }

        ensureEndAnimationExecution()
        if (topPage.rootView.left > 0) {
            val mAnimatedTransitions = provider.getPageAnimation(currentContiner(), prePage.rootView, topPage.rootView)
            resetMargin(prePage.rootView)
            resetMargin(topPage.rootView)
            mAnimatedTransitions.addListener(object : SimpleAnimListener() {
                override fun onAnimationEnd(animation: Animator) {
                    currentContiner().removeView(prePage.rootView)
                    this@NavigationPage.mAnimatedTransitions = null

                }
            })
            this@NavigationPage.mAnimatedTransitions = mAnimatedTransitions;
            mAnimatedTransitions.start()
        } else {
            resetMargin(prePage.rootView)
            resetMargin(topPage.rootView)
            currentContiner().removeView(prePage.rootView)
        }
    }

    private fun resetMargin(view: View) {
        val lp = view.layoutParams as ViewGroup.MarginLayoutParams
        lp.leftMargin = 0
        lp.rightMargin = 0
        view.layoutParams = lp
        view.requestLayout()
    }


    internal fun prepareForPop(willShowPage: IPage, willHidePage: IPage) {
        val willShowView = willShowPage.rootView
        if (willShowView.layoutParams == null) {
            val layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            layoutParams.setMargins(0, 0, 0, 0)
            willShowView.layoutParams = layoutParams
        }
        currentContiner().addView(willShowView)
        willHidePage.rootView.bringToFront()
    }

    //Life Cycle

    override val activiePage: IPage?
        get() = topPage

    override fun onShow() {
        ensureEndAnimationExecution()
        super.onShow()
    }

    override fun onShown() {
        ensureEndAnimationExecution()
        super.onShown()
    }

    override fun onHide() {
        ensureEndAnimationExecution()
        super.onHide()
    }

    override fun onHidden() {
        ensureEndAnimationExecution()
        super.onHidden()
    }

    override fun onDestroy() {
        ensureEndAnimationExecution()
        super.onDestroy()
    }


    override fun onBackPressed(): Boolean {
        if (super.onBackPressed()) {
            return true
        }
        if (childPageCount > 1) {
            popPage()
            return true
        }
        return super.onBackPressed()
    }

    internal val isAnim: Boolean
        get() = mAnimatedTransitions?.isRunning ?: false

    companion object {

        private val TAG = NavigationPage::class.java.name
        private val DEFAULT_ANIMATE_TIME = 300 //ms
    }
}
