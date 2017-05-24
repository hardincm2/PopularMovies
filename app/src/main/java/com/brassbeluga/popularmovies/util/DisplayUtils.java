package com.brassbeluga.popularmovies.util;

import android.content.res.Configuration;

/**
 * Utility class for common display logic
 */
public class DisplayUtils {
    /**
     * This is the minimum width in DP we would like per one movie column
     */
    private static final int DP_PER_MOVIE_COLUMN = 180;

    /**
     * Dynamically generates the number of columns to display for the recycler view depending
     * on the device dimensions.
     */
    public static int getRecyclerViewSpanCount(Configuration configuration) {
        return (int) Math.floor(configuration.screenWidthDp / DP_PER_MOVIE_COLUMN);
    }
}
