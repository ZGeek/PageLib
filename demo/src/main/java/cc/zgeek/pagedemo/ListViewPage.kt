package cc.zgeek.pagedemo

import android.app.WallpaperManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import java.util.ArrayList

import cc.zgeek.pagedemo.util.ToolbarHelper
import cc.zgeek.pagelib.Annotation.InjectView
import cc.zgeek.pagelib.Annotation.PageLayout
import cc.zgeek.pagelib.NavigationPage
import cc.zgeek.pagelib.Page
import cc.zgeek.pagelib.PageActivity

import kotlinx.android.synthetic.main.page_list.*


/**
 * Created by flyop.
 * Change History:
 * 2017/1/29 : Create
 */

@PageLayout(R.layout.page_list)
class ListViewPage(pageActivity: PageActivity) : Page(pageActivity) {

    @InjectView(R.id.tb_header_bar)
    private val mTbHeaderBar: Toolbar? = null
    @InjectView(R.id.iv_parallax_image)
    private val mIvParallaxImage: ImageView? = null
    @InjectView(R.id.rv_main_list)
    private val mRvMainList: RecyclerView? = null

    override fun onViewInitialized(isRestore: Boolean, args: Bundle) {
        mTbHeaderBar!!.title = "ListPage"

        ToolbarHelper.setNavigationIconEnabled(
                mTbHeaderBar, true, View.OnClickListener { (context.rootPage as NavigationPage).popPage() })
        setParallaxImage()
    }


    override fun onShown() {
        super.onShown()

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        mRvMainList!!.layoutManager = LinearLayoutManager(context)
        mRvMainList.adapter = MainListItemAdapter(object : ArrayList<String>() {
            init {
                for (i in 0..24) {
                    add(context.getString(R.string.page_demo_about))
                }
            }
        })
    }

    private fun setParallaxImage() {
        val wallpager = WallpaperManager.getInstance(context).drawable
        if (wallpager != null) {
            mIvParallaxImage!!.setImageDrawable(wallpager)
        }
    }

    private inner class MainListItemAdapter(private val mTextList: List<String>?) : RecyclerView.Adapter<MainListItemAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycler_view_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.tvText.text = mTextList!![position]
        }

        override fun getItemCount(): Int {
            return mTextList?.size ?: 0
        }

        internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var cvContainer: CardView
            var tvText: TextView

            init {
                cvContainer = itemView as CardView
                tvText = itemView.findViewById(R.id.tv_text) as TextView
            }

        }
    }
}
