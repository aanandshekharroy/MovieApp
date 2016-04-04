package com.example.theseus.movieapp;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by theseus on 3/4/16.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private Movie[] mThumbIds=null;
    final String LOG_TAG=ImageAdapter.class.getSimpleName();
    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        if(mThumbIds==null){
            return 0;
        }
        return mThumbIds.length;
    }

    public Movie getItem(int position) {
        return mThumbIds[position];
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
        String url="http://image.tmdb.org/t/p/w185/"+mThumbIds[position].poster_path;
        Log.d(LOG_TAG, "Image url: " + url);
        Picasso.with(mContext).load(url).into(imageView);
        //imageView.setImageResource(mThumbIds[position]);
        //imageView.setImageResource(placeholder);
        //return imageView;
        return imageView;
    }

    // references to our images

    public void setImages(Movie[] movies) {
        if(movies==null){
            return;
        }
        mThumbIds=movies;
        for (Movie m:mThumbIds){
            Log.d(LOG_TAG,m.toString());
        }
    }
}