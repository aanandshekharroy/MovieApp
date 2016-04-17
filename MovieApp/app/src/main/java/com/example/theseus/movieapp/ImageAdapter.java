package com.example.theseus.movieapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.theseus.movieapp.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by theseus on 3/4/16.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    final String LOG_TAG=ImageAdapter.class.getSimpleName();
    public ImageAdapter(Context c) {
        mContext = c;
    }
    public static String sortBy=null;
    public static String[] postersPath;
    public static String[] movieId;
    public int getCount() {
        Cursor cursor=null;
        int count=0;
        try {
            cursor=mContext.getContentResolver().query(MovieContract.MoviesEntry.buildUriFromSortOrder(getSortBy()),
                    new String[]{MovieContract.MoviesEntry.TABLE_NAME+"."+MovieContract.MoviesEntry.COLUMN_MOVIE_ID},null,null,null);
            cursor.moveToFirst();
            count=cursor.getCount();
        }finally {
            if(cursor!=null){
                cursor.close();
            }

        }
        return count;
    }
    public String getSortBy(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(mContext);
        String sortBy=prefs.getString("sort_by_key", "popular");
        return sortBy;
    }
    public String getItem(int position) {

        Cursor cursor = null;
        String item=null;
        try{
            cursor=mContext.getContentResolver().query(MovieContract.MoviesEntry.buildUriFromSortOrder(getSortBy()),
                    null,null,null,null);
            cursor.moveToFirst();
            cursor.moveToPosition(position);
            item=cursor.getString(0);
            if(cursor.moveToFirst()){
                for(int i=0;i<=position;i++){
                    Log.d(LOG_TAG,"position="+i+", movieId="+cursor.getString(1));
                    cursor.moveToNext();
                }
            }
            Log.d(LOG_TAG,"returned id= "+item);

        }finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return item;
    }

    public long getItemId(int position) {
        return 0;
    }
    public Integer placeholder=R.drawable.sample_0;
    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(280, 280));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(40, 40, 17, 17);
        } else {
            imageView = (ImageView) convertView;
        }
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(mContext);
        String sortBy=prefs.getString("sort_by_key","popular");
        //Log.d(LOG_TAG,"In image Adapte: sort by: "+getSortBy());
        Cursor cursor=null;
        try {
            cursor=mContext.getContentResolver().query(MovieContract.MoviesEntry.buildUriFromSortOrder(sortBy),
                    new String[]{MovieContract.MoviesEntry.COLUMN_POSTER},null,null,null);
            cursor.moveToFirst();
            cursor.moveToPosition(position);
            String url="http://image.tmdb.org/t/p/w185/"+cursor.getString(0);
            //Log.d(LOG_TAG, "Position: " + position + " ,Image url: " + url);
            Picasso.with(mContext).load(url).into(imageView);
        }finally {
            cursor.close();
        }
        return imageView;
    }

}