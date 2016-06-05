package com.example.theseus.movieapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by theseus on 11/4/16.
 */
public class MovieProvider extends ContentProvider {
    private static final String LOG_TAG=MovieProvider.class.getSimpleName();
    public static final int MOVIE_WITH_GENRE=9;
    public static final int MOVIE_WITH_GENRE_POPULARITY=10;
    public static final int MOVIE_WITH_GENRE_TOP_RATED=11;

    public static final int FAVOURITE_MOVIE_WITH_ID=13;
    public static final int MOVIE_WITH_ID=101;

    public static final int REVIEWS=200;
    public static final int REVIEWS_WITH_ID=201;

    public static final int TRAILERS=300;
    public static final int TRAILERS_WITH_ID=301;
    public static  MovieDBHelper mDBHelper = null;
    public static SQLiteQueryBuilder movieQueryBuilder;
    public static SQLiteQueryBuilder favouriteMovieQueryBuilder;
    public static SQLiteQueryBuilder reviewsQueryBuilder;
    public static SQLiteQueryBuilder trailersQueryBuilder;
    public static SQLiteQueryBuilder movieDetail;
    static final String POPULARITY="popular";
    static final String TOPRATED="top_rated";
    static final String FAVOURITE="favourites";
    static {
        movieDetail=new SQLiteQueryBuilder();
        movieDetail.setTables(
                MovieContract.MoviesEntry.TABLE_NAME+" LEFT JOIN "+ MovieContract.ReviewsEntry.TABLE_NAME+" ON "+
                        MovieContract.MoviesEntry.TABLE_NAME+"."+ MovieContract.MoviesEntry.COLUMN_MOVIE_ID+" = "+
                        MovieContract.ReviewsEntry.TABLE_NAME+"."+ MovieContract.ReviewsEntry.COLUMN_MOVIE_ID+" LEFT JOIN "+
                        MovieContract.TrailersEntry.TABLE_NAME+" ON "+
                        MovieContract.TrailersEntry.TABLE_NAME+"."+ MovieContract.TrailersEntry.COLUMN_MOVIE_ID+" = "+
                        MovieContract.MoviesEntry.TABLE_NAME+"."+MovieContract.MoviesEntry.COLUMN_MOVIE_ID
        );
        movieQueryBuilder=new SQLiteQueryBuilder();
        movieQueryBuilder.setTables(MovieContract.MoviesEntry.TABLE_NAME);
        reviewsQueryBuilder=new SQLiteQueryBuilder();
        reviewsQueryBuilder.setTables(MovieContract.ReviewsEntry.TABLE_NAME+" INNER JOIN "+MovieContract.MoviesEntry.TABLE_NAME+
        " ON "+MovieContract.ReviewsEntry.TABLE_NAME+"."+MovieContract.ReviewsEntry.COLUMN_MOVIE_ID+" = "+
        MovieContract.MoviesEntry.TABLE_NAME+"."+MovieContract.MoviesEntry.COLUMN_MOVIE_ID);
        trailersQueryBuilder=new SQLiteQueryBuilder();
//        trailersQueryBuilder.setTables(MovieContract.TrailersEntry.TABLE_NAME+" INNER JOIN "+MovieContract.MoviesEntry.TABLE_NAME+
//                " ON "+MovieContract.TrailersEntry.TABLE_NAME+"."+MovieContract.TrailersEntry.COLUMN_MOVIE_ID+" = "+
//                MovieContract.MoviesEntry.TABLE_NAME+"."+MovieContract.MoviesEntry.COLUMN_MOVIE_ID);
        trailersQueryBuilder.setTables(MovieContract.TrailersEntry.TABLE_NAME);
        favouriteMovieQueryBuilder=new SQLiteQueryBuilder();
        favouriteMovieQueryBuilder.setTables(MovieContract.FavouritesEntry.TABLE_NAME+" INNER JOIN "+ MovieContract.MoviesEntry.TABLE_NAME+
        " ON "+ MovieContract.FavouritesEntry.TABLE_NAME+"."+ MovieContract.FavouritesEntry.COLUMN_MOVIE_ID+" = "+
                        MovieContract.MoviesEntry.TABLE_NAME+"."+ MovieContract.MoviesEntry.COLUMN_MOVIE_ID
        );
    }
    public static UriMatcher uriMatcher=buildUriMatcher();
    static UriMatcher buildUriMatcher(){
        UriMatcher matcher=new UriMatcher(UriMatcher.NO_MATCH);
        String authority=MovieContract.CONTENT_AUTHORITY;
        //matcher.addURI(authority,MovieContract.FavouritesEntry.CONTENT_URI+"/*",FAVOURITE_MOVIE_WITH_ID);
        //matcher.addURI(authority,MovieContract.PATH_MOVIE+"/"+POPULARITY,MOVIE_WITH_GENRE_POPULARITY);
        //matcher.addURI(authority,MovieContract.PATH_MOVIE+"/"+TOPRATED,MOVIE_WITH_GENRE_TOP_RATED);
        //matcher.addURI(authority,MovieContract.PATH_MOVIE+"/"+FAVOURITE,MOVIE_WITH_GENRE_FAVOURITE);
        matcher.addURI(authority,"movies/*",MOVIE_WITH_GENRE);

        //matcher.addURI(authority,"movies/favourite/*",FAVOURITE_MOVIE_WITH_ID);
        matcher.addURI(authority,"movies/*/#",MOVIE_WITH_ID);
        //just did on 2 jun
        //matcher.addURI(authority,MovieContract.PATH_MOVIE+"/"+TOPRATED+"/*",MOVIE_WITH_ID);


        matcher.addURI(authority,MovieContract.PATH_REVIEWS,REVIEWS);
        matcher.addURI(authority,MovieContract.PATH_REVIEWS+"/*",REVIEWS_WITH_ID);
        matcher.addURI(authority,MovieContract.PATH_TRAILERS,TRAILERS);
        matcher.addURI(authority,MovieContract.PATH_TRAILERS+"/*",TRAILERS_WITH_ID);
        return matcher;
    }



