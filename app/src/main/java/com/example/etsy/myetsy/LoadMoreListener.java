package com.example.etsy.myetsy;

import android.widget.AbsListView;

public abstract class LoadMoreListener implements AbsListView.OnScrollListener {
    boolean mLoadMore = false;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                break;

            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (mLoadMore) {
                    loadPage();
                }
                break;
        }

    }

    @Override
    public void onScroll( AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount )
    {
        // Pull more when you dont have enough to display for the next page (thats 2 visibleItemCounts away)
        if (firstVisibleItem + 2 * visibleItemCount > totalItemCount ) {
            mLoadMore = true;
        } else {
            mLoadMore = false;
        }

    }

    public abstract void loadPage();
}
