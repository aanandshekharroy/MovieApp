package com.example.theseus.movieapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.theseus.movieapp.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by theseus on 3/4/16.
 */
public class ImageAdapter extends CursorAdapter {
    private Context mContext;
    final String LOG_TAG=ImageAdapter.class.getSimpleName();

    public static String sortBy=null;
    public static String[] postersPath;
    public static String[] movieId;
    static final String[] movieProjections={
            MovieContract.MoviesEntry._ID,
            MovieContract.MoviesEntry.COLUMN_MOVIE_ID,
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
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ImageAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext=context;
    }
    public String getSortBy(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(mContext);
        String sortBy=prefs.getString("sort_by_key", "popular");
        return sortBy;
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ImageView imageView;
        imageView = new ImageView(context);
        imageView.setLayoutParams(new GridView.LayoutParams(280, 280));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setPadding(40, 40, 17, 17);
        bindView(imageView,context,cursor);


        return imageView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(context);
        String sortBy=prefs.getString("sort_by_key","popular");
        String url="http://image.tmdb.org/t/p/w185/"+cursor.getString(COLUMN_POSTER);
        Picasso.with(mContext).load(url).into((ImageView) view);
    }



}