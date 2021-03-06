package cc.zgeek.pagelib.anim

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup

/**
 * Copyright (c) 2015 neevek **@neevek.net>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 * PageAnimator to animate transition when navigating pages
 */
interface PageAnimatorProvider {

    /***
     * 为Page切换提供转场动画
     * @param fromView 将要隐藏的动画
     * *
     * @param toView 将要显示的动画
     * *
     * @return View动画的执行时间
     */
    fun getPageAnimation(container: ViewGroup, fromView: View?, toView: View): Animator


}
