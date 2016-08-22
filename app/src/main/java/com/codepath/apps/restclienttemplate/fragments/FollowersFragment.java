package com.codepath.apps.restclienttemplate.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.codepath.apps.restclienttemplate.client.TwitterApplication;
import com.codepath.apps.restclienttemplate.client.TwitterClient;

public class FollowersFragment extends TweetsListFragment {

    private TwitterClient client;
    private long maxId;
    private View myView;

    // newInstance constructor for creating fragment with arguments
    public static FollowersFragment newInstance(String who) {
        FollowersFragment frag = new FollowersFragment();
        Bundle args = new Bundle();
        args.putString("who", who);
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

    }

    @Override
    protected void populateDb() {

    }

    @Override
    protected void paginate() {

    }
}
