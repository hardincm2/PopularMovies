package com.brassbeluga.popularmovies.model;

/**
 * Movie metadata response POJO. Response from TheMovieDB can be deserialized into this object.
 */
public class MovieInfoResponse {
    public int page;
    public int total_results;
    public int total_pages;
    public MovieInfo[] results;
}
