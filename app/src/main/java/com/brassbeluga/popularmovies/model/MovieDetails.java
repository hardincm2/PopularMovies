package com.brassbeluga.popularmovies.model;

/**
 * Movie details response POJO. Response from TheMovieDB can be deserialized into this object.
 */
public class MovieDetails {
    public long runtime;

    /*
     *  Note there are many more fields in this response but we will only expose the
     *  fields we care about.
     */
}
