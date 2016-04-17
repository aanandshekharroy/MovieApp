package com.example.theseus.movieapp;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.theseus.movieapp.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    final String LOG_TAG=DetailActivityFragment.class.getSimpleName();
    public DetailActivityFragment() {
    }
    static final String[] movieProjections={
            MovieContract.MoviesEntry.TABLE_NAME+"."+MovieContract.MoviesEntry.COLUMN_MOVIE_ID,
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
    static final String[] reviewsProjection={MovieContract.ReviewsEntry.COLUMN_MOVIE_ID,
            MovieContract.ReviewsEntry.COLUMN_AUTHOR,
            MovieContract.ReviewsEntry.COLUMN_CONTENT};
    static final int COLUMN_AUTHOR=1;
    static final int COLUMN_CONTENT=2;
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent=getActivity().getIntent();
        if(intent!=null&&intent.hasExtra("movieId")){
            //String movieDetails=intent.getStringExtra(Intent.EXTRA_TEXT);
            String movieId=intent.getStringExtra("movieId");
            String sortBy=intent.getStringExtra("sortBy");
            //Log.d(LOG_TAG,"MovieId: DetailedActivity "+movieId+",sortBy="+sortBy);
            setMovieDetails(rootView, movieId, sortBy);
            //seeAllFavouriteMovie();
            setMarkAsFavourite(rootView,movieId,sortBy);
            setReviews(rootView, movieId);
            setTrailers(rootView, movieId);

        }
        return rootView;
    }
    public void  seeAllFavouriteMovie(){
        Cursor cursor=getContext().getContentResolver().query(MovieContract.FavouritesEntry.CONTENT_URI, null, null, null, null);
        if(cursor.moveToFirst()){
            Log.d(LOG_TAG,"No of favourites: "+cursor.getCount());
            do{
                Log.d(LOG_TAG,cursor.getString(0)+": "+cursor.getString(2));
            }while (cursor.moveToNext());
        }
        if (cursor!=null){
            cursor.close();
        }
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setMarkAsFavourite(View rootView, final String movieId, String sortBy) {
        final Uri uriWithMovieId= MovieContract.FavouritesEntry.buildUriFromMovieId(movieId);
        boolean isFavourite=false;
        Cursor cursor=null;
        cursor = getContext().getContentResolver().query(uriWithMovieId,new String[]{MovieContract.FavouritesEntry.TABLE_NAME
                +"."+ MovieContract.FavouritesEntry.COLUMN_MOVIE_ID},null,null,null,null);

        if(cursor.moveToFirst()){
            isFavourite=true;
        }
        if(cursor!=null){
            cursor.close();
        }
        final Button favourite=(Button)rootView.findViewById(R.id.favourite);
        if(isFavourite){
            favourite.setText(R.string.removeFromFavourites);
        }else {
            favourite.setText(R.string.markAsFavourite);
        }
        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cursor cursor=null;
                cursor = getContext().getContentResolver().query(uriWithMovieId,new String[]{MovieContract.FavouritesEntry.TABLE_NAME
                        +"."+ MovieContract.FavouritesEntry.COLUMN_MOVIE_ID},null,null,null,null);
                if(!cursor.moveToFirst()){
                    favourite.setText(getString(R.string.removeFromFavourites));
                    getContext().getContentResolver().insert(uriWithMovieId,null);

                }else{
                    favourite.setText(getString(R.string.markAsFavourite));
                    getContext().getContentResolver().delete(uriWithMovieId, null, null);
                }
                if(cursor!=null){
                    cursor.close();
                }
                seeAllFavouriteMovie();
            }
        });
    }

    private void setReviews(View rootView, String movieId) {
        Cursor cursor=getContext().getContentResolver().query(MovieContract.ReviewsEntry.buildUriFromID(movieId), null, null, null, null);
        try {
            if(cursor.moveToFirst()){

                List<Reviews> reviewsList=new ArrayList<>();
                do{
                    reviewsList.add(new Reviews(cursor.getString(1), cursor.getString(2)));
                    //Log.d(LOG_TAG,"reviews: movieId"+cursor.getString(0)+", author: "+cursor.getString(1)+", content: "+cursor.getString(2));
                }while (cursor.moveToNext());
                //Log.d(LOG_TAG," No of reviews: "+reviewsList.size());
                ListView reviewsListView=(ListView)rootView.findViewById(R.id.reviews_list);
                ReviewsAdapter reviewsAdapter=new ReviewsAdapter(getActivity(), reviewsList);
                reviewsListView.setAdapter(reviewsAdapter);
                setListViewHeightBasedOnChildren(reviewsListView);
            }
        }finally {
            cursor.close();
        }
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setTrailers(View rootView, final String movieId) {
        //new MainActivityFragment.FetchTrailersAndReviews().execute(movieId);
        Cursor cursor=getContext().getContentResolver().query(MovieContract.TrailersEntry.buildUriFromMovieId(movieId), null, null, null, null, null);
        //Log.d(LOG_TAG,"cursor size in trailers: "+cursor.getCount());
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
                        Cursor cursor=getContext().getContentResolver().query(MovieContract.TrailersEntry.buildUriFromMovieId(movieId),null,null,null,null);
                        if(cursor.moveToPosition(position)){
                            Uri trailerUri=Uri.parse(cursor.getString(1));
                            Intent youtubeIntent=new Intent(Intent.ACTION_VIEW,trailerUri);
                            startActivity(youtubeIntent);
                        }
                    }
                });
            }
        }finally {
            cursor.close();
        }


    }

    private void setMovieDetails(View rootView, String movieId, String sortBy) {
        Cursor cursor=null;

        try{
            cursor=getContext().getContentResolver().query(MovieContract.MoviesEntry.
                    buildUriFromSortOrderAndMovieId(sortBy,movieId),movieProjections,null,null,null);
            if(cursor.moveToFirst()){
                TextView title=(TextView)rootView.findViewById(R.id.title);
                title.setText(cursor.getString(COLUMN_TITLE));
                //set poster of movie
                String url="http://image.tmdb.org/t/p/w185/"+cursor.getString(COLUMN_POSTER);
                ImageView poster=(ImageView)rootView.findViewById(R.id.thumbnail);
                poster.setLayoutParams(new LinearLayout.LayoutParams(400, 280));
                poster.setScaleType(ImageView.ScaleType.CENTER_CROP);
                poster.setPadding(30, 30, 20, 20);
                Picasso.with(getActivity()).load(url).into(poster);
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



    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
