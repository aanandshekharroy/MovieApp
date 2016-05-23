package com.example.theseus.movieapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.BaseColumns;
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
import java.util.List;

/**
 * Created by theseus on 17/4/16.
 */
public class DetailActivityAdapter extends CursorAdapter {
    private static final String LOG_TAG=DetailActivityAdapter.class.getSimpleName();
    String movieId;
    Cursor moviesCursor=null;
    Cursor reviewsCursor=null;
    Cursor trailersCursor=null;
    Context mContext;
    static final String[] movieProjections={
            MovieContract.MoviesEntry.TABLE_NAME+"."+MovieContract.MoviesEntry.COLUMN_MOVIE_ID+" AS "+ BaseColumns._ID,
            MovieContract.MoviesEntry.COLUMN_TITLE,
            MovieContract.MoviesEntry.COLUMN_SYNOPSIS,
            MovieContract.MoviesEntry.COLUMN_VOTES_AVG,
            MovieContract.MoviesEntry.COLUMN_RELEASE_DATE,
            MovieContract.MoviesEntry.COLUMN_POSTER};

    static final int COLUMN_MOVIE_ID=0;
    static final int COLUMN_TITLE=1;
    static final int COLUMN_SYNOPSIS=2;
    static final int COLUMN_VOTES_AVG=3;
    static final int COLUMN_RELEASE_DATE=4;
    static final int COLUMN_POSTER=5;
    static final int MOVIE=6;
    static final int REVIEWS=3;
    static final int TRAILERS=2;
    static final int TYPE_OF_VIEWS=3;
    static final String[] reviewsProjection={
            MovieContract.ReviewsEntry.TABLE_NAME+"."+MovieContract.ReviewsEntry.COLUMN_MOVIE_ID+" AS "+ BaseColumns._ID,
            MovieContract.ReviewsEntry.COLUMN_AUTHOR,
            MovieContract.ReviewsEntry.COLUMN_CONTENT};

    static final int COLUMN_AUTHOR=1;
    static final int COLUMN_CONTENT=2;
    static final String[] trailersProjection={
            MovieContract.TrailersEntry.TABLE_NAME+"."+MovieContract.TrailersEntry.COLUMN_MOVIE_ID+" AS "+BaseColumns._ID,
            MovieContract.TrailersEntry.COLUMN_TRAILER_URL
    };
    static final int COLUMN_URL=1;
    View baseView=null;
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public DetailActivityAdapter(View baseView, Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.moviesCursor=c;
        this.baseView=baseView;
        c.moveToFirst();
        mContext=context;
        this.movieId=c.getString(0);
        //Log.d(LOG_TAG,"cursor size: "+c.getCount()+",col: "+c.getColumnCount()+",movieId: "+movieId);
        initializeCursors();
        swapCursor(moviesCursor);

    }
    public int getItemViewType(Cursor cursor) {
        return cursor.getColumnCount();
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_OF_VIEWS;
    }



