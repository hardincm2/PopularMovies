package com.brassbeluga.popularmovies.model;

import java.io.Serializable;

public class MovieInfo implements Serializable {
    public String original_title;
    public String poster_path;
    boolean adult;
    public String overview;
    public String release_date;
    public long[] genre_ids;
    public long id;
    public String original_language;
    public String title;
    public String backdrop_path;
    public double popularity;
    public int vote_count;
    public boolean video;
    public double vote_average;
}
