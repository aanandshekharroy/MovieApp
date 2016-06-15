package com.example.theseus.movieapp.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.theseus.movieapp.R;
import com.example.theseus.movieapp.data.MovieContract;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by theseus on 5/6/16.
 */
public class TrailersAdapter extends CursorAdapter  {
    Context context;
    String trailersUrl;
    public static final String LOG_TAG=TrailersAdapter.class.getSimpleName();
    private int noOfTrailers=0;
    Cursor currentCursor;
    static final String[] trailersProjection={
            MovieContract.TrailersEntry.TABLE_NAME+"."+MovieContract.TrailersEntry._ID,
            MovieContract.TrailersEntry.TABLE_NAME+"."+MovieContract.TrailersEntry.COLUMN_MOVIE_ID+" AS trailersId",
            MovieContract.TrailersEntry.COLUMN_TRAILER_URL
    };

    static final int COLUMN_TRAILER_URL=2;
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public TrailersAdapter(Context context, Cursor c, int flags) {

        super(context, c, flags);
        noOfTrailers=0;
        this.context=context;
    }
    public static final class ViewHolder{
        @BindView(R.id.trailerUrl) TextView trailersUrl;
        @BindView(R.id.trailersLabel) TextView trailersLabel;
        public ViewHolder(View trailer) {
            ButterKnife.bind(this, trailer);

        }
    }
    @Override
    public View newView(final Context context, Cursor cursor, ViewGroup parent) {
        View view= LayoutInflater.from(context).inflate(R.layout.trailer,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        view.setTag(viewHolder);
        noOfTrailers++;
        if(noOfTrailers==1){
            Log.d(LOG_TAG,"notr2: "+noOfTrailers);
            viewHolder=(ViewHolder)view.getTag();
            viewHolder.trailersLabel.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder=(ViewHolder)view.getTag();
        String trailerLabel=Integer.toString(cursor.getPosition()+1);
        Log.d(LOG_TAG,"notr1: "+noOfTrailers);
        viewHolder.trailersUrl.setText(trailerLabel);

    }
}
