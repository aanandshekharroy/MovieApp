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
    GridView movieGrid=null;
    final String LOG_TAG=MainActivityFragment.class.getSimpleName();
    static final String[] movieProjections={
            MovieContract.MoviesEntry.TABLE_NAME+"."+MovieContract.MoviesEntry.COLUMN_MOVIE_ID,
            MovieContract.MoviesEntry.COLUMN_TITLE,
            MovieContract.MoviesEntry.COLUMN_SYNOPSIS,
            MovieContract.MoviesEntry.COLUMN_VOTES_AVG,
            MovieContract.MoviesEntry.COLUMN_RELEASE_DATE,
            MovieContract.MoviesEntry.COLUMN_POSTER};
    static final int COLUMN_MOVIE_ID=0;
    static final int COLUMN_TITLE=1;
    static final int COLUMN_SYNOPSIS=2;
    static final int COLUMN_VOTES_AVG=3;
    static final int COLUMN_RELEASE_DATE=4;
    static final int COLUMN_POSTER=5;
    public MainActivityFragment() {
        //setHasOptionsMenu(true);
    }
    public ImageAdapter mImageAdapter=new ImageAdapter(getActivity());;
    @Override
    public void onStart() {
        super.onStart();
        //seeAllFavouriteMovie();
        updateMovieGrid();
    }
    public void  seeAllFavouriteMovie(){
        //Log.d(LOG_TAG,"favourite movies:");
        Cursor cursor=getContext().getContentResolver().query(MovieContract.FavouritesEntry.CONTENT_URI, null, null, null, null);
        if(cursor.moveToFirst()){
            //Log.d(LOG_TAG,"No of favourites: "+cursor.getCount());
            do{
                Log.d(LOG_TAG,cursor.getString(0)+"\n"+cursor.getString(1)+"\n"+cursor.getString(2));
            }while (cursor.moveToNext());
        }
        if (cursor!=null){
            cursor.close();
        }
    }

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
        //getActivity().getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        View rootView= inflater.inflate(R.layout.fragment_main, container, false);
        mImageAdapter=new ImageAdapter(getActivity());
