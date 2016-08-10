package com.codepath.apps.restclienttemplate.fragments;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;

import com.codepath.apps.restclienttemplate.client.TwitterApplication;
import com.codepath.apps.restclienttemplate.client.TwitterClient;
import com.codepath.apps.restclienttemplate.database.TweetsDatabaseHelper;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class HomeTimelineFragment extends TweetsListFragment implements ComposeDialogFragment.ComposeDialogListener {

    private TwitterClient client;
    private boolean firstQuery = true;
    int count = 20;
    private long maxId;
    private String title;
    private int page;
    private String profileUrl;
    TweetsDatabaseHelper helper;

    // newInstance constructor for creating fragment with arguments
    public static HomeTimelineFragment newInstance(int page, String title) {
        HomeTimelineFragment frag = new HomeTimelineFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = TwitterApplication.getRestClient();

        // get db instance and construct the data source
        helper = TweetsDatabaseHelper.getInstance(getActivity());

        if (isNetworkAvailable()) {
            getProfileImageUrl();
            populateTimeline();
        } else {
            ArrayList<Tweet> savedTweets = helper.getAll();
            addDb(savedTweets);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(v -> {
            FragmentManager fm = getFragmentManager();
            ComposeDialogFragment composeDialog = ComposeDialogFragment.newInstance("Compose a tweet:");
            composeDialog.setTargetFragment(HomeTimelineFragment.this, 300);
            Bundle bundle = new Bundle();
            bundle.putString("profile_url", profileUrl);
            composeDialog.setArguments(bundle);
            composeDialog.show(fm, "fragment_compose_dialog");
        });
    }

    private void populateTimeline() {
        RequestParams params = new RequestParams();
        if (firstQuery) {
            params.put("since_id", 1);
            firstQuery = false;
        } else params.put("max_id", maxId);

        params.put("count", count);

        client.getHomeTimeline(params, new JsonHttpResponseHandler() {

            // SUCCESS
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                // get JSON, deserialize it, create models and add them into adapter, into the data set
                Log.d("DEBUG", jsonArray.toString());
                ArrayList<Tweet> newItems = Tweet.fromJSONArray(jsonArray);
                Tweet latestTweet = newItems.get(newItems.size() - 1);
                // passing max_id returns <=, adjust it accordingly to avoid duplicate tweets
                maxId = latestTweet.getTid() - 1;
                addAll(newItems);
                helper.deleteAll();
                helper.addAll(newItems);
            }

            // FAILURE
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
//                Snackbar.make(findViewById(android.R.id.content), R.string.wrong, Snackbar.LENGTH_INDEFINITE).show();
            }
        });
    }

    private void getProfileImageUrl() {
        if(isNetworkAvailable()) {
            client.getProfileDetails(new JsonHttpResponseHandler() {

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
//                    Snackbar.make(findViewById(android.R.id.content), R.string.wrong, Snackbar.LENGTH_INDEFINITE).show();
                }

            });
        }
    }

    @Override
    public void onFinishTweetDialog(String tweet, String inReplyToStatusId) {
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
                    maxId = newTweet.getTid() - 1;
                    addOne(newTweet);
                }

                // FAILURE
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
//                    Snackbar.make(findViewById(android.R.id.content), R.string.wrong, Snackbar.LENGTH_INDEFINITE).show();
                }
            });
        }
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

}
