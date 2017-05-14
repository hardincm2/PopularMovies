package com.brassbeluga.popularmovies.listener;

import com.brassbeluga.popularmovies.model.MovieVideosResponse;

/**
 * Listener interface that supports periodic callback updates for movie videos metadata.
 */
public interface UpdatedMovieVideosListener {
    void movieVideosUpdated(MovieVideosResponse movieInfoResponse);
}
