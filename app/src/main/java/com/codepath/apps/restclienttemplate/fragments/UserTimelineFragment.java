package com.codepath.apps.restclienttemplate.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.client.TwitterApplication;
import com.codepath.apps.restclienttemplate.client.TwitterClient;
import com.codepath.apps.restclienttemplate.database.UserTimelineDatabaseHelper;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class UserTimelineFragment extends TweetsListFragment {

    private TwitterClient client;
    private long maxId;
    private View myView;
    UserTimelineDatabaseHelper helper;

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
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myView = view;
    }

    @Override
    protected void populateTimeline() {
        String userScreenName = getArguments().getString("screen_name");
        RequestParams params = new RequestParams();
        params.put("since_id", 1);
        params.put("screen_name", userScreenName);
        params.put("count", count);

        client.getUserTimeline(params, new JsonHttpResponseHandler() {
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
                    refresh(newItems);
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
    protected void populateDb() {
        helper = UserTimelineDatabaseHelper.getInstance(getActivity());
        ArrayList<Tweet> savedTweets = helper.getAll();
        addDb(savedTweets);
    }

    @Override
    protected void paginate() {
        String userScreenName = getArguments().getString("screen_name");
        RequestParams params = new RequestParams();
        params.put("max_id", maxId);
        params.put("screen_name", userScreenName);
        params.put("count", count);

        client.getUserTimeline(params, new JsonHttpResponseHandler() {
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
