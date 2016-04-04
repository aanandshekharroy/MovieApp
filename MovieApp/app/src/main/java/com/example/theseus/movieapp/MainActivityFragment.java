package com.example.theseus.movieapp;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    GridView movieGrid=null;
    final String LOG_TAG=MainActivityFragment.class.getSimpleName();
    public MainActivityFragment() {
        //setHasOptionsMenu(true);
    }
    public ImageAdapter mImageAdapter=new ImageAdapter(getActivity());;
    @Override
    public void onStart() {
        super.onStart();

        //mImageAdapter=new ImageAdapter(getActivity());
        updateMovieGrid();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void updateMovieGrid() {
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getContext());
        String sortBy=prefs.getString(getString(R.string.sort_by_key),getString(R.string.popular_value));
        if(sortBy.equals(R.string.popular_value)){
            //getActivity().getActionBar().setTitle("Popular Movies");
           // getActivity().getActionBar().setTitle("Popular Movies");
        }
        else{

            //getActivity().getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
            //getActivity().getActionBar().setTitle("Top Rated");
            //getActivity().getActionBar().setTitle("Top rated");
        }
        new FetchMovieData().execute(sortBy);
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
        movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie=mImageAdapter.getItem(position);

                Intent detailActivity=new Intent(getActivity(),DetailActivity.class);
                detailActivity.putExtra("movie",movie);
                startActivity(detailActivity);
                Log.d(LOG_TAG,"Inside on click ");
            }
        });
        return rootView;
    }

    class FetchMovieData extends AsyncTask<String,Void,Movie[]> {
        public final String LOG_TAG=FetchMovieData.class.getSimpleName();
        @Override
        protected Movie[] doInBackground(String... params) {
            String baseUrl = null;
            //Log.d(LOG_TAG,"in do in background: "+params.toString());
            final String APP_KEY="api_key";
            if(params[0].equals("popular")){
                Log.d(LOG_TAG,"in popular:");
                baseUrl="http://api.themoviedb.org/3/movie/popular?";
            }
            else if(params[0].equals("top_rated")){
                Log.d(LOG_TAG,"In top rated:");
                baseUrl="http://api.themoviedb.org/3/movie/top_rated?";
            }
            Uri builtUri=Uri.parse(baseUrl).buildUpon()
                    .appendQueryParameter(APP_KEY,BuildConfig.MOVIE_API_KEY).build();
            HttpURLConnection urlConnection = null;
            BufferedReader reader=null;
            String movieData = null;
            try {
                URL url=new URL(builtUri.toString());
                Log.d(LOG_TAG, "URL: " + url.toString());
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

        @Override
        protected void onPostExecute(Movie[] movies) {

            super.onPostExecute(movies);
            mImageAdapter.setImages(movies);
            movieGrid.setAdapter(mImageAdapter);
        }
        private Movie[] jsonParser(String movieData) throws JSONException {
            if(movieData==null){
                return null;
            }
            JSONObject movies=new JSONObject(movieData);
            JSONArray movieArray=movies.getJSONArray("results");
            Movie[] moviesArray=new Movie[movieArray.length()];
            for(int i=0;i<movieArray.length();i++){
                JSONObject movie=movieArray.getJSONObject(i);
                moviesArray[i]=new Movie(movie.getString("title"),movie.getString("poster_path"),movie.getString("overview"),movie.getString("vote_average"),movie.getString("release_date"));
                //Log.d(LOG_TAG,"i="+i+"json movie:"+movie.getString("title"));
            }
            return moviesArray;
        }
    }
}
