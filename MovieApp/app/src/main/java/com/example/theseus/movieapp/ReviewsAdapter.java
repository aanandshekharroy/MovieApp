package com.example.theseus.movieapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by theseus on 10/4/16.
 */
public class ReviewsAdapter extends ArrayAdapter<Reviews>{

    public ReviewsAdapter(Context context, List<Reviews> lists) {
        super(context,0,lists);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Reviews review=getItem(position);
        View rootView= LayoutInflater.from(getContext()).inflate(R.layout.reviews,parent,false);
        TextView author=(TextView)rootView.findViewById(R.id.author);
        author.setText(review.author);
        TextView content=(TextView)rootView.findViewById(R.id.content);
        content.setText(review.content);
        return rootView;
    }
}