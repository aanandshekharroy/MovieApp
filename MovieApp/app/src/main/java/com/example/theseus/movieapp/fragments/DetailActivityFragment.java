package com.example.theseus.movieapp.fragments;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.commonsware.cwac.merge.MergeAdapter;
import com.example.theseus.movieapp.adapter.DetailActivityAdapter;
import com.example.theseus.movieapp.R;
import com.example.theseus.movieapp.adapter.ReviewsAdapter;
import com.example.theseus.movieapp.adapter.TrailersAdapter;
import com.example.theseus.movieapp.data.MovieContract;

import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    final String LOG_TAG=DetailActivityFragment.class.getSimpleName();
    private static final int LOADER_MOVIE_ID=0;
    private static final int LOADER_REVIEW_ID=1;
    private static final int LOADER_TRAILER_ID=2;
    ListView listView;
    Cursor currentCursor=null;
    ReviewsAdapter reviewsAdapter;
    TrailersAdapter trailersAdapter;
    public static final String DETAIL_URI = "URI";
    Uri movieUri;
    String movieId;
    MergeAdapter mergeAdapter=new MergeAdapter();
    String mShare="";
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
    static final String[] trailersProjection={
            MovieContract.TrailersEntry.TABLE_NAME+"."+MovieContract.TrailersEntry._ID,
            MovieContract.TrailersEntry.TABLE_NAME+"."+MovieContract.TrailersEntry.COLUMN_MOVIE_ID+" AS trailersId",
            MovieContract.TrailersEntry.COLUMN_TRAILER_URL
    };

    static final int COLUMN_TRAILER_URL=2;
    private String getSortBy(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getContext());
        String sortBy=prefs.getString("sort_by_key","popular");
        return sortBy;
    }
    boolean mIsNull=false;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG,"init-1");
        getLoaderManager().initLoader(LOADER_MOVIE_ID,null,this);
//        getLoaderManager().initLoader()

//        if(!mIsNull){
            getLoaderManager().initLoader(LOADER_REVIEW_ID,null,this);
            Log.d(LOG_TAG,"init-2");
            getLoaderManager().initLoader(LOADER_TRAILER_ID,null,this);
            Log.d(LOG_TAG,"init-3");
