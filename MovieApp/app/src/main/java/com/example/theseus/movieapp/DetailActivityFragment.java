package com.example.theseus.movieapp;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    final String LOG_TAG=DetailActivityFragment.class.getSimpleName();
    public DetailActivityFragment() {
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent=getActivity().getIntent();
        if(intent!=null&&intent.hasExtra("movie")){
            //String movieDetails=intent.getStringExtra(Intent.EXTRA_TEXT);
            Movie movie=(Movie)intent.getSerializableExtra("movie");
            if(getActivity()!=null){
                if(getActivity().getActionBar()!=null){
                    getActivity().getActionBar().setTitle(movie.title);
                }
            }

            TextView title=(TextView)rootView.findViewById(R.id.title);
            title.setText(movie.title);
            String url="http://image.tmdb.org/t/p/w185/"+movie.poster_path;
            ImageView poster=(ImageView)rootView.findViewById(R.id.thumbnail);

            poster.setLayoutParams(new LinearLayout.LayoutParams(400, 280));

            poster.setScaleType(ImageView.ScaleType.CENTER_CROP);
            poster.setPadding(30, 30, 20, 20);
            Picasso.with(getActivity()).load(url).into(poster);
            /*ImageView imageView = new ImageView(getActivity());

            imageView.setLayoutParams(new GridView.LayoutParams(280, 280));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
            Picasso.with(getActivity()).load(url).into(imageView);*/
            TextView overview=(TextView)rootView.findViewById(R.id.overview);
            overview.setText(movie.overview);
            TextView releseDate=(TextView)rootView.findViewById(R.id.releaseDate);
            releseDate.setText(movie.release_date);
            RatingBar userRating=(RatingBar)rootView.findViewById(R.id.ratingBar);
            userRating.setRating((float) Double.parseDouble(movie.vote_average)/2);
            //Log.d(LOG_TAG,details.length+" Details: "+Intent.EXTRA_TEXT+"\n Ratings: "+(float) Double.parseDouble(movie_vote_average)/2);*/
            Log.d(LOG_TAG,movie.title+"\n"+"User ratings: "+(float) Double.parseDouble(movie.vote_average)/2);
        }
        return rootView;
    }
}
