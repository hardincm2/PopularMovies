package com.brassbeluga.popularmovies.model;


import java.io.Serializable;

public class MovieVideosResponse  implements Serializable {
    public long id;
    public MovieVideo[] results;
}
