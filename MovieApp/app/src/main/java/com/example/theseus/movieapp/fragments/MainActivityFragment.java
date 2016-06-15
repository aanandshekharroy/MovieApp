package com.example.theseus.movieapp.fragments;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
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

import com.example.theseus.movieapp.adapter.ImageAdapter;
import com.example.theseus.movieapp.R;
import com.example.theseus.movieapp.activity.DetailActivity;
import com.example.theseus.movieapp.data.MovieContract;
import com.example.theseus.movieapp.sync.MovieSyncAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        Log.d(LOG_TAG,"fas----2");
        super.onActivityCreated(savedInstanceState);
    }

    public void initializeLoader(){
        Log.d(LOG_TAG,"reinitialized");
        getLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);

    }

    View rootView;
    public ImageAdapter mImageAdapter;
    private String SORT_BY="";
    public void onSortOrderChange(){
        updateMovieGrid();
        getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        return super.onOptionsItemSelected(item);
    }
    public String getSortBy(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getContext());
        String sortBy=prefs.getString("sort_by_key","popular");
        return sortBy;
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void updateMovieGrid() {
        Log.d(LOG_TAG,"update called");
        MovieSyncAdapter.syncImmediately(getActivity());

    }
    @BindView(R.id.empty_view) View emptyView;
    static final String EXTRA_MOVIE_TITLE="com.example.theseus.movieapp";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(LOG_TAG,"step: 5");
        mImageAdapter=new ImageAdapter(getActivity(),null,0);
        rootView= inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this,rootView);
        movieGrid=(GridView)rootView.findViewById(R.id.movieGrid);
        movieGrid.setEmptyView(emptyView);
        movieGrid.setAdapter(mImageAdapter);
        movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor=(Cursor) parent.getItemAtPosition(position);
                if(cursor!=null){
                    Uri movieIdUri=MovieContract.MoviesEntry.buildUriFromSortOrderAndMovieId(getSortBy(),cursor.getString(COLUMN_MOVIE_ID));
                    Log.d(LOG_TAG,"onselect uri: "+movieIdUri);
                    ((Callback) getActivity())
                            .onItemSelected(movieIdUri);
                }
//                Intent detailActivity=new Intent(getContext(),DetailActivity.class).setData(movieIdUri);
//                startActivity(detailActivity);
            }
        });
        return rootView;
    }
    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri movieUri);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG,"fas----3");
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
        Log.d(LOG_TAG,"fas----4");
        Log.d(LOG_TAG,"-------------------"+data.getColumnName(COLUMN_MOVIE_ID));
        mImageAdapter.swapCursor(data);
//        rootView.findViewById(R.id.loadingGif).setVisibility(View.GONE);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mImageAdapter.swapCursor(null);

    }
}
