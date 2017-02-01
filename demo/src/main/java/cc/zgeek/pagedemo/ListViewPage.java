package cc.zgeek.pagedemo;

import android.app.WallpaperManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.zgeek.pagedemo.util.ToolbarHelper;
import cc.zgeek.pagelib.Annotation.InjectView;
import cc.zgeek.pagelib.Annotation.PageLayout;
import cc.zgeek.pagelib.NavigationPage;
import cc.zgeek.pagelib.Page;
import cc.zgeek.pagelib.PageActivity;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/29 : Create
 */

@PageLayout(R.layout.page_list)
public class ListViewPage extends Page {

    @InjectView(R.id.tb_header_bar)
    private Toolbar mTbHeaderBar;
    @InjectView(R.id.iv_parallax_image)
    private ImageView mIvParallaxImage;
    @InjectView(R.id.rv_main_list)
    private RecyclerView mRvMainList;

    public ListViewPage(PageActivity pageActivity) {
        super(pageActivity);
    }

    @Override
    public void onViewInited(boolean isRestore, Bundle args) {
        mTbHeaderBar.setTitle("ListPage");
        ToolbarHelper.setNavigationIconEnabled(
                mTbHeaderBar, true, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((NavigationPage) getContext().getRootPage()).popPage();
                    }
                });
        setParallaxImage();
    }


    @Override
    public void onShown() {
        super.onShown();

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        mRvMainList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvMainList.setAdapter(new MainListItemAdapter(new ArrayList<String>() {{
            for (int i = 0; i < 25; ++i) {
                add(getContext().getString(R.string.page_demo_about));
            }
        }}));
    }

    private void setParallaxImage() {
        Drawable wallpager = WallpaperManager.getInstance(getContext()).getDrawable();
        if (wallpager != null) {
            mIvParallaxImage.setImageDrawable(wallpager);
        }
    }

    private class MainListItemAdapter extends RecyclerView.Adapter<MainListItemAdapter.ViewHolder> {
        private List<String> mTextList;

        public MainListItemAdapter(List<String> textList) {
            mTextList = textList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_view_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.tvText.setText(mTextList.get(position));
        }

        @Override
        public int getItemCount() {
            return mTextList != null ? mTextList.size() : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            CardView cvContainer;
            TextView tvText;

            public ViewHolder(View itemView) {
                super(itemView);
                cvContainer = (CardView) itemView;
                tvText = (TextView) itemView.findViewById(R.id.tv_text);
            }

        }
    }
}
