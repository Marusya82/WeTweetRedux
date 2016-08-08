package com.codepath.apps.restclienttemplate.models;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

// parse the JSON + store the date, display state logic or display logic
@Parcel
public class Tweet {
    // list out the attributes
    User user;
    long tid;
    String body;
    String createdAt;
    String imageUrl;
    String videoUrl;
    String likes;
    String retweets;

    public String getLikes() {
        return likes;
    }

    public String getRetweets() {
        return retweets;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public User getUser() {
        return user;
    }

    public String getBody() {
        return body;
    }

    public long getTid() {
        return tid;
    }

    public String getCreatedAt() {
        return createdAt;
    }
//    private User user;

    // deserialize the JSON, build Tweet object
    // Tweet.fromJSON("{ ... }") => Tweet
    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        // extract the values from teh json, store them
        try {
            tweet.body = jsonObject.getString("text");
            tweet.tid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
            tweet.retweets = jsonObject.getString("retweet_count");
            tweet.likes = jsonObject.getString("favorite_count");

            JSONObject entities = jsonObject.getJSONObject("entities");
            JSONArray media = entities.getJSONArray("media");
            if (media.length() != 0) {
                JSONObject obj = media.getJSONObject(0);
                String mediaUrl = obj.getString("media_url");
                String mediaType = obj.getString("type");
                if (mediaType.equals("photo") && !mediaUrl.contains("video")) tweet.imageUrl = mediaUrl;
                else if (mediaType.equals("video") || mediaUrl.contains("video")) tweet.videoUrl = mediaUrl;
            }
            if (tweet.imageUrl != null) Log.d("DEBUG", tweet.imageUrl);
            if (tweet.videoUrl != null) Log.d("DEBUG", tweet.videoUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // store the user object
        return tweet;
    }

    // Tweet.fromJSONArray() => List<Tweet>
    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject tweetJson = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJSON(tweetJson);
                if (tweet != null) {
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }

        }
        return tweets;
    }

}

