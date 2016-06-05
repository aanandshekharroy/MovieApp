package com.example.theseus.movieapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by theseus on 10/4/16.
 */
public class MovieContract {
    public static final String LOG_TAG=MovieContract.class.getSimpleName();
    public static final String CONTENT_AUTHORITY="com.example.theseus.movieapp.app";
    public static final Uri BASE_CONTENT_URI=Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_MOVIE="movies";
    public static final String PATH_GENRE="genre";
    public static final String PATH_REVIEWS="reviews";
    public static final String PATH_TRAILERS="trailers";
    public static final String REMOVE="remove";
    public static String BASE_URL;
    public static class MoviesEntry implements BaseColumns {
        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE)
                .build();
        public static final Uri CONTENT_URI_GENRE=CONTENT_URI.buildUpon().appendEncodedPath(PATH_GENRE).build();
        public static final String CONTENT_TYPE=
                ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_MOVIE;
        public static final String CONTENT_TYPE_ITEM=
                ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_MOVIE;

        public static final String TABLE_NAME="movies";
        public static final String COLUMN_MOVIE_ID="movie_id";
        public static final String COLUMN_TITLE="title";
        public static final String COLUMN_SYNOPSIS="synopsis";
        public static final String COLUMN_VOTES_AVG="vote_avg";
        public static final String COLUMN_RELEASE_DATE="release_date";
        public static final String COLUMN_POSTER="poster";
        public static final String COLUMN_SORT_BY="sortBy";
        public static String getMovieIdFromUri(Uri uri){
            Log.d(LOG_TAG,"getMovieFromUri: "+uri);
            return uri.getPathSegments().get(2);
        }
        public static String getSortByFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }

        public static Uri buildUriFromSortOrder(String sortBy){
            return CONTENT_URI.buildUpon().appendPath(sortBy).build();
        }
        public static Uri buildUriFromSortOrderAndMovieId(String sortOrder,String movieId){
            return CONTENT_URI.buildUpon().appendPath(sortOrder).appendPath(movieId).build();
        }
//        public static Uri buildUriFromMovieId(String movieId){
//            return CONTENT_URI.buildUpon().appendPath(movieId).build();
//        }
    }
    public static class ReviewsEntry implements BaseColumns {
        public static final String CONTENT_TYPE=ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_REVIEWS;
        public static final String CONTENT_TYPE_ITEM=ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_REVIEWS;
        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();
        public static final String TABLE_NAME="reviews";
        public static final String COLUMN_MOVIE_ID="movie_id";
        public static final String COLUMN_AUTHOR="author";
        public static final String COLUMN_CONTENT="content";
        public static Uri buildUriFromID(String movie_id){
            return CONTENT_URI.buildUpon().appendPath(movie_id).build();
        }
        public static Uri buildUriForReviews(){
            return CONTENT_URI;
        }
        public static String getMovieIdFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }
    public static class TrailersEntry implements BaseColumns   {
        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILERS).build();
        public static final String CONTENT_TYPE=ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_TRAILERS;
        public static final String CONTENT_TYPE_ITEM=ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_TRAILERS;
        public static final String TABLE_NAME="trailers";
        public static final String COLUMN_MOVIE_ID="movie_id";
        public static final String COLUMN_TRAILER_URL="trailer_url";
        public static Uri buildUriFromMovieId(String movieId){
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }
        public static String getMovieIdFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }
    public static class FavouritesEntry implements BaseColumns {
        public static final String PATH_FAVOURITES="favourites";
        public static final String TABLE_NAME="favourites";
        public static final String COLUMN_MOVIE_ID="movie_id";
        public static final String CONTENT_TYPE=ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_MOVIE+"/"+PATH_FAVOURITES;
        public static final String CONTENT_TYPE_ITEM=ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_MOVIE+"/"+PATH_FAVOURITES;
        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITES).build();
        public static String getMovieIdFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
        public static Uri buildUriFromMovieId(String movieId){
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }
        public static Uri buildDeleteFromFavouritesUri(String movieId){
            return buildUriFromMovieId(movieId).buildUpon().appendPath(REMOVE).build();
        }
    }
}
