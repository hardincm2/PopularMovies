package com.brassbeluga.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.brassbeluga.popularmovies.data.MovieDbContract.FavoriteMovieEntry;

import javax.inject.Inject;

/**
 * Helper class for interacting with sqlite database
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 3;

    @Inject
    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String sqlCreateDbTable = "CREATE TABLE " + FavoriteMovieEntry.TABLE_NAME + " (" +
                FavoriteMovieEntry._ID + " INTEGER PRIMARY KEY," +
                FavoriteMovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL," +
                FavoriteMovieEntry.COLUMN_MOVIE_POSTER_IMAGE_PATH + " TEXT NOT NULL," +
                FavoriteMovieEntry.COLUMN_MOVIE_RATING + " TEXT NOT NULL," +
                FavoriteMovieEntry.COLUMN_MOVIE_RELEASE_DATE + " INTEGER NOT NULL," +
                FavoriteMovieEntry.COLUMN_MOVIE_RUNTIME + " TEXT NOT NULL," +
                FavoriteMovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL," +
                FavoriteMovieEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        db.execSQL(sqlCreateDbTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Just drop and recreate the table for now.
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteMovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
