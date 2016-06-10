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
    public static final class ViewHolder{
        public static TextView author=null;
        private static TextView content=null;

        public ViewHolder(View review) {
            author=(TextView)review.findViewById(R.id.author);
            content=(TextView)review.findViewById(R.id.content);
        }
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View review=LayoutInflater.from(context).inflate(R.layout.reviews,parent,false);
        ViewHolder viewHolder=new ViewHolder(review);
        review.setTag(viewHolder);
        return review;
    }

    @Override
    public void bindView(View review, Context context, Cursor cursor) {
        ViewHolder viewHolder=(ViewHolder)review.getTag();
        viewHolder.author.setText(cursor.getString(COLUMN_AUTHOR));
        viewHolder.content.setText(cursor.getString(COLUMN_CONTENT));
//        TextView author=(TextView)review.findViewById(R.id.author);
//        TextView content=(TextView)review.findViewById(R.id.content);
//        author.setText(cursor.getString(COLUMN_AUTHOR));
//        content.setText(cursor.getString(COLUMN_CONTENT));
    }
}