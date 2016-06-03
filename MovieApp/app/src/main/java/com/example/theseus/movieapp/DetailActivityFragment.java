package com.example.theseus.movieapp;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.theseus.movieapp.data.MovieContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    final String LOG_TAG=DetailActivityFragment.class.getSimpleName();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID,null,this);
        super.onActivityCreated(savedInstanceState);
    }
    DetailActivityAdapter detailActivityAdapter;
    private static final int LOADER_ID=0;
    public DetailActivityFragment() {
    }
    Uri movieUri;
    static final String[] movieProjections={
            //MovieContract.MoviesEntry.TABLE_NAME+"."+MovieContract.MoviesEntry.COLUMN_MOVIE_ID+" AS "+ BaseColumns._ID,
            MovieContract.MoviesEntry.TABLE_NAME+"."+MovieContract.MoviesEntry._ID,
            MovieContract.MoviesEntry.TABLE_NAME+"."+MovieContract.MoviesEntry.COLUMN_MOVIE_ID,
            MovieContract.MoviesEntry.COLUMN_TITLE,
            MovieContract.MoviesEntry.COLUMN_SYNOPSIS,
            MovieContract.MoviesEntry.COLUMN_VOTES_AVG,
            MovieContract.MoviesEntry.COLUMN_RELEASE_DATE,
            MovieContract.MoviesEntry.COLUMN_POSTER,
            MovieContract.ReviewsEntry.COLUMN_AUTHOR,
            MovieContract.ReviewsEntry.COLUMN_CONTENT,
            MovieContract.TrailersEntry.COLUMN_TRAILER_URL
    };

    static final int COLUMN_MOVIE_ID=1;
    static final int COLUMN_TITLE=2;
    static final int COLUMN_SYNOPSIS=3;
    static final int COLUMN_VOTES_AVG=4;
    static final int COLUMN_RELEASE_DATE=5;
    static final int COLUMN_POSTER=6;
    static final int COLUMN_AUTHOR=7;
    static final int COLUMN_CONTENT=8;
    static final int COLUMN_TRAILER_URL=9;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_detail, container, false);
        detailActivityAdapter=new DetailActivityAdapter(getContext(),null,0);
        Intent intent=getActivity().getIntent();
        if(intent!=null){
            Toast.makeText(getContext(),intent.getDataString(),Toast.LENGTH_SHORT).show();
            movieUri=intent.getData();
            //View view=inflater.inflate(R.layout.movieview,container,false);
            ListView linearLayout=(ListView) rootView.findViewById(R.id.detailedView);
            linearLayout.setAdapter(detailActivityAdapter);
        }
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getActivity(),movieUri,movieProjections,null,null,null);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        detailActivityAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    /*@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
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
                //seeAllFavouriteMovie();
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
    }*/
}
