package com.example.theseus.movieapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.theseus.movieapp.data.MovieContract;

import java.util.List;

/**
 * Created by theseus on 10/4/16.
 */
public class ReviewsAdapter extends CursorAdapter{
    public static View reviewsView=null;
    static final String[] reviewsProjection={
            MovieContract.ReviewsEntry.TABLE_NAME+"."+MovieContract.ReviewsEntry._ID,
            MovieContract.ReviewsEntry.TABLE_NAME+"."+MovieContract.ReviewsEntry.COLUMN_MOVIE_ID+" AS reviewsId",
            MovieContract.ReviewsEntry.COLUMN_AUTHOR,
            MovieContract.ReviewsEntry.COLUMN_CONTENT
    };
    static final int COLUMN_AUTHOR=2;
    static final int COLUMN_CONTENT=3;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ReviewsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View review=LayoutInflater.from(context).inflate(R.layout.reviews,parent,false);
        return review;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView author=(TextView)view.findViewById(R.id.author);
        author.setText(cursor.getString(COLUMN_AUTHOR));
        TextView content=(TextView)view.findViewById(R.id.content);
        content.setText(cursor.getString(COLUMN_CONTENT));
        reviewsView=view;
    }
}