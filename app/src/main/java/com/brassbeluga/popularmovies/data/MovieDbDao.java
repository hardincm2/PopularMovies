package com.brassbeluga.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;

import com.brassbeluga.popularmovies.data.MovieDbContract.FavoriteMovieEntry;
import com.brassbeluga.popularmovies.data.model.MovieInfoDto;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;


/**
 * DAO (Data access object) that eases and abstracts interactions with the movie content provider.
 */
public class MovieDbDao extends ContentObserver {
    private final ContentResolver contentResolver;
    private final Set<Long> movieIds;

    @Inject
    public MovieDbDao(ContentResolver contentResolver) {
        super(null);
        this.contentResolver = contentResolver;
        this.movieIds = new HashSet<>();

        contentResolver.registerContentObserver(FavoriteMovieEntry.CONTENT_URI, true, this);
    }

    /**
     * Reads all the favorite movies from the sqlite database
     *
     * @return a List of {@link MovieInfoDto}
     */
    public List<MovieInfoDto> readFavoriteMovies() {
        List<MovieInfoDto> movieInfoDtos = new LinkedList<>();
        Cursor cursor = contentResolver
                .query(MovieDbContract.FavoriteMovieEntry.CONTENT_URI, null, null, null, FavoriteMovieEntry.COLUMN_TIMESTAMP);

        movieIds.clear();

        final int overviewIndex = cursor.getColumnIndex(FavoriteMovieEntry.COLUMN_MOVIE_OVERVIEW);
        final int posterImageUrlIndex = cursor.getColumnIndex(FavoriteMovieEntry.COLUMN_MOVIE_POSTER_IMAGE_PATH);
        final int ratingIndex = cursor.getColumnIndex(FavoriteMovieEntry.COLUMN_MOVIE_RATING);
        final int releaseDateIndex = cursor.getColumnIndex(FavoriteMovieEntry.COLUMN_MOVIE_RELEASE_DATE);
        final int titleIndex = cursor.getColumnIndex(FavoriteMovieEntry.COLUMN_MOVIE_TITLE);
        final int runtimeIndex = cursor.getColumnIndex(FavoriteMovieEntry.COLUMN_MOVIE_RUNTIME);
        final int movieIdIndex = cursor.getColumnIndex(FavoriteMovieEntry._ID);

        while (cursor.moveToNext()) {
            MovieInfoDto movieInfoDto = MovieInfoDto.builder()
                    .overview(cursor.getString(overviewIndex))
                    .posterImagePath(cursor.getString(posterImageUrlIndex))
                    .rating(cursor.getDouble(ratingIndex))
                    .releaseDate(cursor.getString(releaseDateIndex))
                    .title(cursor.getString(titleIndex))
                    .runtime(cursor.getLong(runtimeIndex))
                    .movieId(cursor.getLong(movieIdIndex))
                    .build();

            // Add this movie id to the set of favorite movie ids
            movieIds.add(movieInfoDto.getMovieId());
            movieInfoDtos.add(movieInfoDto);
        }

        return movieInfoDtos;
    }

    /**
     * Writes a favorite movie to the database
     *
     * @param movieInfoDto Information pertaining to the favorite movie to be written.
     */
    public void writeFavoriteMovie(MovieInfoDto movieInfoDto) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavoriteMovieEntry.COLUMN_MOVIE_OVERVIEW, movieInfoDto.getOverview());
        contentValues.put(FavoriteMovieEntry.COLUMN_MOVIE_POSTER_IMAGE_PATH, movieInfoDto.getPosterImagePath());
        contentValues.put(FavoriteMovieEntry.COLUMN_MOVIE_RATING, movieInfoDto.getRating());
        contentValues.put(FavoriteMovieEntry.COLUMN_MOVIE_RELEASE_DATE, movieInfoDto.getReleaseDate());
        contentValues.put(FavoriteMovieEntry.COLUMN_MOVIE_TITLE, movieInfoDto.getTitle());
        contentValues.put(FavoriteMovieEntry.COLUMN_MOVIE_RUNTIME, movieInfoDto.getRuntime());
        contentValues.put(FavoriteMovieEntry._ID, movieInfoDto.getMovieId());

        contentResolver.insert(FavoriteMovieEntry.CONTENT_URI, contentValues);
    }

    /**
     * Deletes a record from the DB
     *
     * @param movieId The id of the movie being deleted
     * @return The number of rows deleted (always 0 or 1)
     */
    public int deleteFavoriteMovie(long movieId) {
        int rowsAffected = contentResolver.delete(ContentUris.withAppendedId(FavoriteMovieEntry.CONTENT_URI, movieId), null, null);
        return rowsAffected;
    }

    /**
     * Checks if a movie id is currently present in the favorites list
     *
     * @param movieId The id of the movie
     * @return true if it is favorited and false otherwise
     */
    public boolean isFavoriteMovie(long movieId) {
        // If we've never read and cached the favorite movie ids before then we trigger a read.
        if (movieIds.isEmpty()) {
            readFavoriteMovies();
        }
        return movieIds.contains(movieId);
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        // Need to cache the new favorite movie ids
        readFavoriteMovies();
    }
}
