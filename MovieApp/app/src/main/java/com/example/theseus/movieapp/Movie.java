package com.example.theseus.movieapp;

import java.io.Serializable;

/**
 * Created by theseus on 3/4/16.
 */
public class Movie implements Serializable{
     String poster_path;
    boolean adult;
     String overview;
     String title;
     String vote_average;
    String release_date;
    public Movie(String title, String poster_path, String overview, String vote_average,String release_date) {
        this.title=title;
        this.poster_path=poster_path;
        this.overview=overview;
        this.vote_average=vote_average;
        this.release_date=release_date;
    }

    @Override
    public String toString() {

        return this.title+" "+this.poster_path+" "+this.overview+" "+this.vote_average+" "+this.release_date;
    }
}
