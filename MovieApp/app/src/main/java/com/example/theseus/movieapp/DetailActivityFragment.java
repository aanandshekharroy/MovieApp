package com.example.theseus.movieapp;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.theseus.movieapp.data.MovieContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    final String LOG_TAG=DetailActivityFragment.class.getSimpleName();
    private static final int LOADER_MOVIE_ID=0;
    private static final int LOADER_REVIEW_ID=1;
    private static final int LOADER_TRAILER_ID=2;
    Uri movieUri;
    String movieId;
    static final String[] movieProjections={
            //MovieContract.MoviesEntry.TABLE_NAME+"."+MovieContract.MoviesEntry.COLUMN_MOVIE_ID+" AS "+ BaseColumns._ID,
            MovieContract.MoviesEntry.TABLE_NAME+"."+MovieContract.MoviesEntry._ID,
            MovieContract.MoviesEntry.TABLE_NAME+"."+MovieContract.MoviesEntry.COLUMN_MOVIE_ID+" As moviesId" ,
            MovieContract.MoviesEntry.COLUMN_TITLE,
            MovieContract.MoviesEntry.COLUMN_SYNOPSIS,
            MovieContract.MoviesEntry.COLUMN_VOTES_AVG,
            MovieContract.MoviesEntry.COLUMN_RELEASE_DATE,
            MovieContract.MoviesEntry.COLUMN_POSTER
    };
    static final int COLUMN_MOVIE_ID=1;
    static final int COLUMN_TITLE=2;
    static final int COLUMN_SYNOPSIS=3;
    static final int COLUMN_VOTES_AVG=4;
    static final int COLUMN_RELEASE_DATE=5;
    static final int COLUMN_POSTER=6;
    static final String[] reviewsProjection={
            MovieContract.ReviewsEntry.TABLE_NAME+"."+MovieContract.ReviewsEntry._ID,
            MovieContract.ReviewsEntry.TABLE_NAME+"."+MovieContract.ReviewsEntry.COLUMN_MOVIE_ID+" AS reviewsId",
            MovieContract.ReviewsEntry.COLUMN_AUTHOR,
            MovieContract.ReviewsEntry.COLUMN_CONTENT
    };

    static final int COLUMN_AUTHOR=2;
    static final int COLUMN_CONTENT=3;
    static final int COLUMN_TRAILER_URL=9;
    private String getSortBy(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getContext());
        String sortBy=prefs.getString("sort_by_key","popular");
        return sortBy;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_MOVIE_ID,null,this);
        getLoaderManager().initLoader(LOADER_REVIEW_ID,null,this);
        super.onActivityCreated(savedInstanceState);
    }
    DetailActivityAdapter detailActivityAdapter;

    public DetailActivityFragment() {
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_detail, container, false);
        detailActivityAdapter=new DetailActivityAdapter(getContext(),null,0);
        Intent intent=getActivity().getIntent();
        if(intent!=null){
            Toast.makeText(getContext(),intent.getDataString(),Toast.LENGTH_SHORT).show();
            movieUri=intent.getData();
            movieId= MovieContract.MoviesEntry.getMovieIdFromUri(movieUri);
            //View view=inflater.inflate(R.layout.movieview,container,false);
            ListView linearLayout=(ListView) rootView.findViewById(R.id.detailedView);
            linearLayout.setAdapter(detailActivityAdapter);
        }
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case LOADER_MOVIE_ID:
                return new CursorLoader(getActivity(),movieUri,movieProjections,null,null,null);
            case LOADER_REVIEW_ID:
                Uri reviewUri= MovieContract.ReviewsEntry.buildUriFromID(movieId);
                return new CursorLoader(getContext(),reviewUri,reviewsProjection,null,null,null);

        }
        return null;

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //detailActivityAdapter.
        detailActivityAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    /*@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setMarkAsFavourite(View rootView, final String movieId, String sortBy) {
        final Uri uriWithMovieId= MovieContract.FavouritesEntry.buildUriFromMovieId(movieId);
        boolean isFavourite=false;
        Cursor cursor=null;
        cursor = getContext().getContentResolver().query(uriWithMovieId,new String[]{MovieContract.FavouritesEntry.TABLE_NAME
                +"."+ MovieContract.FavouritesEntry.COLUMN_MOVIE_ID},null,null,null,null);

        if(cursor.moveToFirst()){
            isFavourite=true;
        }
        if(cursor!=null){
            cursor.close();
        }
        final Button favourite=(Button)rootView.findViewById(R.id.favourite);
        if(isFavourite){
            favourite.setText(R.string.removeFromFavourites);
        }else {
            favourite.setText(R.string.markAsFavourite);
        }
        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cursor cursor=null;
                cursor = getContext().getContentResolver().query(uriWithMovieId,new String[]{MovieContract.FavouritesEntry.TABLE_NAME
                        +"."+ MovieContract.FavouritesEntry.COLUMN_MOVIE_ID},null,null,null,null);
                if(!cursor.moveToFirst()){
                    favourite.setText(getString(R.string.removeFromFavourites));
                    getContext().getContentResolver().insert(uriWithMovieId,null);

                }else{
                    favourite.setText(getString(R.string.markAsFavourite));
                    getContext().getContentResolver().delete(uriWithMovieId, null, null);
                }
                if(cursor!=null){
                    cursor.close();
                }
                //seeAllFavouriteMovie();
            }
        });
    }*/


}
