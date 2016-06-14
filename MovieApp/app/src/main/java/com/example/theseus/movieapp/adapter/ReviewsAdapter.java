package com.example.theseus.movieapp.adapter;

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

import com.example.theseus.movieapp.R;
import com.example.theseus.movieapp.data.MovieContract;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    public static final class ReviewHolder{
        @BindView(R.id.author)TextView author;
        @BindView(R.id.content)TextView content;
        @BindView(R.id.reviewsLabel) TextView reviewsLabel;


        public ReviewHolder(View review) {
            ButterKnife.bind(this,review);
        }
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View review=LayoutInflater.from(context).inflate(R.layout.reviews,parent,false);
        ReviewHolder viewHolder=new ReviewHolder(review);
        review.setTag(viewHolder);
        return review;
    }

    @Override
    public void bindView(View review, Context context, Cursor cursor) {
        ReviewHolder viewHolder=(ReviewHolder)review.getTag();
        viewHolder.author.setText(cursor.getString(COLUMN_AUTHOR));
        viewHolder.content.setText(cursor.getString(COLUMN_CONTENT));
        if(cursor.getPosition()==0){
            viewHolder.reviewsLabel.setVisibility(View.VISIBLE);
        }
    }
}