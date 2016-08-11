package com.codepath.apps.restclienttemplate.fragments;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.activities.TweetActivity;
import com.codepath.apps.restclienttemplate.adapters.TweetsRecyclerViewAdapter;
import com.codepath.apps.restclienttemplate.models.DividerItemDecoration;
import com.codepath.apps.restclienttemplate.models.EndlessScrollListener;
import com.codepath.apps.restclienttemplate.models.SampleModel;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class TweetsListFragment extends Fragment {

    protected ArrayList<Tweet> tweets;
    protected TweetsRecyclerViewAdapter adaptTweets;
    @BindView(R.id.rvTweets) RecyclerView rvTweets;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    private Unbinder unbinder;
    protected boolean swipe;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragments_tweets_list, parent, false);
        unbinder = ButterKnife.bind(this, view);
        setupViews();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setupViews() {
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

        // abstract method call
        populateTimeline();

        rvTweets.addOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                paginate();
            }
        });

        SampleModel.ItemClickSupport.addTo(rvTweets).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    Intent i = new Intent(getContext(), TweetActivity.class);
                    Tweet tweet = tweets.get(position);
                    i.putExtra("tweet", Parcels.wrap(tweet));
                    startActivity(i);
                }
        );

        swipeContainer.setOnRefreshListener(() -> {
            swipe = true;
            populateTimeline();
            swipeContainer.setRefreshing(false);
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    protected void addAll(ArrayList<Tweet> newTweets) {
        int curSize = adaptTweets.getItemCount();
        tweets.addAll(newTweets);
        adaptTweets.notifyItemRangeInserted(curSize, newTweets.size());
    }

    protected void addOne(Tweet newTweet) {
        tweets.add(0, newTweet);
        adaptTweets.notifyItemInserted(0);
    }

    protected void addDb(ArrayList<Tweet> savedTweets) {
        tweets.addAll(savedTweets);
        adaptTweets.notifyItemRangeInserted(0, savedTweets.size());
    }

    protected abstract void populateTimeline();

    protected abstract void populateDb();

    protected abstract void paginate();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
