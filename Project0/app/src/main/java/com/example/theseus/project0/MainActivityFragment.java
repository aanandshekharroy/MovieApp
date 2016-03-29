package com.example.theseus.project0;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_main, container, false);
        Button spotifyStreamer=(Button) rootView.findViewById(R.id.spotifyStreamer);
        spotifyStreamer.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
               Toast.makeText(getActivity(),"This button will launch my "+"spotify streamer app!",Toast.LENGTH_SHORT).show();
            }
        });
        Button scoresApp=(Button) rootView.findViewById(R.id.scoresApp);
        scoresApp.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Toast.makeText(getActivity(),"This button will launch my "+"scores app!",Toast.LENGTH_SHORT).show();
            }
        });
        Button libraryApp=(Button) rootView.findViewById(R.id.libraryApp);
        libraryApp.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Toast.makeText(getActivity(),"This button will launch my "+"library app!",Toast.LENGTH_SHORT).show();
            }
        });
        Button buildItBigger=(Button) rootView.findViewById(R.id.buildItBigger);
        buildItBigger.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Toast.makeText(getActivity(),"This button will launch my "+"build it bigger app!",Toast.LENGTH_SHORT).show();
            }
        });
        Button xyzReader=(Button) rootView.findViewById(R.id.xyzReader);
        xyzReader.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Toast.makeText(getActivity(),"This button will launch my "+"xyz reader app!",Toast.LENGTH_SHORT).show();
            }
        });
        Button capstone=(Button) rootView.findViewById(R.id.capstoneMyOwnApp);
        capstone.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Toast.makeText(getActivity(),"This button will launch my "+"capstone app!",Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }
}
