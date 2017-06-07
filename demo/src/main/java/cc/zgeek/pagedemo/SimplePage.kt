package cc.zgeek.pagedemo

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView

import java.util.Random

import cc.zgeek.pagedemo.util.NavHelper
import cc.zgeek.pagelib.Annotation.InjectView
import cc.zgeek.pagelib.Annotation.PageLayout
import cc.zgeek.pagelib.Page
import cc.zgeek.pagelib.PageActivity


/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/13 : Create
 */

@PageLayout(R.layout.simple_page)
class SimplePage private constructor(pageActivity: PageActivity) : Page(pageActivity), View.OnClickListener {

    internal val index: String
    @InjectView(R.id.text)
    internal lateinit var textView: TextView
    @InjectView(R.id.content)
    internal lateinit var frameLayout: FrameLayout
    private var tvBg: Int = 0
    private var frBg: Int = 0

    init {
        index = random.nextInt().toString() + ""
    }


    override fun onViewInitialized(isRestore: Boolean, args: Bundle) {
        super.onViewInitialized(isRestore, args)
        //        if (isRestore) {
        ////            setPageName(args.getString("name"));
        tvBg = args.getInt("tvBg")
        //            if (tvBg == 0)
        //                tvBg = Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255));
        frBg = args.getInt("frBg")
        //            if (frBg == 0)
        //                frBg = Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255));
        //        } else {
        //            tvBg = Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255));
        //            frBg = Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255));
        //        }
        textView.setBackgroundColor(tvBg)
        frameLayout.setBackgroundColor(frBg)
        textView.text = name
        textView.setOnClickListener(this)
    }

    override fun onSaveInstanceState(isViewInited: Boolean): Bundle {
        return super.onSaveInstanceState(false)
        //        bundle.putString("name", getName());

        //        bundle.putInt("tvBg", getArgs().getInt("tvBg"));
        //        bundle.putInt("frBg", getArgs().getInt("frBg"));
        //        return bundle;
    }

    override fun onClick(v: View) {
        val page = NavHelper.findFirstNav(this)
        page?.pushPage(SimplePage.newInstance(context))
        //        ((NavigationPage) getContext().getRootPage()).pushPage(new SimplePage(getContext()));
    }

    override fun onShow() {
        super.onShow()
        Log.d(SimplePage::class.java.simpleName, name + "-->onShow")
    }

    override fun onShown() {
        super.onShown()
        Log.d(SimplePage::class.java.simpleName, name + "-->onShown")
    }

    override fun onHide() {
        super.onHide()
        Log.d(SimplePage::class.java.simpleName, name + "-->onHide")
    }

    override fun onHidden() {
        super.onHidden()
        Log.d(SimplePage::class.java.simpleName, name + "-->onHidden")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(SimplePage::class.java.simpleName, name + "-->onDestroy")
    }

    override var name: String = ""
        get() {
            val name = super.name
            if (TextUtils.isEmpty(name))
                return index
            return name
        }

    companion object {
        internal var random = Random()

        fun newInstance(activity: PageActivity): SimplePage {
            val args = Bundle()
            args.putInt("tvBg", Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255)))
            args.putInt("frBg", Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255)))

            val simplePage = SimplePage(activity)
            simplePage.args = args
            return simplePage
        }
    }
}
