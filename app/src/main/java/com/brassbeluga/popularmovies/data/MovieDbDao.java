package com.brassbeluga.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.brassbeluga.popularmovies.data.model.MovieInfoDto;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.brassbeluga.popularmovies.data.MovieDbContract.FavoriteMovieEntry;

import javax.inject.Inject;


/**
 * DAO (Data access object) that eases and abstracts interactions with the movie content provider.
 */
public class MovieDbDao {
    private final ContentResolver contentResolver;
    private final Set<Long> movieIds;

    @Inject
    public MovieDbDao(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
        this.movieIds = new HashSet<>();
    }

    public List<MovieInfoDto> readFavoriteMovies() {
        List<MovieInfoDto> movieInfoDtos = new LinkedList<>();
        Cursor cursor = contentResolver
                .query(MovieDbContract.FavoriteMovieEntry.CONTENT_URI, null, null, null, null);

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

            movieIds.add(movieInfoDto.getMovieId());
            movieInfoDtos.add(movieInfoDto);
        }

        return movieInfoDtos;
    }

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

    public boolean isFavoriteMovie(long movieId) {
        if (movieIds.isEmpty()) {
            readFavoriteMovies();
        }
        return movieIds.contains(movieId);
    }
}
