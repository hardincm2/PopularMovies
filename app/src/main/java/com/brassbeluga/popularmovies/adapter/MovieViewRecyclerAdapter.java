package com.brassbeluga.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.brassbeluga.popularmovies.R;
import com.brassbeluga.popularmovies.activity.MovieDetailActivity;
import com.brassbeluga.popularmovies.model.MovieInfo;
import com.squareup.picasso.Picasso;

import lombok.Getter;
import lombok.Setter;

import static com.brassbeluga.popularmovies.contants.ExtraDataKeys.MOVIE_INFO_EXTRA_DATA;
import static com.brassbeluga.popularmovies.service.MovieDbService.BASE_IMAGE_URL;

/**
 * Recycler view adapter that provides memory efficient management of the image views that will display
 * movie titles to the user.
 */
public class MovieViewRecyclerAdapter extends RecyclerView.Adapter<MovieViewRecyclerAdapter.ImageViewHolder> {
    private static final String TAG = MovieViewRecyclerAdapter.class.getSimpleName();

    @Getter
    @Setter
    private MovieInfo[] movieInfos;

    @Override
    public MovieViewRecyclerAdapter.ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.image_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        ImageViewHolder viewHolder = new ImageViewHolder(view, context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (movieInfos != null) {
            return movieInfos.length;
        } else {
            // The movie info response has not yet been set, likely because the first call to
            // TheMovieDb has yet to complete. This is NOT an error state.
            return 0;
        }
    }

    /**
     * This view holder will house the image view and additional data needed to properly
     * display each movie title poster. The same view holder will be reused as the user scrolls
     * and it's internal image will be rebound to show new movie images.
     */
    class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView;
        private Context context;
        private MovieInfo movieInfo;

        public ImageViewHolder(View itemView, Context context) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_item);
            itemView.setOnClickListener(this);
            this.context = context;
        }

        public void bind(int listIndex) {
            // Safety check to make sure the index being bound to is not larger than the total number
            // of movie info objects we have to show.
            if (listIndex < movieInfos.length) {
                movieInfo = movieInfos[listIndex];
                Picasso.with(context).load(BASE_IMAGE_URL + movieInfo.poster_path).into(imageView);
            } else {
                Log.e(TAG, String.format("Attempt to bind view for list index %s but only %s MovieInfo objects available",
                           listIndex, movieInfos.length));
            }
        }

        @Override
        public void onClick(View v) {
            Intent movieDetailsIntent = new Intent(context, MovieDetailActivity.class);
            movieDetailsIntent.putExtra(MOVIE_INFO_EXTRA_DATA, movieInfo);
            context.startActivity(movieDetailsIntent);
        }
    }
}
