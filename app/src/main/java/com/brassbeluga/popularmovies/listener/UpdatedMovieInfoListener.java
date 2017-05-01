package com.brassbeluga.popularmovies.listener;


import com.brassbeluga.popularmovies.model.MovieInfoResponse;

/**
 * Listener interface that supports periodic callback updates for movie information metadata.
 */
public interface UpdatedMovieInfoListener {
    void movieInfoUpdated(MovieInfoResponse movieInfoResponse);
}
