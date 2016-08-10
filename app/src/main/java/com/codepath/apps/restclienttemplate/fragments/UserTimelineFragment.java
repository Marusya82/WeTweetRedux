package com.codepath.apps.restclienttemplate.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codepath.apps.restclienttemplate.client.TwitterApplication;
import com.codepath.apps.restclienttemplate.client.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class UserTimelineFragment extends TweetsListFragment {

    private TwitterClient client;
    private boolean firstQuery = true;
    private long maxId;
    private String title;
    private int page;

    // newInstance constructor for creating fragment with arguments
    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment frag = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        populateUserTimeline();
    }

    private void populateUserTimeline() {
        String userScreenName = getArguments().getString("screen_name");
        RequestParams params = new RequestParams();
        if (firstQuery) {
            params.put("since_id", 1);
            firstQuery = false;
        } else params.put("max_id", maxId);

        client.getUserTimeline(userScreenName, new JsonHttpResponseHandler() {
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
            }

            // FAILURE
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
//                Snackbar.make(findViewById(android.R.id.content), R.string.wrong, Snackbar.LENGTH_INDEFINITE).show();
            }
        });
    }
}
