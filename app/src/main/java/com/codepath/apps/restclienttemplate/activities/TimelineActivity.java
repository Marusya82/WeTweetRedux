package com.codepath.apps.restclienttemplate.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapters.TweetsRecyclerViewAdapter;
import com.codepath.apps.restclienttemplate.client.TwitterApplication;
import com.codepath.apps.restclienttemplate.client.TwitterClient;
import com.codepath.apps.restclienttemplate.database.TweetsDatabaseHelper;
import com.codepath.apps.restclienttemplate.fragments.ComposeDialogFragment;
import com.codepath.apps.restclienttemplate.models.DividerItemDecoration;
import com.codepath.apps.restclienttemplate.models.EndlessScrollListener;
import com.codepath.apps.restclienttemplate.models.ItemClickSupport;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements ComposeDialogFragment.ComposeDialogListener {

    private TwitterClient client;
    ArrayList<Tweet> tweets;
    TweetsRecyclerViewAdapter adaptTweets;

    MenuItem miActionProgressItem;

    long maxID;
    boolean firstQuery = true;
    final int count = 50;

    String profileUrl;

    // database instance
    TweetsDatabaseHelper helper;

    @BindView(R.id.rvTweets) RecyclerView rvTweets;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
//    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        setupViews();
    }

    public void setupViews() {
        //        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.birdie_small);
        actionBar.setDisplayUseLogoEnabled(true);

        // get db instance and construct the data source
        helper = TweetsDatabaseHelper.getInstance(this);

        tweets = new ArrayList<>();
        adaptTweets = new TweetsRecyclerViewAdapter(tweets);
        rvTweets.setAdapter(adaptTweets);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayout.VERTICAL, false);
        rvTweets.setLayoutManager(layoutManager);
        layoutManager.scrollToPosition(0);

        // setup visual line divider
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        rvTweets.addItemDecoration(itemDecoration);
        rvTweets.setHasFixedSize(false);

        // setup client
        client = TwitterApplication.getRestClient();

        if(isNetworkAvailable()) {
            getProfileImageUrl();

            populateTimeline();

            rvTweets.addOnScrollListener(new EndlessScrollListener(layoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    populateTimeline();
                }
            });

            ItemClickSupport.addTo(rvTweets).setOnItemClickListener(
                    (recyclerView, position, v) -> {
                        Intent i = new Intent(getApplicationContext(), TweetActivity.class);
                        Tweet tweet = tweets.get(position);
                        i.putExtra("tweet", Parcels.wrap(tweet));
                        startActivity(i);
                    }
            );

            fab.setOnClickListener(v -> {
                FragmentManager fm = getSupportFragmentManager();
                ComposeDialogFragment composeDialog = ComposeDialogFragment.newInstance("Compose a tweet:");
                Bundle bundle = new Bundle();
                bundle.putString("profile_url", profileUrl);
                composeDialog.setArguments(bundle);
                composeDialog.show(fm, "fragment_compose_dialog");
            });

            swipeContainer.setOnRefreshListener(() -> {

                RequestParams params = new RequestParams();
                params.put("since_id", 1);
                params.put("count", count);

                if (isNetworkAvailable()) {
                    client.getHomeTimeline(params, new JsonHttpResponseHandler() {

                        // SUCCESS
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                            // get JSON, deserialize it, create models and add them into adapter, into the data set
                            int curSize = adaptTweets.getItemCount();
                            tweets.clear();
                            adaptTweets.notifyItemRangeRemoved(0, curSize);
                            ArrayList<Tweet> newItems = Tweet.fromJSONArray(jsonArray);
                            Tweet latestTweet = newItems.get(newItems.size() - 1);
                            // passing max_id returns <=, adjust it accordingly to avoid duplicate tweets
                            maxID = latestTweet.getTid() - 1;
                            tweets.addAll(newItems);
                            // curSize should represent the first element that got added, newItems.size() represents the itemCount
                            adaptTweets.notifyItemRangeInserted(curSize, newItems.size());
                            //                        rvTweets.invalidate();
                            //                    hideProgressBar();
                        }

                        // FAILURE
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("DEBUG", errorResponse.toString());
                            Snackbar.make(findViewById(android.R.id.content), R.string.wrong, Snackbar.LENGTH_INDEFINITE).show();
                        }

                    });
                }

                swipeContainer.setRefreshing(false);

            });
            // Configure the refreshing colors
            swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
        } else {
            Snackbar.make(findViewById(android.R.id.content), R.string.offline, Snackbar.LENGTH_INDEFINITE).show();
            int curSize = adaptTweets.getItemCount();
            ArrayList<Tweet> savedTweets = helper.getAll();
            tweets.addAll(savedTweets);
            adaptTweets.notifyItemRangeInserted(curSize, savedTweets.size());
            Log.d("DEBUG", helper.getAll().toString());
        }
    }

    private void getProfileImageUrl() {
        if(isNetworkAvailable()) {
            client.getProfileImageUrl(new JsonHttpResponseHandler() {

                // SUCCESS
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                    // get JSON, deserialize it, create models and add them into adapter, into the data set
                    try {
                        profileUrl = jsonObject.getString("profile_image_url");
                        Log.d("DEBUG", profileUrl);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                // FAILURE
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                    Snackbar.make(findViewById(android.R.id.content), R.string.wrong, Snackbar.LENGTH_INDEFINITE).show();
                }

            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        return super.onPrepareOptionsMenu(menu);
    }

    // send an API request to get the timeline json
    private void populateTimeline() {
//        showProgressBar();
        RequestParams params = new RequestParams();
        if (firstQuery) {
            params.put("since_id", 1);
            firstQuery = false;
        } else params.put("max_id", maxID);
        params.put("count", count);

        if(isNetworkAvailable()) {
            client.getHomeTimeline(params, new JsonHttpResponseHandler() {

                // SUCCESS
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                    // get JSON, deserialize it, create models and add them into adapter, into the data set
                    Log.d("DEBUG", jsonArray.toString());
                    int curSize = adaptTweets.getItemCount();

                    ArrayList<Tweet> newItems = Tweet.fromJSONArray(jsonArray);
                    Tweet latestTweet = newItems.get(newItems.size() - 1);
                    // passing max_id returns <=, adjust it accordingly to avoid duplicate tweets
                    maxID = latestTweet.getTid() - 1;

                    tweets.addAll(newItems);
                    // curSize should represent the first element that got added, newItems.size() represents the itemCount
                    adaptTweets.notifyItemRangeInserted(curSize, newItems.size());
                    helper.deleteAll();
                    helper.addAll(newItems);
//                    hideProgressBar();
                }

                // FAILURE
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                    Snackbar.make(findViewById(android.R.id.content), R.string.wrong, Snackbar.LENGTH_INDEFINITE).show();
                }
            });
        }
    }

    public void showProgressBar() {
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        miActionProgressItem.setVisible(false);
    }

    @Override
    public void onFinishTweetDialog(String tweet, String inReplyToStatusId) {
        Snackbar.make(findViewById(android.R.id.content), tweet, Snackbar.LENGTH_SHORT).show();
        RequestParams params = new RequestParams();
        params.add("status", tweet);

        if(isNetworkAvailable()) {
            client.postNewTweet(params, new JsonHttpResponseHandler() {

                // SUCCESS
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("DEBUG", response.toString());
                    // get JSON, deserialize it, create models and add them into adapter, into the listview
                    Tweet newTweet = Tweet.fromJSON(response);
                    // update max_id and tweets array, notify adapter
                    maxID = newTweet.getTid() - 1;
                    tweets.add(0, newTweet);
                    adaptTweets.notifyItemInserted(0);
                }

                // FAILURE
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                    Snackbar.make(findViewById(android.R.id.content), R.string.wrong, Snackbar.LENGTH_INDEFINITE).show();
                }
            });
        }
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    // when coming back from Tweet Activity, update the max_id?
    public void onSubmit(View v) {
        // closes the activity and returns to first screen
        maxID = getIntent().getLongExtra("maxId", 0);
        this.finish();
    }
}