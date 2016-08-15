package com.codepath.apps.restclienttemplate.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.client.TwitterApplication;
import com.codepath.apps.restclienttemplate.client.TwitterClient;
import com.codepath.apps.restclienttemplate.fragments.ComposeDialogFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetActivity extends AppCompatActivity implements ComposeDialogFragment.ComposeDialogListener {

    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.tvUserName) TextView tvUserName;
    @BindView(R.id.tvUserScreenName) TextView tvUserScreenName;
    @BindView(R.id.tvTweetBody) TextView tvTweetBody;
    @BindView(R.id.tvTimestamp) TextView tvTimestamp;
    @BindView(R.id.ivImage) ImageView ivImage;
    @BindView(R.id.ivReply) ImageView ivReply;
    @BindView(R.id.tvLikes) TextView tvLikes;
    @BindView(R.id.tvLikesText) TextView tvLikesText;
    @BindView(R.id.tvRetweets) TextView tvRetweets;
    @BindView(R.id.tvRetweetsText) TextView tvRetweetsText;
    @BindView(R.id.ivFav) ImageView ivFav;
    @BindView(R.id.ivRetweet) ImageView ivRetweet;

    Context context;
    private TwitterClient client;
    long maxId;
    String profileUrl;
    private Tweet tweet;
    private boolean liked = false;
    private boolean retweeted = false;
