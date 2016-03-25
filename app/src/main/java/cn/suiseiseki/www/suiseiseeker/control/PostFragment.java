package cn.suiseiseki.www.suiseiseeker.control;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import cn.suiseiseki.www.suiseiseeker.R;

/**
 * Created by Suiseiseki/shuikeyi on 2016/3/23.
 */
public class PostFragment extends Fragment {

    private final static String TAG = "PostFragment";

    /* The View */
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private NestedScrollView mNestedScrollView;
    private AppBarLayout mAppBarLayout;
    private CoordinatorLayout mCoordinatorLayout;
    private ImageView featuredImageView;
    private WebView mWebView;
    /* The Label */
    private int id;
    private String mTitle;
    private String mContent;
    private String mUrl;
    private String mFeaturedImageurl;
    /* The Extra / argument */
    final static String ARG_ID = "postfragment.id";
    final static String ARG_TITLE = "postfragment.title";
    final static String ARG_URL = "postfragment.url";
    final static String ARG_CONTENT = "postfragment.content";
    final static String ARG_IMAGEURL = "postfragment.imageurl";
    final static String ARG_DATE = "postfragment.date";
    final static String ARG_AUTHOR = "postfragment.authorname";


    /**
     * Define a callback
     */
    public interface onPostListener
    {
        void onHomePressed();
    }
    private onPostListener mCallback;
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mCallback = (onPostListener)activity;
    }
    public void onDetach()
    {
        super.onDetach();
        mCallback = null;
    }
    /**
     * The onCreate Method
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }
    /**
     * The onCreateView() Method,initialize the layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup parent,Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_post,parent,false);
        /* Find Views */
        mToolbar = (Toolbar)v.findViewById(R.id.post_toolbar);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout)v.findViewById(R.id.post_collapsingtoorbarlayout);
        mCollapsingToolbarLayout.setTitle(getString(R.string.app_name));
        mNestedScrollView = (NestedScrollView)v.findViewById(R.id.post_nestedScrollView);
        mAppBarLayout = (AppBarLayout)v.findViewById(R.id.post_AppBarLayout);
        mCoordinatorLayout = (CoordinatorLayout)v.findViewById(R.id.post_coordinatorLayout);
        featuredImageView = (ImageView)v.findViewById(R.id.featuredImage);
        mWebView = (WebView)v.findViewById(R.id.webview);
        return v;
    }
    /**
     *  Cannot reach bundle when fragment was already existed,we have to do it in UI thread.
     */
    public void setUIArguments(final Bundle args)
    {
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                // clear the content by load empty html
                mWebView.loadData("", "text/html; charset=UTF-8", null);
                featuredImageView.setImageBitmap(null);
                // get data from arguments
                id = args.getInt(ARG_ID);
                mTitle = args.getString(ARG_TITLE);
                String date = args.getString(ARG_DATE);
                String author = args.getString(ARG_AUTHOR);
                mContent = args.getString(ARG_CONTENT);
                mUrl = args.getString(ARG_URL);
                mFeaturedImageurl = args.getString(ARG_IMAGEURL);

                // download featured image
                Glide.with(PostFragment.this)
                        .load(mFeaturedImageurl)
                        .centerCrop()
                        .into(featuredImageView);
                // Build HTML content
                // CSS
                String html = "<style>img{max-width:100%;height:auto;} " +
                        "iframe{width:100%;}</style> ";
                // Article Title
                html += "<h2>" + mTitle + "</h2> ";
                // Date & author
                html += "<h4>" + date + " " + author + "</h4>";
                // The actual content
                html += mContent;

                mWebView.getSettings().setJavaScriptEnabled(true);
                mWebView.setWebChromeClient(new WebChromeClient());
                // Loading html data
                mWebView.loadDataWithBaseURL("",html, "text/html", "utf-8", null);

                //Reset Action Bar and expand Toolbar
                ((MainActivity) getActivity()).setSupportActionBar(mToolbar);
                ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                expandToolbar();

                // delay some time to wait html loading
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mNestedScrollView.smoothScrollTo(0, 0);
                    }
                },666);
            }
        }; // The define of Runnable ends
        getActivity().runOnUiThread(myRunnable);
    }

    /**
     * Expand the collapsed toolbar
     */
    private void expandToolbar()
    {
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams)mAppBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior)layoutParams.getBehavior();
        // set the behavior of AppBar
        if(behavior != null)
        {
            behavior.setTopAndBottomOffset(0);
            behavior.onNestedPreScroll(mCoordinatorLayout, mAppBarLayout, null, 0, 1, new int[2]);
        }
    }
    /**
     * Set the menu of fragment
     */
    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater)
    {
        inflater.inflate(R.menu.post_menu,menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId()){
            case android.R.id.home: mCallback.onHomePressed();return true;
            case R.id.share_post_item: return true;
            case R.id.delete_post_item: return true;
            default: return super.onOptionsItemSelected(item);
        }
    }
}