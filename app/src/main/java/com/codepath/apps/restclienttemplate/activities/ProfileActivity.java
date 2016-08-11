package com.codepath.apps.restclienttemplate.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.client.TwitterApplication;
import com.codepath.apps.restclienttemplate.client.TwitterClient;
import com.codepath.apps.restclienttemplate.fragments.UserTimelineFragment;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.ivBackgroundImage) ImageView ivBackgroundUrl;
    @BindView(R.id.tvUserName) TextView tvUserName;
    @BindView(R.id.tvUserScreenName) TextView tvUserScreenName;
    @BindView(R.id.tvTagline) TextView tvTagline;
    @BindView(R.id.tvFollowing) TextView tvFollowing;
    @BindView(R.id.tvFollowers) TextView tvFollowers;
    @BindView(R.id.tvFollowingText) TextView tvFollowingText;
    @BindView(R.id.tvFollowersText) TextView tvFollowersText;

    TwitterClient client;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_home);
        actionBar.setDisplayHomeAsUpEnabled(true);

        String screenName = getIntent().getStringExtra("screen_name");
//        Log.d("DEBUG", screenName);
        user = Parcels.unwrap(getIntent().getParcelableExtra("user"));
        if (user == null) {
            client = TwitterApplication.getRestClient();
            // get the account info
            client.getProfileDetails(new JsonHttpResponseHandler() {
                // SUCCESS
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonResponse) {
                    // get JSON, deserialize it, create models and add them into adapter, into the data set
                    Log.d("DEBUG", jsonResponse.toString());
                    user = User.fromJSON(jsonResponse);
                    // my current user information
                    populateProfileHeader(user);
                }

                // FAILURE
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    if (errorResponse != null) Log.d("DEBUG", errorResponse.toString());
                    // Snackbar.make(findViewById(android.R.id.content), R.string.wrong, Snackbar.LENGTH_INDEFINITE).show();
                }
            });
        } else {
            // whatever is passed in the intent
            populateProfileHeader(user);
        }

        if (savedInstanceState == null) {
            UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(screenName);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flBody, userTimelineFragment);
            ft.commit();
        }
    }

    private void populateProfileHeader(User user) {
        ivBackgroundUrl.setImageResource(android.R.color.transparent);
        String backImageUrl = user.getBackGroundUrl();
        Glide.with(this).load(backImageUrl).into(ivBackgroundUrl);
//        ivProfileImage.setImageResource(android.R.color.transparent); // clear out the old image for a recycler view);
//        String imageUrl = user.getProfileImageUrl();
//        Glide.with(this).load(imageUrl).bitmapTransform(new RoundedCornersTransformation(this, 5, 0)).into(ivProfileImage);
        tvUserName.setText(user.getName());
        tvTagline.setText(user.getTagline());
        tvUserScreenName.setText(String.format("@%s", user.getScreenName()));
        if (Integer.parseInt(user.getFollowing()) > 0) {
            tvFollowing.setText(user.getFollowing());
            tvFollowingText.setText("FOLLOWING");
        }
        if (Integer.parseInt(user.getFollowers()) > 0) {
            tvFollowers.setText(user.getFollowers());
            tvFollowersText.setText("FOLLOWERS");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // ProjectsActivity is my 'home' activity
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