    private void initializeCursors() {
        //moviesCursor=mContext.getContentResolver().query(MovieContract.MoviesEntry.bu)
        reviewsCursor=mContext.getContentResolver().query(MovieContract.ReviewsEntry.buildUriFromID(movieId),reviewsProjection,null,null,null);
        trailersCursor=mContext.getContentResolver().query(MovieContract.TrailersEntry.buildUriFromMovieId(movieId),trailersProjection,null,null,null);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int typeOfView=getItemViewType(cursor);
        int layout=-1;
        switch (typeOfView){
            case MOVIE:
                layout=R.layout.movieview;
                break;
            case REVIEWS:
                layout=R.layout.reviews;
                break;
            case TRAILERS:
                layout=R.layout.trailer;
                break;
        }
        View view= LayoutInflater.from(context).inflate(layout,parent,false);
        return view;
    }

    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int typeOfView=cursor.getColumnCount();
        switch (typeOfView){
            case MOVIE:
                setMovieDetails(view, cursor);
                swapCursor(reviewsCursor);
                break;
            case TRAILERS:
                //setTrailers(view,cursor);
                //swapCursor(moviesCursor);
                break;
            case REVIEWS:
                Log.d(LOG_TAG," no of reviews: "+reviewsCursor.getCount()+", columns: "+reviewsCursor.getColumnCount());
                setReviews2(cursor);
                swapCursor(trailersCursor);
                break;

        }
    }
    private void setReviews2( Cursor cursor) {
        //Cursor cursor=getContext().getContentResolver().query(MovieContract.ReviewsEntry.buildUriFromID(movieId), null, null, null, null);
        try {
            if(cursor.moveToFirst()){

                List<Reviews> reviewsList=new ArrayList<>();
                do{
                    reviewsList.add(new Reviews(cursor.getString(1), cursor.getString(2)));
                    //Log.d(LOG_TAG,"reviews: movieId"+cursor.getString(0)+", author: "+cursor.getString(1)+", content: "+cursor.getString(2));
                }while (cursor.moveToNext());
                //Log.d(LOG_TAG," No of reviews: "+reviewsList.size());

                ReviewsAdapter reviewsAdapter=new ReviewsAdapter(mContext, reviewsList);
                //View rootView=in
                ListView reviewsListView=(ListView) baseView.findViewById(R.id.reviewsList);
                reviewsListView.setAdapter(reviewsAdapter);
               // setListViewHeightBasedOnChildren(reviewsListView);
            }
        }finally {
            cursor.close();
        }
    }

    private void setReviews(View rootView, Cursor cursor) {
       // Cursor cursor=getContext().getContentResolver().query(MovieContract.ReviewsEntry.buildUriFromID(movieId), null, null, null, null);
        try {
            if(cursor.moveToFirst()){

                //List<Reviews> reviewsList=new ArrayList<>();
                do{
                    TextView author=(TextView)rootView.findViewById(R.id.author);
                    author.setText(cursor.getString(1));
                    TextView content=(TextView)rootView.findViewById(R.id.content);
                    content.setText(cursor.getString(2));
                    Log.d(LOG_TAG,"author: "+cursor.getString(1)+", content: "+cursor.getString(2));
                    //reviewsList.add(new Reviews(cursor.getString(1), cursor.getString(2)));
                    //Log.d(LOG_TAG,"reviews: movieId"+cursor.getString(0)+", author: "+cursor.getString(1)+", content: "+cursor.getString(2));
                }while (cursor.moveToNext());
                //Log.d(LOG_TAG," No of reviews: "+reviewsList.size());
                //ListView reviewsListView=(ListView)rootView.findViewById(R.id.reviews_list);
                //ReviewsAdapter reviewsAdapter=new ReviewsAdapter(getActivity(), reviewsList);
                //reviewsListView.setAdapter(reviewsAdapter);
                //setListViewHeightBasedOnChildren(reviewsListView);
            }
        }finally {
            cursor.close();
        }
    }
    private void setMovieDetails(View rootView,Cursor cursor) {
        //Cursor cursor=null;

        try{
            if(cursor.moveToFirst()){
                TextView title=(TextView)rootView.findViewById(R.id.title);
                title.setText(cursor.getString(COLUMN_TITLE));
                //set poster of movie
                String url="http://image.tmdb.org/t/p/w185/"+cursor.getString(COLUMN_POSTER);
                ImageView poster=(ImageView)rootView.findViewById(R.id.thumbnail);
                poster.setLayoutParams(new LinearLayout.LayoutParams(400, 280));
                poster.setScaleType(ImageView.ScaleType.CENTER_CROP);
                poster.setPadding(30, 30, 20, 20);
                Picasso.with(mContext).load(url).into(poster);
                //Set synopsis of movie
                TextView overview=(TextView)rootView.findViewById(R.id.overview);
                overview.setText(cursor.getString(COLUMN_SYNOPSIS));
                //Set release date
                TextView releseDate=(TextView)rootView.findViewById(R.id.releaseDate);
                releseDate.setText(cursor.getString(COLUMN_RELEASE_DATE));
                //set raings of movie
                TextView userRating=(TextView)rootView.findViewById(R.id.ratings);
                userRating.setText(cursor.getString(COLUMN_VOTES_AVG)+"/10");
            }
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }
    /*private void setTrailers(View rootView, Cursor cursor) {
        try{
            if(cursor.moveToFirst()){
                List<String> trailerNumbering=new ArrayList<>();
                int i=0;
                do{
                    trailerNumbering.add("Trailer "+(i+1));
                    i++;
                }while (cursor.moveToNext());
                ListView trailersList=(ListView)rootView.findViewById(R.id.trailers_list);
                ArrayAdapter<String> trailerAdapter=new ArrayAdapter<String>(getActivity(),R.layout.trailer,R.id.trailerLabel, trailerNumbering);
                trailersList.setAdapter(trailerAdapter);
                setListViewHeightBasedOnChildren(trailersList);
                trailersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Cursor cursor = getContext().getContentResolver().query(MovieContract.TrailersEntry.buildUriFromMovieId(movieId), null, null, null, null);
                        if (cursor.moveToPosition(position)) {
                            Uri trailerUri = Uri.parse(cursor.getString(1));
                            Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, trailerUri);
                            startActivity(youtubeIntent);
                        }
                    }
                });
            }
        }finally {
            cursor.close();
        }


    }*/
}
