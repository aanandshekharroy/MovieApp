package com.example.theseus.movieapp;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

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
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final int MOVIE_LOADER_ID=1;
    GridView movieGrid=null;
    final String LOG_TAG=MainActivityFragment.class.getSimpleName();
    static final String[] movieProjections={
            MovieContract.MoviesEntry.TABLE_NAME+"."+MovieContract.MoviesEntry._ID,
            MovieContract.MoviesEntry.TABLE_NAME+"."+MovieContract.MoviesEntry.COLUMN_MOVIE_ID,
            MovieContract.MoviesEntry.COLUMN_TITLE,
            MovieContract.MoviesEntry.COLUMN_SYNOPSIS,
            MovieContract.MoviesEntry.COLUMN_VOTES_AVG,
            MovieContract.MoviesEntry.COLUMN_RELEASE_DATE,
            MovieContract.MoviesEntry.COLUMN_POSTER};
    static final int COLUMN_MOVIE_ID=1;
    static final int COLUMN_TITLE=2;
    static final int COLUMN_SYNOPSIS=3;
    static final int COLUMN_VOTES_AVG=4;
    static final int COLUMN_RELEASE_DATE=5;
    static final int COLUMN_POSTER=6;
    public MainActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }
    public void initializeLoader(){
        getLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);

    }
    public ImageAdapter mImageAdapter;
    @Override
    public void onStart() {
        updateMovieGrid();
        super.onStart();
    }
    @Override
    public void onResume() {
        super.onStart();
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        return super.onOptionsItemSelected(item);
    }
    private String getSortBy(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getContext());
        String sortBy=prefs.getString("sort_by_key","popular");
        return sortBy;
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void updateMovieGrid() {
        new FetchMovieData().execute(getSortBy());

    }
    static final String EXTRA_MOVIE_TITLE="com.example.theseus.movieapp";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG,"step: 5");
        mImageAdapter=new ImageAdapter(getActivity(),null,0);
        View rootView= inflater.inflate(R.layout.fragment_main, container, false);

        movieGrid=(GridView)rootView.findViewById(R.id.movieGrid);
        movieGrid.setAdapter(mImageAdapter);
        movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor=(Cursor) parent.getItemAtPosition(position);
                Uri movieIdUri=MovieContract.MoviesEntry.buildUriFromSortOrderAndMovieId(getSortBy(),cursor.getString(COLUMN_MOVIE_ID));
                Intent detailActivity=new Intent(getContext(),DetailActivity.class).setData(movieIdUri);
                startActivity(detailActivity);
            }
        });
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortBy=getSortBy();
        if(sortBy.equals(MovieContract.FavouritesEntry.TABLE_NAME)){
            return new CursorLoader(getActivity(), MovieContract.FavouritesEntry.CONTENT_URI,movieProjections,null,null,null);
        }
        Uri movieByGenre= MovieContract.MoviesEntry.buildUriFromSortOrder(sortBy);
        return new CursorLoader(getActivity(),
                movieByGenre,movieProjections,null,null,null);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(LOG_TAG,"step: 7");
        mImageAdapter.swapCursor(data);

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mImageAdapter.swapCursor(null);

    }
    class FetchMovieData extends AsyncTask<String,Void,Uri> {
        public final String LOG_TAG=FetchMovieData.class.getSimpleName();
        public final Context mContext=getContext();
        @Override
        protected Uri doInBackground(String... params) {
            String baseUrl = null;
            final String APP_KEY="api_key";
           // params[0]="popular";
            if(params[0].contains(MovieContract.FavouritesEntry.TABLE_NAME)){
                return MovieContract.FavouritesEntry.CONTENT_URI;
            }
            baseUrl="http://api.themoviedb.org/3/movie/"+params[0]+"?";

            Uri builtUri=Uri.parse(baseUrl).buildUpon()
                    .appendQueryParameter(APP_KEY,BuildConfig.MOVIE_API_KEY).build();
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
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
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
                return jsonParser(movieData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(Uri uri){
            Cursor cursor=null;
            if(uri!=null){
                if(uri.toString().contains(MovieContract.FavouritesEntry.TABLE_NAME)){
                    cursor=mContext.getContentResolver().query(MovieContract.FavouritesEntry.CONTENT_URI,movieProjections,null,null,null,null);
                    mImageAdapter.swapCursor(cursor);
                }else{
                    try {
                        cursor = mContext.getContentResolver().query(uri,new String[]{MovieContract.MoviesEntry.COLUMN_MOVIE_ID},null,null,null,null);
                        if(cursor.moveToFirst()){
                            do{
                                new FetchTrailersAndReviews().execute(cursor.getString(0));
                            }while (cursor.moveToNext());

                        }
                    }finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
            }
            uri= MovieContract.MoviesEntry.buildUriFromSortOrder(getSortBy());
            cursor=mContext.getContentResolver().query(uri,movieProjections,null,null,null,null);
            mImageAdapter.swapCursor(cursor);
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
                values.put(MovieContract.MoviesEntry.COLUMN_SORT_BY,getSortBy());
                cvVector.add(values);
            }
            if(cvVector.size()>0){
                ContentValues[] contentValuesArray=new ContentValues[cvVector.size()];
                cvVector.toArray(contentValuesArray);
                mContext.getContentResolver().bulkInsert(MovieContract.MoviesEntry.CONTENT_URI_GENRE,contentValuesArray);
            }
            return MovieContract.MoviesEntry.buildUriFromSortOrder(getSortBy());
        }
    }
    class FetchTrailersAndReviews extends AsyncTask<String,Void,String[]>{
        private  final String LOG_TAG=FetchTrailersAndReviews.class.getSimpleName();
        public final Context mContext=getContext();
        String movieId;
        @Override
        protected String[] doInBackground(String... params) {
            String baseUrlTrailer = null;
            String baseUrlReviews = null;
            final String APP_KEY="api_key";
            movieId=params[0];
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
                trailers(trailersData);
                reviews(reviewsData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        private  void reviews(String reviewsData) throws JSONException {
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
                mContext.getContentResolver().bulkInsert(MovieContract.ReviewsEntry.CONTENT_URI,contentValues);
            }
        }

        private  void trailers(String trailer) throws JSONException {

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
                mContext.getContentResolver().bulkInsert(MovieContract.TrailersEntry.CONTENT_URI,contentValues);
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
}