//        updateMovieGrid();
        movieGrid=(GridView)rootView.findViewById(R.id.movieGrid);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortBy=getSortBy();
        Uri movieByGenre= MovieContract.MoviesEntry.buildUriFromSortOrder(sortBy);
        return new CursorLoader(getActivity(),
                movieByGenre,movieProjections,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    class FetchMovieData extends AsyncTask<String,Void,Uri> {
        public final String LOG_TAG=FetchMovieData.class.getSimpleName();
        public final Context mContext=getContext();
        @Override
        protected Uri doInBackground(String... params) {
            String baseUrl = null;
            //Log.d(LOG_TAG,"in do in background: "+params.toString());
            final String APP_KEY="api_key";
           // params[0]="popular";
            if(params[0].equals("favourites")){
                Log.d(LOG_TAG,"in popular:");
                //baseUrl="http://api.themoviedb.org/3/movie/popular?";
                return MovieContract.FavouritesEntry.CONTENT_URI;
            }
            else {
                baseUrl="http://api.themoviedb.org/3/movie/"+params[0]+"?";
            }
            Uri builtUri=Uri.parse(baseUrl).buildUpon()
                    .appendQueryParameter(APP_KEY,BuildConfig.MOVIE_API_KEY).build();
            HttpURLConnection urlConnection = null;
            BufferedReader reader=null;
            String movieData = null;
            try {
                URL url=new URL(builtUri.toString());
                //Log.d(LOG_TAG, "URL: " + url.toString());
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
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieData = buffer.toString();
                //Log.d(LOG_TAG,movieData);
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
            //Log.d(LOG_TAG,movieData);

            try {
                //Log.d(LOG_TAG,"going to On post Execute");
                return jsonParser(movieData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        String[] MOVIES_COLUMNS={
                MovieContract.MoviesEntry._ID,
                MovieContract.MoviesEntry.COLUMN_MOVIE_ID,
                MovieContract.MoviesEntry.COLUMN_TITLE,
                MovieContract.MoviesEntry.COLUMN_SYNOPSIS,
                MovieContract.MoviesEntry.COLUMN_VOTES_AVG,
                MovieContract.MoviesEntry.COLUMN_RELEASE_DATE,
                MovieContract.MoviesEntry.COLUMN_POSTER
        };
        int COLUMN_INDEX_MOVIE_ID=1;
        int COLUMN_INDEX_MOVIE_TITLE=2;
        int COLUMN_INDEX_MOVIE_SYNOPSIS=3;
        int COLUMN_INDEX_MOVIE_VOTES_AVG=4;
        int COLUMN_INDEX_MOVIE_RELEASE_DATE=5;
        int COLUMN_INDEX_MOVIE_POSTER=6;

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(Uri uri){
            Cursor cursor = null;
            final String sortBy;
            if(uri==null){
                sortBy=getSortBy();
            }else {
                sortBy = MovieContract.MoviesEntry.getSortByFromUri(uri);
            }


            if(sortBy.equals("favourites")){
                //mImageAdapter.
                movieGrid.setAdapter(mImageAdapter);
                return;
            }
            try {
                if(uri==null){
                    movieGrid.setAdapter(mImageAdapter);
                    return;
                }
                cursor = mContext.getContentResolver().query(uri,new String[]{MovieContract.MoviesEntry.COLUMN_MOVIE_ID},null,null,null);
                if(cursor.moveToFirst()){
                    do{
                        Log.d(LOG_TAG,"on post execute: movieId:"+cursor.getString(0));
                        new FetchTrailersAndReviews().execute(cursor.getString(0));
                }while (cursor.moveToNext());
                    //mImageAdapter.setImages(uri,mContext);
                    movieGrid.setAdapter(mImageAdapter);
                }
            }finally {
                movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String movieId=mImageAdapter.getItem(position);
//                        Log.d(LOG_TAG,"on position "+position+", movieId="+movieId);
                        Cursor cursor=mContext.getContentResolver().query(MovieContract.MoviesEntry.buildUriFromSortOrder(getSortBy()),MOVIES_COLUMNS,null,null,null,null);
                        cursor.moveToFirst();
                        cursor.moveToPosition(position);
                        //Toast.makeText(mContext,"Clicked on = "+cursor.getString(0),Toast.LENGTH_SHORT).show();
                        //new FetchTrailersAndReviews().execute(movieId);
                        //
                        //Log.d(LOG_TAG, "sending: " + movieId + ", position = " + position + "getSort by= " + getSortBy());
                        Intent detailActivity=new Intent(getActivity(),DetailActivity.class);
                        detailActivity.putExtra("movieId",cursor.getString(COLUMN_INDEX_MOVIE_ID) );
//                        detailActivity.putExtra("title",cursor.getString(COLUMN_INDEX_MOVIE_TITLE) );
//                        detailActivity.putExtra("synopsis",cursor.getString(COLUMN_INDEX_MOVIE_SYNOPSIS) );
//                        detailActivity.putExtra("votes_avg",cursor.getString(COLUMN_INDEX_MOVIE_VOTES_AVG) );
//                        detailActivity.putExtra("releaseDate",cursor.getString(COLUMN_INDEX_MOVIE_RELEASE_DATE) );
//                        detailActivity.putExtra("poster",cursor.getString(COLUMN_INDEX_MOVIE_POSTER));
                        detailActivity.putExtra("sortBy",getSortBy());
                        startActivity(detailActivity);
                    }
                });
                //U
//                cursor=mContext.getContentResolver().query(MovieContract.ReviewsEntry.buildUriForReviews(),
//                        null,null,null,null);
//                cursor.moveToFirst();
//                do{
//                    Log.d(LOG_TAG," review-of-"+cursor.getString(1));
//                }while (cursor.moveToNext());
                if (cursor != null) {
                    cursor.close();
                }
            }

        }
        private Uri jsonParser(String movieData) throws JSONException {
            if(movieData==null){
                //Log.d(LOG_TAG,"jsonParse: movieData="+movieData);
                return null;
            }
            //Log.d(LOG_TAG,"jsonParse: "+movieData);
            JSONObject movies=new JSONObject(movieData);
            JSONArray movieArray=movies.getJSONArray("results");
            //Log.d(LOG_TAG,"movieArrayLength="+movieArray.length());
            Vector<ContentValues> cvVector=new Vector<>(movieArray.length());
            for(int i=0;i<movieArray.length();i++){
                //Log.d(LOG_TAG,"i="+i);
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
                //Log.d(LOG_TAG, " value: " + calue);
                //Log.d(LOG_TAG,"inserted value: "+values.toString());
                cvVector.add(values);
            }
            //Log.d(LOG_TAG,"vector:size= "+cvVector.size());

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
            //http://api.themoviedb.org/3/movie/273248/reviews?api_key=0105153be6c50857f2ec1ac9a3f65741
            Uri builtUriTrailer=Uri.parse(baseUrlTrailer).buildUpon()
                    .appendPath(movieId)
                    .appendPath("videos")
                    .appendQueryParameter(APP_KEY,BuildConfig.MOVIE_API_KEY).build();
            Uri builtUriReviews=Uri.parse(baseUrlTrailer).buildUpon()
                    .appendPath(movieId)
                    .appendPath("reviews")
                    .appendQueryParameter(APP_KEY, BuildConfig.MOVIE_API_KEY).build();

            String trailersData=getJson(builtUriTrailer);
            //Log.d(LOG_TAG,"do in background:" );
            String reviewsData=getJson(builtUriReviews);
            //Log.d(LOG_TAG,"reviews json: \n"+reviewsData);
            try {
                //Log.d(LOG_TAG,"trailers data: "+trailersData);
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
            //Log.d(LOG_TAG,"No of reviews: "+reviewsArray.length());
            for(int i=0;i<reviewsArray.length();i++){
                JSONObject Review=reviewsArray.getJSONObject(i);
                ContentValues values=new ContentValues();
                values.put(MovieContract.ReviewsEntry.COLUMN_MOVIE_ID,movieId);
                values.put(MovieContract.ReviewsEntry.COLUMN_AUTHOR,Review.getString("author"));
                values.put(MovieContract.ReviewsEntry.COLUMN_CONTENT,Review.getString("content"));
                cvVarray.add(values);
                //Log.d(LOG_TAG,"reviews: \n"+Review.getString("author")+"\n"+Review.getString("content"));
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
            //Log.d(LOG_TAG,"No of trailers: "+trailersArray.length());
            String baseUrl="https://www.youtube.com/watch?v=";
            Vector<ContentValues> cvVector=new Vector<>(trailersArray.length());
            for (int i=0;i<trailersArray.length();i++){
                JSONObject Trailer=trailersArray.getJSONObject(i);
                ContentValues values=new ContentValues();
                values.put(MovieContract.TrailersEntry.COLUMN_MOVIE_ID, movieId);
                values.put(MovieContract.TrailersEntry.COLUMN_TRAILER_URL, baseUrl + Trailer.getString("key"));
                //Log.d(LOG_TAG,"content value trailer: "+values.toString());
                cvVector.add(values);
                //Log.d(LOG_TAG,"you tube trailers of: "+trailers[i]);
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
                //Log.d(LOG_TAG, "URL: " + url.toString());
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
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0 ){
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                data = buffer.toString();
                //Log.d(LOG_TAG,data);
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
