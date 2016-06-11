package com.example.theseus.movieapp.service;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.theseus.movieapp.BuildConfig;
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

/**
 * Created by theseus on 11/6/16.
 */
public class MovieService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public MovieService() {
        super("MovieApp");
    }
    String SORT_BY="";
    String LOG_TAG=MovieService.class.getSimpleName();
    public String getSortBy(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String sortBy=prefs.getString("sort_by_key","popular");
        return sortBy;
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        String sortBy=getSortBy();
        Log.d(LOG_TAG,"error 1");
        this.SORT_BY=sortBy;
        String baseUrl = null;
        final String APP_KEY="api_key";
        // params[0]="popular";
        if(sortBy.contains(MovieContract.FavouritesEntry.TABLE_NAME)){
            onPostExecute(MovieContract.FavouritesEntry.CONTENT_URI);
            return;
        }
        baseUrl="http://api.themoviedb.org/3/movie/"+sortBy+"?";

        Uri builtUri=Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter(APP_KEY, BuildConfig.MOVIE_API_KEY).build();
        HttpURLConnection urlConnection = null;
        BufferedReader reader=null;
        String movieData = null;
        try {
            URL url=new URL(builtUri.toString());
            urlConnection=(HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream=urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                onPostExecute(null);
                return ;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                onPostExecute(null);
                return ;
            }
            movieData = buffer.toString();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        finally {
            if(urlConnection!=null){
                urlConnection.disconnect();
            }
            if(reader!=null){
                try {
                    reader.close();
                }catch (final IOException e){
                    Log.e(LOG_TAG,"Error closing stream");
                }
            }
        }
        try {
            onPostExecute(jsonParser(movieData));
        } catch (JSONException e) {
            e.printStackTrace();
        }
       onPostExecute(null);
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
                    cursor = getContentResolver().query(uri,new String[]{MovieContract.MoviesEntry.COLUMN_MOVIE_ID},null,null,null,null);
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
            values.put(MovieContract.MoviesEntry.COLUMN_SORT_BY,SORT_BY);
            cvVector.add(values);
        }
        if(cvVector.size()>0){
            ContentValues[] contentValuesArray=new ContentValues[cvVector.size()];
            cvVector.toArray(contentValuesArray);
            getContentResolver().bulkInsert(MovieContract.MoviesEntry.CONTENT_URI_GENRE,contentValuesArray);
        }
        return MovieContract.MoviesEntry.buildUriFromSortOrder(SORT_BY);
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

            String trailersData=getJson(builtUriTrailer);
            String reviewsData=getJson(builtUriReviews);
            try {
                trailers(trailersData,movieId);
                reviews(reviewsData,movieId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
                getContentResolver().bulkInsert(MovieContract.ReviewsEntry.CONTENT_URI,contentValues);
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
                getContentResolver().bulkInsert(MovieContract.TrailersEntry.CONTENT_URI,contentValues);
            }
        }
        private String getJson(Uri builtUri) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader=null;
            String data=null;
            try {
                URL url=new URL(builtUri.toString());
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream=urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0 ){
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                data = buffer.toString();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            finally {
                if(urlConnection!=null){
                    urlConnection.disconnect();
                }
                if(reader!=null){
                    try {
                        reader.close();
                    }catch (final IOException e){
                        Log.e(LOG_TAG,"Error closing stream");
                    }
                }
            }
            return data;
        }

}
