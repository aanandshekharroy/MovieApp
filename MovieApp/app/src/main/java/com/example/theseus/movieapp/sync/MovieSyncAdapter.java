package com.example.theseus.movieapp.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.theseus.movieapp.BuildConfig;
import com.example.theseus.movieapp.R;
import com.example.theseus.movieapp.activity.MainActivity;
import com.example.theseus.movieapp.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();

    public static final int SYNC_INTERVAL = 10;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }
    private String mSortBy;
    public String getSortBy(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getContext());
        String sortBy=prefs.getString("sort_by_key","popular");
        return sortBy;
    }
    RequestQueue requestQueue=null;
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        String sortBy=getSortBy();
      this.mSortBy=sortBy;
        String baseUrl = null;
        final String APP_KEY="api_key";
        if(sortBy.contains(MovieContract.FavouritesEntry.TABLE_NAME)){
            onPostExecute(MovieContract.FavouritesEntry.CONTENT_URI);
            return;
        }
        baseUrl="http://api.themoviedb.org/3/movie/"+sortBy+"?";

        Uri builtUri=Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter(APP_KEY, BuildConfig.MOVIE_API_KEY).build();
        StringRequest stringRequest = new StringRequest(builtUri.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            onPostExecute(jsonParser(response));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(),"Something went wrong",Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void onPostExecute(Uri uri){
        Cursor cursor=null;
        if(uri!=null){
            if(uri.toString().contains(MovieContract.FavouritesEntry.TABLE_NAME)){
//                    cursor=mContext.getContentResolver().query(MovieContract.FavouritesEntry.CONTENT_URI,movieProjections,null,null,null,null);
//                    mImageAdapter.swapCursor(cursor);
            }else{
                try {
                    cursor = getContext().getContentResolver().query(uri,new String[]{MovieContract.MoviesEntry.COLUMN_MOVIE_ID},null,null,null,null);
                    if(cursor.moveToFirst()){
                        do{
                            fetchTrailersAndReviews(cursor.getString(0));
                        }while (cursor.moveToNext());

                    }
                }finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        }
    }
    private Uri jsonParser(String movieData) throws JSONException {
        if(movieData==null){
            return null;
        }
        JSONObject movies=new JSONObject(movieData);
        JSONArray movieArray=movies.getJSONArray("results");
        Vector<ContentValues> cvVector=new Vector<>(movieArray.length());
        for(int i=0;i<movieArray.length();i++){
            JSONObject movie=movieArray.getJSONObject(i);
            ContentValues values=new ContentValues();
            String calue="id="+movie.getString("id")+"\n"+
                    "title:"+movie.getString("title")+"\n"+
                    "overview: "+movie.getString("overview")+"\n"
                    +"votes: "+movie.getString("vote_average")+"\n"+"releasing: "+movie.getString("release_date")+"\n"+
                    "poster: "+movie.getString("poster_path")+"\n";

            values.put(MovieContract.MoviesEntry.COLUMN_MOVIE_ID,movie.getString("id"));
            values.put(MovieContract.MoviesEntry.COLUMN_TITLE,movie.getString("title"));
            values.put(MovieContract.MoviesEntry.COLUMN_SYNOPSIS,movie.getString("overview"));
            values.put(MovieContract.MoviesEntry.COLUMN_VOTES_AVG,movie.getString("vote_average"));
            values.put(MovieContract.MoviesEntry.COLUMN_RELEASE_DATE, movie.getString("release_date"));
            values.put(MovieContract.MoviesEntry.COLUMN_POSTER,movie.getString("poster_path"));
            values.put(MovieContract.MoviesEntry.COLUMN_SORT_BY,mSortBy);
            cvVector.add(values);
        }
        if(cvVector.size()>0){
            ContentValues[] contentValuesArray=new ContentValues[cvVector.size()];
            cvVector.toArray(contentValuesArray);
            int count=getContext().getContentResolver().bulkInsert(MovieContract.MoviesEntry.CONTENT_URI_GENRE,contentValuesArray);
            Log.d(LOG_TAG,"count of rows inserted= "+count);
        }
        return MovieContract.MoviesEntry.buildUriFromSortOrder(mSortBy);
    }
    private void fetchTrailersAndReviews(String movieId){
        String baseUrlTrailer = null;
        String baseUrlReviews = null;
        final String APP_KEY="api_key";
//            movieId=params[0];
        baseUrlTrailer="http://api.themoviedb.org/3/movie/";
        baseUrlReviews="http://api.themoviedb.org/3/movie/";
        Uri builtUriTrailer=Uri.parse(baseUrlTrailer).buildUpon()
                .appendPath(movieId)
                .appendPath("videos")
                .appendQueryParameter(APP_KEY,BuildConfig.MOVIE_API_KEY).build();
        Uri builtUriReviews=Uri.parse(baseUrlTrailer).buildUpon()
                .appendPath(movieId)
                .appendPath("reviews")
                .appendQueryParameter(APP_KEY, BuildConfig.MOVIE_API_KEY).build();

        getJson(builtUriTrailer,movieId,"trailers");
        getJson(builtUriReviews,movieId,"reviews");
        return ;
    }

    private  void reviews(String reviewsData, String movieId) throws JSONException {
        JSONObject jsonObject=new JSONObject(reviewsData);
        JSONArray reviewsArray=jsonObject.getJSONArray("results");
        Vector<ContentValues> cvVarray=new Vector<>(reviewsArray.length());
        for(int i=0;i<reviewsArray.length();i++){
            JSONObject Review=reviewsArray.getJSONObject(i);
            ContentValues values=new ContentValues();
            values.put(MovieContract.ReviewsEntry.COLUMN_MOVIE_ID,movieId);
            values.put(MovieContract.ReviewsEntry.COLUMN_AUTHOR,Review.getString("author"));
            values.put(MovieContract.ReviewsEntry.COLUMN_CONTENT,Review.getString("content"));
            cvVarray.add(values);
        }
        if(cvVarray.size()>0){
            ContentValues[] contentValues=new ContentValues[cvVarray.size()];
            cvVarray.toArray(contentValues);
            getContext().getContentResolver().bulkInsert(MovieContract.ReviewsEntry.CONTENT_URI,contentValues);
        }
    }

    private  void trailers(String trailer, String movieId) throws JSONException {

        JSONObject trailerObject=new JSONObject(trailer);
        JSONArray trailersArray=trailerObject.getJSONArray("results");
        String baseUrl="https://www.youtube.com/watch?v=";
        Vector<ContentValues> cvVector=new Vector<>(trailersArray.length());
        for (int i=0;i<trailersArray.length();i++){
            JSONObject Trailer=trailersArray.getJSONObject(i);
            ContentValues values=new ContentValues();
            values.put(MovieContract.TrailersEntry.COLUMN_MOVIE_ID, movieId);
            values.put(MovieContract.TrailersEntry.COLUMN_TRAILER_URL, baseUrl + Trailer.getString("key"));
            cvVector.add(values);
        }
        if(cvVector.size()>0){
            ContentValues[] contentValues=new ContentValues[cvVector.size()];
            cvVector.toArray(contentValues);
            getContext().getContentResolver().bulkInsert(MovieContract.TrailersEntry.CONTENT_URI,contentValues);
        }
    }
    private void getJson(Uri builtUri, final String movieId, final String reviews) {
        String data=null;
        StringRequest stringRequest = new StringRequest(builtUri.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(reviews.equals("reviews")){
                            try {
                                reviews(response,movieId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else if(reviews.equals("trailers")){
                            try {
                                trailers(response,movieId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getContext(),"Something went wrong",Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(stringRequest);
    }
    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

        }
        return newAccount;
    }
}