package com.codepath.apps.restclienttemplate.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.client.TwitterApplication;
import com.codepath.apps.restclienttemplate.client.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MessagesFragment extends TweetsListFragment {

    private TwitterClient client;
    private View myView;
    private long maxId;

    // newInstance constructor for creating fragment with arguments
    public static MessagesFragment newInstance(int page, String title) {
        MessagesFragment frag = new MessagesFragment();
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
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myView = view;
    }

    @Override
    protected void populateTimeline() {
        RequestParams params = new RequestParams();
        params.put("since_id", 1);
        params.put("count", count);

        client.getMessages(params, new JsonHttpResponseHandler() {

            // SUCCESS
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                // get JSON, deserialize it, create models and add them into adapter, into the data set
                ArrayList<Tweet> newItems = Tweet.fromJSONArray(jsonArray);
                if (!newItems.isEmpty()) {
                    Tweet latestTweet = newItems.get(newItems.size() - 1);
                    maxId = latestTweet.getTid() - 1;
                    refresh(newItems);
                    Log.d("DEBUG", newItems.toString());
                }
            }

            // FAILURE
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse != null) Log.d("DEBUG", errorResponse.toString());
                Snackbar.make(myView, R.string.wrong, Snackbar.LENGTH_INDEFINITE).show();
            }
        });
    }

    @Override
    protected void populateDb() {}

    @Override
    protected void paginate() {
        RequestParams params = new RequestParams();
        params.put("max_id", maxId);
        params.put("count", count);

        client.getMessages(params, new JsonHttpResponseHandler() {

            // SUCCESS
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                // get JSON, deserialize it, create models and add them into adapter, into the data set
                Log.d("DEBUG", jsonArray.toString());
                ArrayList<Tweet> newItems = Tweet.fromJSONArray(jsonArray);
                if (!newItems.isEmpty()) {
                    Tweet latestTweet = newItems.get(newItems.size() - 1);
                    // passing max_id returns <=, adjust it accordingly to avoid duplicate tweets
                    maxId = latestTweet.getTid() - 1;
                    addToTail(newItems);
                }
            }

            // FAILURE
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse != null) Log.d("DEBUG", errorResponse.toString());
                Snackbar.make(myView, R.string.wrong, Snackbar.LENGTH_INDEFINITE).show();
            }
        });
    }
}