//    MenuItem miActionProgressItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);
        ButterKnife.bind(this);
        setupViews();
    }

    private void setupViews() {
        // setup client
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_home);
        actionBar.setDisplayHomeAsUpEnabled(true);

        client = TwitterApplication.getRestClient();

        getProfileImageUrl();
        context = getApplicationContext();
        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        ivProfileImage.setImageResource(android.R.color.transparent); // clear out the old image for a recycler view);
        String imageUrl = tweet.getUser().getProfileImageUrl();
        Glide.with(context).load(imageUrl).bitmapTransform(new RoundedCornersTransformation(context, 5, 0)).into(ivProfileImage);

        tvUserName.setText(tweet.getUser().getName());
        tvTweetBody.setText(tweet.getBody());
        tvUserScreenName.setText(String.format("@%s", tweet.getUser().getScreenName()));
        if (Integer.parseInt(tweet.getLikes()) > 0) {
            tvLikes.setText(tweet.getLikes());
            tvLikesText.setText("LIKES");
        }
        if (Integer.parseInt(tweet.getRetweets()) > 0) {
            tvRetweets.setText(tweet.getRetweets());
            tvRetweetsText.setText("RETWEETS");
        }

        if (liked) ivFav.setImageResource(R.drawable.heart_clicked);
        else ivFav.setImageResource(R.drawable.heart);
        ivFav.setOnClickListener(v -> {
            if (liked) {
                unfavorite();
                ivFav.setImageResource(R.drawable.heart);
                liked = false;
            } else {
                favorite();
                ivFav.setImageResource(R.drawable.heart_clicked);
                liked = true;
            }
        });

        if (retweeted) ivRetweet.setImageResource(R.drawable.retweeted);
        else ivRetweet.setImageResource(R.drawable.retweet);
        ivRetweet.setOnClickListener(v -> {
            if (retweeted) {
                unretweet();
                ivRetweet.setImageResource(R.drawable.retweet);
                retweeted = false;
            } else {
                retweet();
                ivRetweet.setImageResource(R.drawable.retweeted);
                retweeted = true;
            }

        });

        String[] str = tweet.getCreatedAt().split(" ");
        tvTimestamp.setText(String.format("%s %s %s, %s", str[0], str[1], str[2], str[3]));

        ivImage.setImageResource(android.R.color.transparent);
        String tweetImageUrl = tweet.getImageUrl();
        if (tweetImageUrl != null && !tweetImageUrl.isEmpty()) Glide.with(context).load(tweetImageUrl).override(750, 400).centerCrop().into(ivImage);

        ivReply.setImageResource(R.drawable.reply_to);
        ivReply.setOnClickListener(v -> {
            FragmentManager fm = getSupportFragmentManager();
            ComposeDialogFragment composeDialog = ComposeDialogFragment.newInstance("Compose a tweet:");
            Bundle bundle = new Bundle();
            bundle.putString("status_id", String.valueOf(tweet.getTid()));
            bundle.putString("in_reply_to", tweet.getUser().getScreenName());
            bundle.putString("profile_url", profileUrl);
            composeDialog.setArguments(bundle);
            composeDialog.show(fm, "fragment_compose_dialog");
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                // ProjectsActivity is my 'home' activity
                finish();
                return true;
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    public void onFinishTweetDialog(String tweet, String inReplyToStatusId) {
        RequestParams params = new RequestParams();
        params.add("status", tweet);
        params.add("in_reply_to_status_id", inReplyToStatusId);

        if(isNetworkAvailable()) {
            client.postNewTweet(params, new JsonHttpResponseHandler() {

                // SUCCESS
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("DEBUG", response.toString());
                    Snackbar.make(findViewById(android.R.id.content), R.string.posted, Snackbar.LENGTH_SHORT).show();
                    // get JSON, deserialize it, create models and add them into adapter, into the listview
                    Tweet newTweet = Tweet.fromJSON(response);
                    // update max_id and tweets array, notify adapter
                    maxId = newTweet.getTid() - 1;
                }

                // FAILURE
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                    Snackbar.make(findViewById(android.R.id.content), R.string.wrong, Snackbar.LENGTH_SHORT).show();
                }
            });
//            Intent mIntent = new Intent(TweetActivity.this, TimelineActivity.class);
//            mIntent.putExtra("max_id", maxId);
//            startActivity(mIntent);
        }
    }

    private void getProfileImageUrl() {
        if(isNetworkAvailable()) {
            client.getProfileDetails(new JsonHttpResponseHandler() {

                // SUCCESS
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                    // get JSON, deserialize it, create models and add them into adapter, into the data set
                    try {
                        profileUrl = jsonObject.getString("profile_image_url");
                        Log.d("DEBUG", profileUrl);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                // FAILURE
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                    Snackbar.make(findViewById(android.R.id.content), R.string.wrong, Snackbar.LENGTH_SHORT).show();
                }

            });
        }
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public void favorite() {
        RequestParams params = new RequestParams();
        params.add("id", String.valueOf(tweet.getTid()));
        client.postFavorite(params, new JsonHttpResponseHandler() {

            // SUCCESS
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                // get JSON, deserialize it, create models and add them into adapter, into the data set
//                Snackbar.make(findViewById(android.R.id.content), R.string.favorite, Snackbar.LENGTH_SHORT).show();
            }

            // FAILURE
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                Snackbar.make(findViewById(android.R.id.content), R.string.wrong, Snackbar.LENGTH_SHORT).show();
            }

        });
    }

    public void unfavorite() {
        RequestParams params = new RequestParams();
        params.add("id", String.valueOf(tweet.getTid()));
        client.postUnfavorite(params, new JsonHttpResponseHandler() {

            // SUCCESS
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                // get JSON, deserialize it, create models and add them into adapter, into the data set
//                Snackbar.make(findViewById(android.R.id.content), R.string.unfavorite, Snackbar.LENGTH_SHORT).show();
            }

            // FAILURE
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                Snackbar.make(findViewById(android.R.id.content), R.string.wrong, Snackbar.LENGTH_SHORT).show();
            }

        });
    }

    public void retweet() {
        client.postRetweet(tweet.getTid(), new JsonHttpResponseHandler() {

            // SUCCESS
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                // get JSON, deserialize it, create models and add them into adapter, into the data set
                Snackbar.make(findViewById(android.R.id.content), R.string.retweeted, Snackbar.LENGTH_SHORT).show();
            }

            // FAILURE
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                Snackbar.make(findViewById(android.R.id.content), R.string.wrong, Snackbar.LENGTH_SHORT).show();
            }

        });
    }

    public void unretweet() {
        client.postUnretweet(tweet.getTid(), new JsonHttpResponseHandler() {

            // SUCCESS
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                // get JSON, deserialize it, create models and add them into adapter, into the data set
                Snackbar.make(findViewById(android.R.id.content), R.string.unretweeted, Snackbar.LENGTH_SHORT).show();
            }

            // FAILURE
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                Snackbar.make(findViewById(android.R.id.content), R.string.wrong, Snackbar.LENGTH_SHORT).show();
            }

        });
    }
}
