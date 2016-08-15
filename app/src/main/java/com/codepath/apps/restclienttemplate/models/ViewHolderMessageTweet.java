package com.codepath.apps.restclienttemplate.models;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;

public class ViewHolderMessageTweet extends RecyclerView.ViewHolder {

    ImageView ivProfileImage;
    TextView tvUserName;
    TextView tvMessageBody;
    TextView tvTimestamp;

    public ViewHolderMessageTweet(View itemView) {
        super(itemView);
        ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
        tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
        tvMessageBody = (TextView) itemView.findViewById(R.id.tvMessageBody);
        tvTimestamp = (TextView) itemView.findViewById(R.id.tvTimestamp);
    }

    public void setTvMessageBody(TextView tvMessageBody) {
        this.tvMessageBody = tvMessageBody;
    }

    public void setTvTimestamp(TextView tvTimestamp) {
        this.tvTimestamp = tvTimestamp;
    }

    public TextView getTvMessageBody() {
        return tvMessageBody;
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

    public TextView getTvTimestamp() {
        return tvTimestamp;
    }
}
