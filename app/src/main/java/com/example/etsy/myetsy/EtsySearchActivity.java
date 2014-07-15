package com.example.etsy.myetsy;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class EtsySearchActivity extends Activity {

    // SEARCH URLS
    private static final String API_KEY = "liwecjs0c3ssk6let4p1wqt9";
    private static final String TRENDING_LISTINGS_URL = "https://api.etsy.com/v2/listings/trending?" +
            "api_key=" + API_KEY + "&includes=MainImage";
    private static final String ACTIVE_LISTINGS_URL = "https://api.etsy.com/v2/listings/active?" +
            "api_key=" + API_KEY + "&includes=MainImage&keywords=";

    private ActionBar mActionBar;
    private ImageLoader mImageLoader;
    private NetworkImageView mNetworkImageView;
    private GridView mGridView;
    private MainImageAdapter mImageAdapter;
    private LinearLayout mProgressBar;
    private int nextPage = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etsy_search);

        mActionBar = this.getActionBar();
        mGridView = (GridView) findViewById(R.id.gridView);
        mImageLoader = VolleySingleton.getInstance(this).getImageLoader();
        mImageAdapter = new MainImageAdapter();
        mGridView.setAdapter(mImageAdapter);
        mProgressBar = (LinearLayout) findViewById(R.id.loadingMore);

        // Start with a clean slate
        ListingCache.getInstance().clearResults();
        loadPage();

        mGridView.setOnScrollListener(new LoadMoreListener() {
            @Override
            public void loadPage() {
                EtsySearchActivity.this.loadPage();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.etsy_search, menu);

        // Setup search
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    private String getSearchUrl() {

        String keywords = getKeywords();
        if (keywords != null) {
            try {
                return ACTIVE_LISTINGS_URL + URLEncoder.encode(keywords, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                Utils.displayMessage(this, R.string.bad_keywords);
                return TRENDING_LISTINGS_URL;
            }
        } else {
            return TRENDING_LISTINGS_URL;
        }

    }

    private String getKeywords() {
        if (getIntent() != null && Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
            return getIntent().getStringExtra(SearchManager.QUERY);
        } else {
            return null;
        }
    }

    private String getCurrentTitle() {
       String keywords = getKeywords();
       if (keywords == null) {
           return "Etsy results: Trending";
       } else {
           return "Etsy results: " + getKeywords();
       }
    }

    private void loadPage() {
        if (nextPage > 0) {
            if (nextPage == 1) {
                mActionBar.setTitle(getCurrentTitle());
            }
            loadPage(nextPage);
        } else {
            Toast.makeText(this, "That's all folks, check back for more goods later!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadPage(int page) {

        // Check connectivity before loading
        if (Utils.haveConnectivity(this)) {

            mProgressBar.setVisibility(View.VISIBLE);
            JsonObjectRequest request
                    = new JsonObjectRequest(getSearchUrl() + "&page=" + page, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            SearchResponse searchResponse = Utils.parseJsonFeed(response);
                            nextPage = searchResponse.getNextPage();
                            if (searchResponse.getCount() == 0) {
                                Utils.displayMessage(EtsySearchActivity.this, R.string.no_results);
                            }
                            mProgressBar.setVisibility(View.GONE);
                            mImageAdapter.notifyDataSetChanged();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mProgressBar.setVisibility(View.GONE);
                            Utils.displayMessage(EtsySearchActivity.this, R.string.generic_server_error);
                        }
                    }
            );

            VolleySingleton.getInstance(this).getRequestQueue().add(request);
        } else {
            Utils.displayMessage(EtsySearchActivity.this, R.string.no_connectivity_msg);
        }
    }

    static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }

    class MainImageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return ListingCache.getInstance().getListingCount();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = convertView;
            final ViewHolder gridViewImageHolder;

            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.listing_item, parent, false);
                gridViewImageHolder = new ViewHolder();
                gridViewImageHolder.imageView = (ImageView) view.findViewById(R.id.networkImageView);
                gridViewImageHolder.textView = (TextView) view.findViewById(R.id.imageTitle);
                view.setTag(gridViewImageHolder);
            } else {
                gridViewImageHolder = (ViewHolder) view.getTag();
            }

            Listing listing = ListingCache.getInstance().getListing(position);

            // Set the image for volley to load
            mNetworkImageView = (NetworkImageView) gridViewImageHolder.imageView;
            mNetworkImageView.setAdjustViewBounds(true);
            mNetworkImageView.setImageUrl(listing.getImageUrl(), mImageLoader);

            // Set the title
            gridViewImageHolder.textView.setText(listing.getTitle());

            return view;
        }
    }

}
