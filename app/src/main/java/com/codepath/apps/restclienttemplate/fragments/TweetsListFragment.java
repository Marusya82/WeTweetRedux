package com.codepath.apps.restclienttemplate.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapters.TweetsRecyclerViewAdapter;
import com.codepath.apps.restclienttemplate.models.DividerItemDecoration;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TweetsListFragment extends Fragment {

    private ArrayList<Tweet> tweets;
    private TweetsRecyclerViewAdapter adaptTweets;
    @BindView(R.id.rvTweets) RecyclerView rvTweets;
    @BindView(R.id.fab) FloatingActionButton fab;
    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragments_tweets_list, parent, false);
        unbinder = ButterKnife.bind(this, view);

        tweets = new ArrayList<>();
        adaptTweets = new TweetsRecyclerViewAdapter(tweets);
        rvTweets.setAdapter(adaptTweets);

        // setup layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false);
        rvTweets.setLayoutManager(layoutManager);
        layoutManager.scrollToPosition(0);

        // setup visual line divider
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        rvTweets.addItemDecoration(itemDecoration);
        rvTweets.setHasFixedSize(false);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void addAll(ArrayList<Tweet> newTweets) {
        int curSize = adaptTweets.getItemCount();
        tweets.addAll(newTweets);
        adaptTweets.notifyItemRangeInserted(curSize, newTweets.size());
    }

    public void addOne(Tweet newTweet) {
        tweets.add(0, newTweet);
        adaptTweets.notifyItemInserted(0);
    }

    public void addDb(ArrayList<Tweet> savedTweets) {
        tweets.addAll(savedTweets);
        adaptTweets.notifyItemRangeInserted(0, savedTweets.size());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
