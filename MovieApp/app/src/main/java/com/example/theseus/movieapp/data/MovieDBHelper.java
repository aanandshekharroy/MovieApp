package com.example.theseus.movieapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by theseus on 10/4/16.
 */
public class MovieDBHelper extends SQLiteOpenHelper {
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+MovieContract.MoviesEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+MovieContract.ReviewsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+MovieContract.TrailersEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ MovieContract.FavouritesEntry.TABLE_NAME);
        onCreate(db);
    }

    public static int DATABASE_VERSION=26;
    public static String DATABASE_NAME="movie.db";
    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIES_TABLE="CREATE TABLE "+MovieContract.MoviesEntry.TABLE_NAME+"( "+
                MovieContract.MoviesEntry._ID+" INTEGER PRIMARY KEY, "+
                MovieContract.MoviesEntry.COLUMN_MOVIE_ID+" STRING NOT NULL, "+
                MovieContract.MoviesEntry.COLUMN_TITLE+" STRING NOT NULL, "+
                MovieContract.MoviesEntry.COLUMN_SYNOPSIS+" STRING NOT NULL, "+
                MovieContract.MoviesEntry.COLUMN_VOTES_AVG+" REAL NOT NULL, "+
                MovieContract.MoviesEntry.COLUMN_RELEASE_DATE+" STRING NOT NULL, "+
                MovieContract.MoviesEntry.COLUMN_POSTER+" STRING NOT NULL," +
                MovieContract.MoviesEntry.COLUMN_SORT_BY+" STRING NOT NULL, "
                +"UNIQUE ("+ MovieContract.MoviesEntry.COLUMN_MOVIE_ID+","+
                MovieContract.MoviesEntry.COLUMN_SORT_BY+") ON CONFLICT IGNORE " +
                ");";
         final String SQL_CREATE_REVIEWS_TABLE="CREATE TABLE "+MovieContract.ReviewsEntry.TABLE_NAME+"( "+
                MovieContract.ReviewsEntry._ID+" INTEGER PRIMARY KEY ,"+
                MovieContract.ReviewsEntry.COLUMN_MOVIE_ID+" STRING NOT NULL, "+
                MovieContract.ReviewsEntry.COLUMN_AUTHOR+" STRING NOT NULL, "+
                MovieContract.ReviewsEntry.COLUMN_CONTENT+" STRING NOT NULL, "+
                 "UNIQUE ("+ MovieContract.ReviewsEntry.COLUMN_MOVIE_ID+","+ MovieContract.ReviewsEntry.COLUMN_AUTHOR
                 +","+ MovieContract.ReviewsEntry.COLUMN_CONTENT+") ON CONFLICT IGNORE, "+
                " FOREIGN KEY ("+MovieContract.ReviewsEntry.COLUMN_MOVIE_ID+") REFERENCES "+
                MovieContract.MoviesEntry.TABLE_NAME+" ("+MovieContract.MoviesEntry.COLUMN_MOVIE_ID+"));";
        final String SQL_CREATE_TRAILERS_TABLE="CREATE TABLE "+MovieContract.TrailersEntry.TABLE_NAME+"( "+
                MovieContract.TrailersEntry._ID+" INTEGER PRIMARY KEY ,"+
                MovieContract.TrailersEntry.COLUMN_MOVIE_ID+" STRING NOT NULL, "+
                MovieContract.TrailersEntry.COLUMN_TRAILER_URL+" STRING NOT NULL, "+
                "UNIQUE ("+ MovieContract.TrailersEntry.COLUMN_TRAILER_URL
                +") ON CONFLICT IGNORE, "+
                " FOREIGN KEY ("+MovieContract.TrailersEntry.COLUMN_MOVIE_ID+") REFERENCES "+
                MovieContract.MoviesEntry.TABLE_NAME+" ("+MovieContract.MoviesEntry.COLUMN_MOVIE_ID+"));";
        final String SQL_CREATE_FAVOURITES_TABLE="CREATE TABLE "+ MovieContract.FavouritesEntry.TABLE_NAME+"( "+
                MovieContract.FavouritesEntry._ID+" INTEGER PRIMARY KEY ,"+
                MovieContract.FavouritesEntry.COLUMN_MOVIE_ID+" STRING NOT NULL, "+" UNIQUE ("+
                MovieContract.FavouritesEntry.COLUMN_MOVIE_ID+") ON CONFLICT IGNORE "+" FOREIGN KEY ("+
                MovieContract.FavouritesEntry.COLUMN_MOVIE_ID+") REFERENCES "+
                MovieContract.MoviesEntry.TABLE_NAME+" ( "+ MovieContract.MoviesEntry.COLUMN_MOVIE_ID+"));";
        db.execSQL(SQL_CREATE_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_REVIEWS_TABLE);
        db.execSQL(SQL_CREATE_TRAILERS_TABLE);
        db.execSQL(SQL_CREATE_FAVOURITES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+MovieContract.MoviesEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+MovieContract.ReviewsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+MovieContract.TrailersEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ MovieContract.FavouritesEntry.TABLE_NAME);
        onCreate(db);
    }
}
