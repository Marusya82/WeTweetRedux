package com.codepath.apps.restclienttemplate.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;

import java.util.ArrayList;


public class UserTimelineDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DATABASE DEBUG";
    private static UserTimelineDatabaseHelper sInstance;

    // Database Info
    private static final String DATABASE_NAME = "userTimelineDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Name
    private static final String TABLE_USER_TWEETS = "tweets";

    // Tasks Table Columns
    private static final String KEY_TWEET_ID = "id";
    private static final String KEY_TWEET_BODY = "tweet_text";
    private static final String KEY_CREATED = "created";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_PROFILE_URL = "profile_url";
    private static final String KEY_IMAGE_URL = "image_url";
    private static final String KEY_USER_SCREEN_NAME = "user_screen_name";


    public static synchronized UserTimelineDatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you don't accidentally leak an Activity's context.
        if (sInstance == null) {
            sInstance = new UserTimelineDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private UserTimelineDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TWEETS_TABLE = "CREATE TABLE " + TABLE_USER_TWEETS +
                "(" +
                KEY_TWEET_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                KEY_TWEET_BODY + " TEXT," +
                KEY_CREATED + " TEXT," +
                KEY_USER_NAME + " TEXT," +
                KEY_PROFILE_URL + " TEXT," +
                KEY_IMAGE_URL + " TEXT," +
                KEY_USER_SCREEN_NAME + " TEXT" +
                ")";

        db.execSQL(CREATE_TWEETS_TABLE);
    }

    /**
     * Called when the database needs to be upgraded.
     * This method will only be called if a database already exists on disk with the same DATABASE_NAME,
     * but the DATABASE_VERSION is different than the version of the database that exists on disk.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_TWEETS);
            onCreate(db);
        }
    }

    // Insert a task into the database
    public void addAll(ArrayList<Tweet> tweets) {

        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();

        try {
            for (Tweet tweet: tweets) {
                ContentValues values = new ContentValues();
                values.put(KEY_TWEET_BODY, tweet.getBody());
                values.put(KEY_CREATED, tweet.getCreatedAt());
                values.put(KEY_USER_NAME, tweet.getUser().getName());
                values.put(KEY_PROFILE_URL, tweet.getUser().getProfileImageUrl());
                if (tweet.getImageUrl() != null) {
                    values.put(KEY_IMAGE_URL, tweet.getImageUrl());
                }
                values.put(KEY_USER_SCREEN_NAME, tweet.getUser().getScreenName());

                // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
                db.insertOrThrow(TABLE_USER_TWEETS, null, values);

            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add tweets to database");

        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<Tweet> getAll() {

        ArrayList<Tweet> tweets = new ArrayList<>();

        // SELECT * FROM TASKS
        String TWEETS_SELECT_QUERY = String.format("SELECT * FROM %s", TABLE_USER_TWEETS);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TWEETS_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Tweet newTweet = new Tweet();
                    newTweet.setBody(cursor.getString(cursor.getColumnIndex(KEY_TWEET_BODY)));
                    newTweet.setCreatedAt(cursor.getString(cursor.getColumnIndex(KEY_CREATED)));
                    newTweet.setImageUrl(cursor.getString(cursor.getColumnIndex(KEY_IMAGE_URL)));

                    User user = new User();
                    user.setName(cursor.getString(cursor.getColumnIndex(KEY_USER_NAME)));
                    user.setProfileImageUrl(cursor.getString(cursor.getColumnIndex(KEY_PROFILE_URL)));
                    user.setScreenName(cursor.getString(cursor.getColumnIndex(KEY_USER_SCREEN_NAME)));
                    newTweet.setUser(user);

                    tweets.add(newTweet);

                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get tweets from database");

        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
        }
        return tweets;
    }

    // Delete all tasks in the database
    public void deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_USER_TWEETS, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all tweets");
        } finally {
            db.endTransaction();
        }
    }
}
