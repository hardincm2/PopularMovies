package com.brassbeluga.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.brassbeluga.popularmovies.data.MovieDbContract.FavoriteMovieEntry;

/**
 * Content provider that provides access to movie data
 */
public class MovieContentProvider extends ContentProvider {
    public static final int MOVIES = 100;
    public static final int MOVIES_WITH_ID = 101;

    private MovieDbHelper movieDbHelper;
    private UriMatcher movieUriMatcher;

    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());

        // Initialize a UriMatcher that will match URIs supported by this content provider.
        movieUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        movieUriMatcher.addURI(MovieDbContract.AUTHORITY, MovieDbContract.PATH_MOVIES_FAVORITES, MOVIES);
        movieUriMatcher.addURI(MovieDbContract.AUTHORITY, MovieDbContract.PATH_MOVIES_FAVORITES + "/#", MOVIES_WITH_ID);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        final SQLiteDatabase db = movieDbHelper.getReadableDatabase();

        int matchCode = movieUriMatcher.match(uri);

        Cursor cursorResult;
        switch (matchCode) {
            case MOVIES :
                cursorResult =  db.query(FavoriteMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default: throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        cursorResult.setNotificationUri(getContext().getContentResolver(), uri);

        return cursorResult;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = movieDbHelper.getWritableDatabase();

        int matchCode = movieUriMatcher.match(uri);
        Uri returnUri;
        switch (matchCode) {
            case MOVIES :
                long id = db.insert(FavoriteMovieEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(FavoriteMovieEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the content resolver that the uri has changed
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = movieDbHelper.getWritableDatabase();

        int matchCode = movieUriMatcher.match(uri);
        int deletedRows;
        switch (matchCode) {
            case MOVIES_WITH_ID :
                String deleteId = Long.toString(ContentUris.parseId(uri));
                deletedRows = db.delete(FavoriteMovieEntry.TABLE_NAME, "_id=?", new String[]{deleteId});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items deleted
        if (deletedRows != 0) {
            // A favorite movie was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Operation not yet implemented");
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Operation not yet implemented");
    }
}
