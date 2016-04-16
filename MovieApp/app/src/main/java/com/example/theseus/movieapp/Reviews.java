package com.example.theseus.movieapp;

import java.io.Serializable;

/**
 * Created by theseus on 3/4/16.
 */
public class Reviews implements Serializable{

    String author;
    String content;
    public Reviews(String author, String content) {
        this.author=author;
        this.content=content;
    }
}
