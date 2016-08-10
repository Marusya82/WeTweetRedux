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

public class MentionsTimelineFragment extends TweetsListFragment {

    private TwitterClient client;
    private boolean firstQuery = true;
    int count = 20;
    private long maxId;
    private String title;
    private int page;

    // newInstance constructor for creating fragment with arguments
    public static MentionsTimelineFragment newInstance(int page, String title) {
        MentionsTimelineFragment frag = new MentionsTimelineFragment();
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
        populateMentionsTimeline();
    }

    private void populateMentionsTimeline() {
        RequestParams params = new RequestParams();
        if (firstQuery) {
            params.put("since_id", 1);
            firstQuery = false;
        } else params.put("max_id", maxId);

        params.put("screen_name", "MarinaGetAway");
        params.put("count", count);

        client.getMentions(params, new JsonHttpResponseHandler() {

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
