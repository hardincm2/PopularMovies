package com.brassbeluga.popularmovies.data.model;

import com.brassbeluga.popularmovies.model.MovieDetailsResponse;
import com.brassbeluga.popularmovies.model.MovieInfo;

import lombok.Data;
import lombok.experimental.Builder;

/**
 * Data-transfer object provides and interface between data in the database and the models consumed by the application
 */
@Data
@Builder
public class MovieInfoDto {
    private long movieId;
    private String overview;
    private String posterImagePath;
    private double rating;
    private String title;
    private String releaseDate;
    private long runtime;

    public MovieInfo toMovieInfo() {
        MovieInfo movieInfo = new MovieInfo();
        movieInfo.overview = overview;
        movieInfo.poster_path = posterImagePath;
        movieInfo.title = title;
        movieInfo.release_date = releaseDate;
        movieInfo.vote_average = rating;
        movieInfo.id = movieId;
        return movieInfo;
    }

    public MovieDetailsResponse toMovieDetailsResponse() {
        MovieDetailsResponse movieDetailsResponse = new MovieDetailsResponse();
        movieDetailsResponse.runtime = runtime;
        return movieDetailsResponse;
    }
}
