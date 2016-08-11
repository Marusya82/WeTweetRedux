package com.codepath.apps.restclienttemplate.client;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1/"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "twKvmHhqnyY19Dl7i99Bvrwnl";       // Change this
	public static final String REST_CONSUMER_SECRET = "tnO6DuXhnrWxIiSytJlJ2kGuog7XNCyqLJnCYnytwRPGTdWcn9"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cpmysimpletweets"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	// CHANGE THIS
	// DEFINE METHODS for different API endpoints here
	public void getInterestingnessList(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("?nojsoncallback=1&method=flickr.interestingness.getList");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("format", "json");
		client.get(apiUrl, params, handler);
	}

	// EACH METHOD == ENDPOINT

	// HomeTimeline - Gets us the home timeline

	public void getHomeTimeline(RequestParams params, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// execute the request
		getClient().get(apiUrl, params, handler);
	}

	public void postNewTweet(RequestParams params, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		// execute the request
		getClient().post(apiUrl, params, handler);
	}

	public void getProfileDetails(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		// execute the request
		getClient().get(apiUrl, handler);
	}

	// retweet a tweet, require an "id" of a tweet passed in the params
	public void postRetweet(RequestParams params, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/retweet/:id.json");
		// execute the request
		getClient().post(apiUrl, params, handler);
	}

	// https://api.twitter.com/1.1/followers/ids.json
	// pass "user_id" or "screen_name" as a param
	public void getFollowers(RequestParams params, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("followers/ids.json");
        // execute the request
        getClient().get(apiUrl, params, handler);
	}

    // https://api.twitter.com/1.1/search/tweets.json
    // "q" as a search query for the request
    public void searchTweets(RequestParams params, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("search/tweets.json");
        // execute the request
        getClient().get(apiUrl, params, handler);
    }

    // https://api.twitter.com/1.1/statuses/user_timeline.json
    // returns a collection of the most recent Tweets posted by the user indicated by the screen_name or user_id parameters
    public void getUserTimeline(RequestParams params, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        // execute the request
        getClient().get(apiUrl, params, handler);
    }

    // https://api.twitter.com/1.1/direct_messages.json
    // returns the 20 most recent direct messages sent to the authenticating user
    // optional: since_id, max_id, count
    public void getMessages(RequestParams params, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("direct_messages.json");
        // execute the request
        getClient().get(apiUrl, params, handler);
    }

    // https://api.twitter.com/1.1/statuses/mentions_timeline.json
    // optional: since_id, max_id, count
    public void getMentions(RequestParams params, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        // execute the request
        getClient().get(apiUrl, params, handler);
    }



	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}