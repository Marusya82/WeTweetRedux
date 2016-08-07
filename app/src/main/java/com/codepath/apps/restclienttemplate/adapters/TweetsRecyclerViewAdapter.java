package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.ViewHolderImageTweet;
import com.codepath.apps.restclienttemplate.models.ViewHolderSimpleTweet;
import com.codepath.apps.restclienttemplate.models.ViewHolderVideoTweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Tweet> mTweets;
    Context context;
    private final int SIMPLE = 0, IMAGE = 1, VIDEO = 2;

    public TweetsRecyclerViewAdapter(ArrayList<Tweet> tweets) {
        this.mTweets = tweets;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // later here will be different types of tweets (with image/video or simple text)
        switch (viewType) {
            case SIMPLE:
                View view1 = inflater.inflate(R.layout.viewholder_simple_tweet, viewGroup, false);
                viewHolder = new ViewHolderSimpleTweet(view1);
                break;
            case IMAGE:
                View view2 = inflater.inflate(R.layout.viewholder_image_tweet, viewGroup, false);
                viewHolder = new ViewHolderImageTweet(view2);
                break;
//            case VIDEO:
//                View view3 = inflater.inflate(R.layout.viewholder_video_tweet, viewGroup, false);
//                viewHolder = new ViewHolderVideoTweet(view3);
//                break;
            default:
                View view4 = inflater.inflate(R.layout.viewholder_simple_tweet, viewGroup, false);
                viewHolder = new ViewHolderSimpleTweet(view4);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch(viewHolder.getItemViewType()) {
            case SIMPLE:
                ViewHolderSimpleTweet vh1 = (ViewHolderSimpleTweet) viewHolder;
                configureViewHolderSimpleTweet(vh1, position);
                break;
            case IMAGE:
                ViewHolderImageTweet vh2 = (ViewHolderImageTweet) viewHolder;
                configureViewHolderImageTweet(vh2, position);
                break;
//            case VIDEO:
//                ViewHolderVideoTweet vh3 = (ViewHolderVideoTweet) viewHolder;
//                configureViewHolderVideoTweet(vh3, position);
//                break;
            default:
                ViewHolderSimpleTweet vh4 = (ViewHolderSimpleTweet) viewHolder;
                configureViewHolderSimpleTweet(vh4, position);
                break;
        }
    }

    private void configureViewHolderVideoTweet(ViewHolderVideoTweet holder, int position) {
        Tweet tweet = mTweets.get(position);
        Log.d("DEBUG", tweet.toString());
        if (tweet != null) {
            ImageView profileImage = holder.getIvProfileImage();
            profileImage.setImageResource(android.R.color.transparent); // clear out the old image for a recycler view);
            String imageUrl = tweet.getUser().getProfileImageUrl();
            Glide.with(context).load(imageUrl).bitmapTransform(new RoundedCornersTransformation(context, 5, 0)).into(profileImage);

            holder.getTvUserName().setText(tweet.getUser().getName());
            holder.getTvTweetBody().setText(tweet.getBody());
            holder.getTvTimestamp().setText(getRelativeTimeAgo(tweet.getCreatedAt()));
            holder.getTvUserScreenName().setText(String.format("@%s", tweet.getUser().getScreenName()));

//            holder.getTextureView().setMediaController(holder.getPlayerController());
//            holder.getTextureView().setVideo(tweet.getVideoUrl());
        }
    }

    private void configureViewHolderSimpleTweet(ViewHolderSimpleTweet holder, int position) {
        Tweet tweet = mTweets.get(position);
        Log.d("DEBUG", tweet.toString());
        if (tweet != null) {
            ImageView profileImage = holder.getIvProfileImage();
            profileImage.setImageResource(android.R.color.transparent); // clear out the old image for a recycler view);
            String imageUrl = tweet.getUser().getProfileImageUrl();
            Glide.with(context).load(imageUrl).bitmapTransform(new RoundedCornersTransformation(context, 5, 0)).into(profileImage);

            holder.getTvUserName().setText(tweet.getUser().getName());
            holder.getTvTweetBody().setText(tweet.getBody());
            holder.getTvTimestamp().setText(getRelativeTimeAgo(tweet.getCreatedAt()));
            holder.getTvUserScreenName().setText(String.format("@%s", tweet.getUser().getScreenName()));
        }
    }

    private void configureViewHolderImageTweet(ViewHolderImageTweet holder, int position) {
        Tweet tweet = mTweets.get(position);
        Log.d("DEBUG", tweet.toString());
        if (tweet != null) {
            ImageView profileImage = holder.getIvProfileImage();
            profileImage.setImageResource(android.R.color.transparent); // clear out the old image for a recycler view);
            String imageUrl = tweet.getUser().getProfileImageUrl();
            Glide.with(context).load(imageUrl).bitmapTransform(new RoundedCornersTransformation(context, 5, 0)).into(profileImage);

            holder.getTvUserName().setText(tweet.getUser().getName());
            holder.getTvTweetBody().setText(tweet.getBody());
            holder.getTvTimestamp().setText(getRelativeTimeAgo(tweet.getCreatedAt()));
            holder.getTvUserScreenName().setText(String.format("@%s", tweet.getUser().getScreenName()));

            ImageView tweetImage = holder.getIvImage();
            tweetImage.setImageResource(android.R.color.transparent);
            String tweetImageUrl = tweet.getImageUrl();
            Glide.with(context).load(tweetImageUrl).override(650, 400).centerCrop().into(tweetImage);
        }
    }
    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return this.mTweets.size();
    }

    //Returns the view type of the item at position for the purposes of view recycling
    @Override
    public int getItemViewType(int position) {
        // later here will be different types of tweets (with image/video or simple text)
        Tweet inspect = mTweets.get(position);
        if (inspect.getImageUrl() != null && !inspect.getImageUrl().isEmpty()) return IMAGE;
//        else if (inspect.getVideoUrl() != null && !inspect.getVideoUrl().isEmpty()) return VIDEO;
        else return SIMPLE;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String[] parts = relativeDate.split(" ");
        String dateToShow = parts[0] + parts[1].charAt(0);

        return dateToShow;
    }
}

