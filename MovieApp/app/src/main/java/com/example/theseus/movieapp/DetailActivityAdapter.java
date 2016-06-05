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
    View baseView=null;
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public DetailActivityAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.movieview,parent,false);
        return view;
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
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
            favouriteButton(context,view,cursor.getString(COLUMN_MOVIE_ID));
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void favouriteButton(final Context context, View view, final String movieId) {
        final Button favouriteButton=(Button)view.findViewById(R.id.favourite);
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
//    favouriteButton
//    private void setMarkAsFavourite(View rootView, final String movieId, String sortBy) {
//        final Uri uriWithMovieId= MovieContract.FavouritesEntry.buildUriFromMovieId(movieId);
//        boolean isFavourite=false;
//        Cursor cursor=null;
//        cursor = getContext().getContentResolver().query(uriWithMovieId,new String[]{MovieContract.FavouritesEntry.TABLE_NAME
//                +"."+ MovieContract.FavouritesEntry.COLUMN_MOVIE_ID},null,null,null,null);
//
//        if(cursor.moveToFirst()){
//            isFavourite=true;
//        }
//        if(cursor!=null){
//            cursor.close();
//        }
//        final Button favourite=(Button)rootView.findViewById(R.id.favourite);
//        if(isFavourite){
//            favourite.setText(R.string.removeFromFavourites);
//        }else {
//            favourite.setText(R.string.markAsFavourite);
//        }
//        favourite.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Cursor cursor=null;
//                cursor = getContext().getContentResolver().query(uriWithMovieId,new String[]{MovieContract.FavouritesEntry.TABLE_NAME
//                        +"."+ MovieContract.FavouritesEntry.COLUMN_MOVIE_ID},null,null,null,null);
//                if(!cursor.moveToFirst()){
//                    favourite.setText(getString(R.string.removeFromFavourites));
//                    getContext().getContentResolver().insert(uriWithMovieId,null);
//
//                }else{
//                    favourite.setText(getString(R.string.markAsFavourite));
//                    getContext().getContentResolver().delete(uriWithMovieId, null, null);
//                }
//                if(cursor!=null){
//                    cursor.close();
//                }
//                //seeAllFavouriteMovie();
//            }
//        });
//    }*/
}