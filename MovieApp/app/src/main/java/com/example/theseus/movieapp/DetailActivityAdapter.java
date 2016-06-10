package com.example.theseus.movieapp;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    private static final String NOT_FAVOURITE_BUTTON_LABEL="Add to favourites";
    private static final String FAVOURITE_BUTTON_LABEL="Remove from favourites";
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
    public static class ViewHolder {
        public static TextView movieTitle=null;
        public static TextView synopsis;
        public static TextView release_date;
        public static ImageView poster;
        public static TextView votes;
        public static Button favouriteButton;
        public ViewHolder(View view) {
            movieTitle =(TextView)view.findViewById(R.id.title);
            synopsis=(TextView)view.findViewById(R.id.synopsis);
            release_date=(TextView)view.findViewById(R.id.release_date);
            poster=(ImageView)view.findViewById(R.id.poster);
            votes=(TextView)view.findViewById(R.id.votes);
            favouriteButton=(Button)view.findViewById(R.id.favourite);
        }
    }
    View baseView=null;
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public DetailActivityAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.movieview,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String tableName =cursor.getColumnName(COLUMN_MOVIE_ID);
        Log.d(LOG_TAG,"table name= "+tableName);
        if(tableName.contains(MovieContract.MoviesEntry.TABLE_NAME)){
            viewHolder.movieTitle.setText(cursor.getString(COLUMN_TITLE));
            viewHolder.synopsis.setText(cursor.getString(COLUMN_SYNOPSIS));
            viewHolder.release_date.setText(cursor.getString(COLUMN_RELEASE_DATE));
            String url="http://image.tmdb.org/t/p/w185/"+cursor.getString(COLUMN_POSTER);
            Picasso.with(context).load(url).into(viewHolder.poster);
            viewHolder.votes.setText(cursor.getString(COLUMN_VOTES_AVG)+"/10");
            favouriteButton(context,view,cursor.getString(COLUMN_MOVIE_ID),viewHolder.favouriteButton);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void favouriteButton(final Context context, View view, final String movieId, final Button favouriteButton) {

        final Uri favouriteUri= MovieContract.FavouritesEntry.buildUriFromMovieId(movieId);
        Cursor favourite=context.getContentResolver().query(favouriteUri
        ,movieProjections,null,null,null,null);
        favourite.moveToFirst();
        if(favourite.getCount()==0){
            favouriteButton.setText(NOT_FAVOURITE_BUTTON_LABEL);
        }else {
            favouriteButton.setText(FAVOURITE_BUTTON_LABEL);
        }
        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String isFavourite= (String) favouriteButton.getText();
                if(isFavourite.equals(NOT_FAVOURITE_BUTTON_LABEL)){
                    ContentValues cvalue=new ContentValues();
                    cvalue.put(MovieContract.FavouritesEntry.COLUMN_MOVIE_ID,movieId);
                    Uri retUri=context.getContentResolver().insert(favouriteUri,cvalue);
                    if (retUri!=null){
                        favouriteButton.setText(FAVOURITE_BUTTON_LABEL);
                    }
                }else {
                    int id=context.getContentResolver().delete(favouriteUri,null,null);
                    if(id!=-1){
                        favouriteButton.setText(NOT_FAVOURITE_BUTTON_LABEL);
                    }
                }
            }
        });
    }
}