//        }

        super.onActivityCreated(savedInstanceState);
    }
    DetailActivityAdapter detailActivityAdapter;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    View rootView;
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            movieUri = arguments.getParcelable(DetailActivityFragment.DETAIL_URI);
            Log.d(LOG_TAG,"movieUri--detailed--"+movieUri);
            movieId= MovieContract.MoviesEntry.getMovieIdFromUri(movieUri);
        }
        rootView=inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this,rootView);

        detailActivityAdapter=new DetailActivityAdapter(getContext(),null,0);
        reviewsAdapter=new ReviewsAdapter(getContext(),null,0);
        trailersAdapter=new TrailersAdapter(getContext(),null,0);
        listView=(ListView) rootView.findViewById(R.id.detailedView);return rootView;
    }
    private Intent createShareIntent(){
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setType("text/plain");
        Log.d(LOG_TAG, "just-sending "+mShare);
        intent.putExtra(Intent.EXTRA_TEXT,
                mShare+" #MovieApp");
        return intent;
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(null!=movieUri){
            CursorLoader newCursorLoader=null;
            switch (id){
                case LOADER_MOVIE_ID:
                    Log.d(LOG_TAG,"3");
                    newCursorLoader=new CursorLoader(getActivity(),movieUri,movieProjections,null,null,null);
                    break;
                case LOADER_REVIEW_ID:
                    Log.d(LOG_TAG,"4");
                    Uri reviewUri= MovieContract.ReviewsEntry.buildUriFromID(movieId);
                    newCursorLoader=new CursorLoader(getContext(),reviewUri,reviewsProjection,null,null,null);
                    break;
                case LOADER_TRAILER_ID:
                    Log.d(LOG_TAG,"5");
                    Uri trailerUri= MovieContract.TrailersEntry.buildUriFromMovieId(movieId);
                    newCursorLoader=new CursorLoader(getContext(),trailerUri,trailersProjection,null,null,null);
                    break;
            }
            return newCursorLoader;
        }
        return null;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment,menu);
        MenuItem menuItem=menu.findItem(R.id.action_share);
        ShareActionProvider mShareActionProvider=
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if (mShareActionProvider != null ) {
            mShareActionProvider.setShareIntent(createShareIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
//        super.onCreateOptionsMenu(menu, inflater);

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()){
            case LOADER_MOVIE_ID:
                Log.d(LOG_TAG,"6-movie data= "+data.getCount());
//                detailActivityAdapter.c

                if(data.getCount()==0){
                    mIsNull=true;
                    if(rootView!=null){
                        rootView.findViewById(R.id.removedFromFav).setVisibility(View.VISIBLE);
                    }

                }else{
                    data.moveToFirst();
//                    Log.d(LOG_TAG,"sharing: "+data.getString(COLUMN_TITLE));
//                    mShare=mShare+data.getString(COLUMN_TITLE);
                    mShare=mShare+" "+data.getString(COLUMN_TITLE);
                }
                detailActivityAdapter.swapCursor(data);
                break;
            case LOADER_REVIEW_ID:
                Log.d(LOG_TAG,"7");
                if(!mIsNull){
                    reviewsAdapter.swapCursor(data);
                }else{
//                    reviewsAdapter.swapCursor(data.se);
                }

                break;
            case LOADER_TRAILER_ID:
                Log.d(LOG_TAG,"8");
                if(!mIsNull) {
                    if(data.getCount()!=0){
                        data.moveToFirst();
                        if(data.getPosition()==0){

                        Log.d(LOG_TAG,"sharing-tra: "+data.getString(2));
                            mShare=mShare+(" Trailer 1: "+data.getString(2));
//                            mShare=mShare+;

                        }

                    }
                    trailersAdapter.swapCursor(data);
                    Log.d(LOG_TAG,"sharing: "+mShare);

                }else{
                    if(rootView!=null){
//                        rootView.findViewById(R.id.removedFromFav).setVisibility(View.VISIBLE);
                    }
                }
                mergeAdapter.addAdapter(detailActivityAdapter);
                mergeAdapter.addAdapter(reviewsAdapter);
                mergeAdapter.addAdapter(trailersAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Cursor cursor=(Cursor)parent.getItemAtPosition(position);
                        String tableName=cursor.getColumnName(COLUMN_MOVIE_ID);
                        if(tableName.contains(MovieContract.TrailersEntry.TABLE_NAME)){
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(cursor.getString(COLUMN_TRAILER_URL))));

                        }
//                    Toast.makeText(getContext(),Integer.toString(cursor.getColumnCount()),Toast.LENGTH_SHORT).show();
                    }
                });
                listView.setAdapter(mergeAdapter);
                //linearLayout.addView();

                break;


        }
        //detailActivityAdapter.
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()){
            case LOADER_MOVIE_ID:
                Log.d(LOG_TAG,"9");
                detailActivityAdapter.swapCursor(null);
                break;
            case LOADER_REVIEW_ID:
                Log.d(LOG_TAG,"10");
                reviewsAdapter.swapCursor(null);
            case LOADER_TRAILER_ID:
                Log.d(LOG_TAG,"11");
                reviewsAdapter.swapCursor(null);

        }
    }

    public void onSortOrderChange() {
//        Uri uri=movieUri;
//        if(uri!=null){
//            getLoaderManager().restartLoader(LOADER_MOVIE_ID,null,this);
//            getLoaderManager().restartLoader(LOADER_REVIEW_ID,null,this);
//            getLoaderManager().restartLoader(LOADER_TRAILER_ID,null,this);
//        }
    }
}
