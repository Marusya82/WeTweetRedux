package com.codepath.apps.restclienttemplate.models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;


public class ViewHolderSimpleTweet extends RecyclerView.ViewHolder {

    ImageView ivProfileImage;
    TextView tvUserName;
    TextView tvUserScreenName;
    TextView tvTweetBody;
    TextView tvTimestamp;

    public ViewHolderSimpleTweet(View itemView) {
        super(itemView);
        ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
        tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
        tvUserScreenName = (TextView) itemView.findViewById(R.id.tvUserScreenName);
        tvTweetBody = (TextView) itemView.findViewById(R.id.tvTweetBody);
        tvTimestamp = (TextView) itemView.findViewById(R.id.tvTimestamp);
        tvUserScreenName = (TextView) itemView.findViewById(R.id.tvUserScreenName);
    }

    public TextView getTvUserScreenName() {
        return tvUserScreenName;
    }

    public ImageView getIvProfileImage() {
        return ivProfileImage;
    }

    public void setIvProfileImage(ImageView image) {
        this.ivProfileImage = image;
    }

    public TextView getTvUserName() {
        return tvUserName;
    }

    public void setTvUserName(TextView name) {
        this.tvUserName = name;
    }

    public TextView getTvTweetBody() {
        return tvTweetBody;
    }

    public void setTvTweetBody(TextView body) {
        this.tvTweetBody = body;
    }

    public TextView getTvTimestamp() {
        return tvTimestamp;
    }

}
