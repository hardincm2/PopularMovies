package com.brassbeluga.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieDbContract {

    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.brassbeluga.popularmovies";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_MOVIES_FAVORITES = "movies/favorites";

    /**
     * Favorite movie entry constants that describe the data schema
     */
    public static final class FavoriteMovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_MOVIES_FAVORITES).build();

        public static final String TABLE_NAME = "favorite_movies";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_POSTER_IMAGE_PATH = "poster_image_path";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";
        public static final String COLUMN_MOVIE_RUNTIME = "runtime";
        public static final String COLUMN_MOVIE_RATING = "rating";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
