package com.example.theseus.movieapp.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.example.theseus.movieapp.R;
import com.example.theseus.movieapp.fragments.DetailActivityFragment;
import com.example.theseus.movieapp.fragments.MainActivityFragment;

public class MainActivity extends AppCompatActivity {
    public static String LOG_TAG=MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private   boolean mTwoPane=false;
    private static String mSortBy;
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        this.getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(findViewById(R.id.fragment_detail)!=null){
            mTwoPane=true;
            if(savedInstanceState==null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_detail,new DetailActivityFragment(),DETAILFRAGMENT_TAG)
                        .commit();
            }
        }else {
            mTwoPane=false;
        }
//
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //Intent settingsIntent=new Intent(S)
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onResume() {


        String sortBy=getSortBy();
        Log.d(LOG_TAG,"sortByfunc: "+sortBy+", mSortBy= "+mSortBy);
        if(mSortBy==null||!mSortBy.equals(sortBy)){
            MainActivityFragment ff=(MainActivityFragment)getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_main);
            if(ff!=null){
                ff.initializeLoader();
            }
            mSortBy=sortBy;
        }
        super.onResume();
    }
    public String getSortBy(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String sortBy=prefs.getString("sort_by_key","popular");
        return sortBy;
    }
}
