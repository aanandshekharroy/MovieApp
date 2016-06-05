package com.example.theseus.movieapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.BaseColumns;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.theseus.movieapp.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by theseus on 17/4/16.
 */
public class DetailActivityAdapter extends CursorAdapter {

    private static final String LOG_TAG=DetailActivityAdapter.class.getSimpleName();
    String movieId;
    Context mContext;
    public static View detailView=null;
    static final String[] movieProjections={
            //MovieContract.MoviesEntry.TABLE_NAME+"."+MovieContract.MoviesEntry.COLUMN_MOVIE_ID+" AS "+ BaseColumns._ID,
            MovieContract.MoviesEntry.TABLE_NAME+"."+MovieContract.MoviesEntry._ID ,
            MovieContract.MoviesEntry.TABLE_NAME+"."+MovieContract.MoviesEntry.COLUMN_MOVIE_ID+" As moviesId",
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
    View baseView=null;
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public DetailActivityAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.movieview,parent,false);
//        if(detailView!=null){
//            return detailView;
//        }

//        String tableName =cursor.getColumnName(COLUMN_MOVIE_ID);
//        Log.d(LOG_TAG,"table name= "+tableName);
//
//        if(tableName.contains(MovieContract.MoviesEntry.TABLE_NAME)){
//
//        }else if(tableName.contains(MovieContract.ReviewsEntry.TABLE_NAME)){
//            view=LayoutInflater.from(context).inflate(R.layout.reviews,parent,false);
//        }

        return view;
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //
        // cursor.
        String tableName =cursor.getColumnName(COLUMN_MOVIE_ID);
        Log.d(LOG_TAG,"table name= "+tableName);
        if(tableName.contains(MovieContract.MoviesEntry.TABLE_NAME)){
            Log.d(LOG_TAG,"\ncursor sixe: "+cursor.getCount()+",columns: "+cursor.getColumnCount());
            TextView movieTitle=(TextView)view.findViewById(R.id.title);
            movieTitle.setText(cursor.getString(COLUMN_TITLE));
            TextView synopsis=(TextView)view.findViewById(R.id.synopsis);
            synopsis.setText(cursor.getString(COLUMN_SYNOPSIS));
            TextView release_date=(TextView)view.findViewById(R.id.release_date);
            release_date.setText(cursor.getString(COLUMN_RELEASE_DATE));
            ImageView poster=(ImageView)view.findViewById(R.id.poster);
            String url="http://image.tmdb.org/t/p/w185/"+cursor.getString(COLUMN_POSTER);
            Picasso.with(context).load(url).into(poster);
            TextView votes=(TextView)view.findViewById(R.id.votes);
            votes.setText(cursor.getString(COLUMN_VOTES_AVG));
        }
        detailView=view;
//        else if (tableName.contains(MovieContract.ReviewsEntry.TABLE_NAME)){
//            TextView author=(TextView)view.findViewById(R.id.author);
//            author.setText(cursor.getString(COLUMN_AUTHOR));
//            TextView content=(TextView)view.findViewById(R.id.content);
//            content.setText(cursor.getString(COLUMN_CONTENT));
//
//
////            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(
////                    LinearLayout.LayoutParams.MATCH_PARENT,
////                    LinearLayout.LayoutParams.WRAP_CONTENT);
////
////            LinearLayout reviewsLayout=(LinearLayout)view.findViewById(R.id.reviewsLayout);
////
////                cursor.moveToFirst();
////                do{
////                    authorTextView.setLayoutParams(params);
////                    String content=cursor.getString(COLUMN_CONTENT);
////                    TextView contentTextView=new TextView(context);
////                    contentTextView.setText(content);
////                    contentTextView.setLayoutParams(params);
////                    reviewsLayout.addView(authorTextView);
////                    reviewsLayout.addView(contentTextView);
////                }while (cursor.moveToNext());
//
//        }
//        cursor.moveToFirst();
//        LinearLayout reviewsLayout= (LinearLayout) view.findViewById(R.id.reviewsLayout);
//        String prevAuthor="";
//        LinearLayout trailersLayout=(LinearLayout)view.findViewById(R.id.trailersLayout);
//        String prevUrl="";
//        HashSet<String> authors=new HashSet<>();
//        HashSet<String> trailersUrl=new HashSet<>();
//        do{
//
//            if(cursor.getString(COLUMN_AUTHOR)!=null&&!authors.contains(cursor.getString(COLUMN_AUTHOR))){
//                authors.add(cursor.getString(COLUMN_AUTHOR));
//                Log.d(LOG_TAG,"movie name: "+cursor.getString(COLUMN_TITLE)+"\nContent: "+cursor.getString(COLUMN_CONTENT)+"\n");
//                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//                TextView author=new TextView(context);
//                author.setLayoutParams(lparams);
//                author.setText(cursor.getString(COLUMN_AUTHOR));
//                TextView content=new TextView(context);
//                content.setLayoutParams(lparams);
//                content.setText(cursor.getString(COLUMN_CONTENT));
//                reviewsLayout.addView(author);
//                reviewsLayout.addView(content);
//
//            }
//            if(cursor.getString(COLUMN_TRAILER_URL)!=null&&!trailersUrl.contains(cursor.getString(COLUMN_TRAILER_URL))){
//
//            }
//        }while (cursor.moveToNext());
//        cursor.moveToFirst();
    }
}

//            Log.d(LOG_TAG,"\nmovie : "+cursor.getString(COLUMN_TITLE)+"\n"
//            +"synopsis: "+cursor.getString(COLUMN_SYNOPSIS)+"\n"+
//            "votes avg: "+cursor.getString(COLUMN_VOTES_AVG)+"\n"+
//            "uthor: "+cursor.getString(COLUMN_AUTHOR)+"\n"+
//            "content: "+cursor.getString(COLUMN_CONTENT)+"\n"+
//            "trailer-url"+cursor.getString(COLUMN_TRAILER_URL));