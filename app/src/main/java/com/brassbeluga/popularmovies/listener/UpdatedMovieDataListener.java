package com.brassbeluga.popularmovies.listener;

/**
 * Listener interface that supports periodic callback updates for movie reviews.
 */
public interface UpdatedMovieDataListener {
    void movieDataUpdated(Object movieDataResponse);
}
