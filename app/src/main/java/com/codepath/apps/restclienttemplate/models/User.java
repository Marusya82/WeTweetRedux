package com.codepath.apps.restclienttemplate.models;


import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class User {

    // list attributes
    String name;
    long uid;
    String screenName;
    String profileImageUrl;
    String tagline;
    String followersCount;
    String followingCount;
    String backGroundUrl;

    public String getBackGroundUrl() {
        return backGroundUrl;
    }

    public String getTagline() {
        return tagline;
    }

    public String getFollowers() {
        return followersCount;
    }

    public String getFollowing() {
        return followingCount;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    // deserialize the user json => User
    public static User fromJSON(JSONObject jsonObject) {
        User user = new User();
        // extract the values from the json, store them
        try {
            user.name = jsonObject.getString("name");
            user.uid = jsonObject.getLong("id");
            user.screenName = jsonObject.getString("screen_name");
            user.profileImageUrl = jsonObject.getString("profile_image_url");
            user.tagline = jsonObject.getString("description");
            user.followersCount = jsonObject.getString("followers_count");
            user.followingCount = jsonObject.getString("friends_count");
            user.backGroundUrl = jsonObject.getString("profile_banner_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // tweet.user = ...
        return user;
    }
}
