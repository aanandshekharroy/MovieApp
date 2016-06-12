package com.example.theseus.movieapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.theseus.movieapp.R;
import com.example.theseus.movieapp.fragments.DetailActivityFragment;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_detail,new DetailActivityFragment())
                    .commit();
        }
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
