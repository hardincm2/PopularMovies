package com.brassbeluga.popularmovies.listener;

import com.brassbeluga.popularmovies.model.MovieDetails;

/**
 * Listener interface that supports periodic callback updates for movie details.
 */
public interface UpdatedMovieDetailsListener {
    void movieDetailsUpdated(MovieDetails movieDetails);
}
