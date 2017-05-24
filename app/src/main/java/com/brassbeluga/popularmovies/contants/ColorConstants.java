package com.brassbeluga.popularmovies.contants;


import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;

/**
 * Contains a few different color contants used throughout the app.
 */
public class ColorConstants {
    public static final ColorFilter FAVORITED_COLOR_FILTER = new LightingColorFilter(0xF4EE42, 0xFFAA0000);

    public static final int[] AUTHOR_COLOR_CODES = new int[] {
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.CYAN,
            Color.MAGENTA
    };
}