    @Override
    public boolean onCreate() {
        mDBHelper=new MovieDBHelper(getContext());
        return true;
    }
    String movieSelectionGenre= MovieContract.MoviesEntry.TABLE_NAME+"."+ MovieContract.MoviesEntry.COLUMN_SORT_BY+" = ? ";
    String movieSelectionById=MovieContract.MoviesEntry.TABLE_NAME+"."+MovieContract.MoviesEntry.COLUMN_SORT_BY+" = ? " +
            "AND "+ MovieContract.MoviesEntry.TABLE_NAME+"."+MovieContract.MoviesEntry.COLUMN_MOVIE_ID+" = ? ";
    String reviewsSelection=MovieContract.ReviewsEntry.TABLE_NAME+"."+MovieContract.ReviewsEntry.COLUMN_MOVIE_ID+" = ? ";
    String trailersSelection=MovieContract.TrailersEntry.TABLE_NAME+"."+MovieContract.TrailersEntry.COLUMN_MOVIE_ID+" = ? ";
    public void  seeAllFavouriteMovie(){
        Cursor cursor=getContext().getContentResolver().query(MovieContract.FavouritesEntry.CONTENT_URI, null, null, null, null);
        if(cursor.moveToFirst()){
            //Log.d(LOG_TAG,"No of favourites: "+cursor.getCount());
            do{
                //Log.d(LOG_TAG,cursor.getString(0)+"\n"+cursor.getString(1)+"\n"+cursor.getString(2));
            }while (cursor.moveToNext());
        }
        if (cursor!=null){
            cursor.close();
        }
    }
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match=uriMatcher.match(uri);
        Log.d(LOG_TAG,"query uri matcher: "+match+",as uri= "+uri.toString());
        Cursor retCursor=null;
        switch (match){
            case FAVOURITE_MOVIE_WITH_ID:
                retCursor=getFavouriteMovieFromId(uri, projection, sortOrder);
                break;
            case MOVIE_WITH_GENRE:
                retCursor=getMoviesFromGenre(uri, projection, sortOrder);
                break;
            case MOVIE_WITH_ID:
                retCursor=getMoviesFromId(uri, projection, sortOrder);

                break;
            case REVIEWS_WITH_ID:
                retCursor=getReviewsFromId(uri, projection, sortOrder);
                break;
            case TRAILERS_WITH_ID:
                retCursor=getTrailersFromId(uri,projection,sortOrder);
                break;
            case TRAILERS:
                retCursor=trailersQueryBuilder.query(mDBHelper.getReadableDatabase(),projection,null,null,null,null,sortOrder) ;
                break;
            case REVIEWS:
                retCursor=reviewsQueryBuilder.query(mDBHelper.getReadableDatabase(),projection,null,null,null,null,sortOrder);
                break;
        }
        Log.d(LOG_TAG,"\ncursor sixe: "+retCursor.getCount()+",columns: "+retCursor.toString());
        return retCursor;
    }

    private Cursor getFavouriteMovieFromId(Uri uri, String[] projection, String sortOrder) {
        Cursor cursor=null;
        return favouriteMovieQueryBuilder.query(mDBHelper.getReadableDatabase(),projection, MovieContract.FavouritesEntry.TABLE_NAME+"."+
                MovieContract.FavouritesEntry.COLUMN_MOVIE_ID+" = ? ",new String[]{MovieContract.FavouritesEntry.getMovieIdFromUri(uri)},null
        ,null,sortOrder);

    }


    private Cursor getReviewsFromId(Uri uri, String[] projection, String sortOrder) {
        String movieId=MovieContract.ReviewsEntry.getMovieIdFromUri(uri);
        return reviewsQueryBuilder.query(mDBHelper.getReadableDatabase(),projection,reviewsSelection,new String[]{movieId},null,null,sortOrder);
    }

    private Cursor getMoviesFromId(Uri uri, String[] projection, String sortOrder) {
        String movieId=MovieContract.MoviesEntry.getMovieIdFromUri(uri);
        String sortBy= MovieContract.MoviesEntry.getSortByFromUri(uri);
        if(sortBy.equals("favourites")){
            return favouriteMovieQueryBuilder.query(mDBHelper.getReadableDatabase(),projection, MovieContract.FavouritesEntry.TABLE_NAME
            +"."+ MovieContract.FavouritesEntry.COLUMN_MOVIE_ID+" = ? ",new String[]{movieId},null,null
            ,sortOrder);
        }
        return movieQueryBuilder.query(mDBHelper.getReadableDatabase(),projection,movieSelectionById,new String[]{sortBy,movieId},null,null,sortOrder);
    }
    private Cursor getMoviesFromGenre(Uri uri, String[] projection, String sortOrder){
        String sortBy= MovieContract.MoviesEntry.getSortByFromUri(uri);
        if(sortBy.equals("favourites")){
            return favouriteMovieQueryBuilder.query(mDBHelper.getReadableDatabase(),projection,null,null,null,null,sortOrder);
        }
        return movieQueryBuilder.query(mDBHelper.getReadableDatabase(),projection,movieSelectionGenre,new String[]{sortBy},null,null,sortOrder);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        int match=uriMatcher.match(uri);
        switch (match){
            case FAVOURITE_MOVIE_WITH_ID:
                return MovieContract.FavouritesEntry.CONTENT_TYPE_ITEM;
            case MOVIE_WITH_GENRE_POPULARITY:
                return MovieContract.MoviesEntry.CONTENT_TYPE;
            case MOVIE_WITH_GENRE_TOP_RATED:
                return MovieContract.MoviesEntry.CONTENT_TYPE;
            case MOVIE_WITH_GENRE:
                return MovieContract.MoviesEntry.CONTENT_TYPE;
            case MOVIE_WITH_ID:
                return MovieContract.MoviesEntry.CONTENT_TYPE_ITEM;
            case REVIEWS:
                return MovieContract.ReviewsEntry.CONTENT_TYPE;
            case REVIEWS_WITH_ID:
                return MovieContract.ReviewsEntry.CONTENT_TYPE;
            case TRAILERS:
                return MovieContract.TrailersEntry.CONTENT_TYPE;
            case TRAILERS_WITH_ID:
                return MovieContract.TrailersEntry.CONTENT_TYPE;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match=uriMatcher.match(uri);
        Log.d(LOG_TAG,"match = "+match+"insert : as url= "+uri.toString());
        Uri retUri = null;
        final SQLiteDatabase db=mDBHelper.getWritableDatabase();
        switch (match){
            case MOVIE_WITH_ID:
                String movieId= MovieContract.FavouritesEntry.getMovieIdFromUri(uri);
                ContentValues c=new ContentValues();
                c.put(MovieContract.FavouritesEntry.COLUMN_MOVIE_ID, movieId);

                long id=db.insert(MovieContract.FavouritesEntry.TABLE_NAME,null,c);
               // Log.d(LOG_TAG,"inseting in favourites"+c.toString()+" ,id= "+id);
                seeAllFavouriteMovie();
                retUri=uri;
                break;
            case MOVIE_WITH_GENRE:
                db.insert(MovieContract.MoviesEntry.TABLE_NAME,null,values);
                break;
            case REVIEWS:
                db.insert(MovieContract.ReviewsEntry.TABLE_NAME,null,values);
                retUri= MovieContract.ReviewsEntry.buildUriFromID((String) values.get(MovieContract.ReviewsEntry.COLUMN_MOVIE_ID));
                break;
            case TRAILERS:
                db.insert(MovieContract.TrailersEntry.TABLE_NAME,null,values);
                retUri= MovieContract.TrailersEntry.buildUriFromMovieId((String)values.get(MovieContract.TrailersEntry.COLUMN_MOVIE_ID));
                break;



        }
        getContext().getContentResolver().notifyChange(uri,null);
        return retUri;
    }
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db=mDBHelper.getWritableDatabase();
        int match=uriMatcher.match(uri);
        //Log.d(LOG_TAG,"match:"+match);
        int retCount=0;
        switch (match){
            case MOVIE_WITH_GENRE:
                try{
                    Cursor query=null;
                    db.beginTransaction();
                    for (ContentValues c:values){
                        long _id=db.insert(MovieContract.MoviesEntry.TABLE_NAME,null,c);
                        if(_id!=-1){
                            retCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                    if(query!=null){
                        query.close();
                    }

                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return retCount;
            case REVIEWS:
                try{
                    db.beginTransaction();
                    Cursor query=null;
                    for (ContentValues c:values){
                        long _id = db.insert(MovieContract.ReviewsEntry.TABLE_NAME, null, c);
                        if (_id != -1) retCount++;
                    }
                    db.setTransactionSuccessful();

                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return retCount;
            case TRAILERS:
                try{
                    Cursor query=null;
                    db.beginTransaction();
                    for(ContentValues c:values){
                        long _id=db.insert(MovieContract.TrailersEntry.TABLE_NAME,null,c);
                        if (_id != -1) {
                            retCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                    if(query!=null){
                        query.close();
                    }
                }
                finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return retCount;
        }
        return super.bulkInsert(uri, values);
    }

    private Cursor getTrailersFromId(Uri uri, String[] projection, String sortOrder) {
        Log.d(LOG_TAG,"trailer url: "+uri.toString());
        String movieId=MovieContract.TrailersEntry.getMovieIdFromUri(uri);
        Log.d(LOG_TAG,"trailer url movieId: "+movieId);
        return trailersQueryBuilder.query(mDBHelper.getReadableDatabase(), projection, trailersSelection, new String[]{movieId}, null, null, sortOrder);
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if(selection==null){
            selection="1";
        }
        int match=uriMatcher.match(uri);
        switch (match){
            case MOVIE_WITH_ID:
                long id=mDBHelper.getWritableDatabase().delete(MovieContract.FavouritesEntry.TABLE_NAME, MovieContract.FavouritesEntry.COLUMN_MOVIE_ID+" = ?",
                        new String[]{MovieContract.FavouritesEntry.getMovieIdFromUri(uri)});
                //Log.d(LOG_TAG,"deleted: "+id);
                break;
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
