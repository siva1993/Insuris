package com.siva.insuris;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.siva.insuris.model.Movies;
import com.siva.insuris.utils.TransitionHelper;
import com.siva.insuris.utils.Utils;

import java.util.ArrayList;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> {
    ArrayList<Movies.Results> taskList;
    Activity context;
    private String strImageUrl ="http://image.tmdb.org/t/p/w600_and_h900_bestv2";

    public MoviesAdapter(Activity context, ArrayList<Movies.Results> taskList) {
        this.context = context;
        this.taskList = taskList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item_adapter, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int position) {
        Movies.Results listModel = taskList.get(position);
        viewHolder.titleTxt.setText(listModel.getTitle() );
        viewHolder.overviewTxt.setText(listModel.getOverview());
        viewHolder.releaseDateTxt.setText("Release date: "+Utils.getDate(listModel.getRelease_date()) );
        viewHolder.voteTxt.setText("Rating: " + String.valueOf(listModel.getVote_average()));

       String strImage = strImageUrl + listModel.getPoster_path();
        RequestOptions requestOptions = new RequestOptions();
//            requestOptions.transform(new GlideCropCircleTransformation(getActivity()));
        requestOptions.skipMemoryCache(true);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.error(R.drawable.ic_loading);
        requestOptions.placeholder(R.drawable.ic_loading);
        Glide.with(context)
                .load(strImage)
                .apply(requestOptions)
                .into(viewHolder.movieImg);

        viewHolder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transitionToActivity(MovieDetailsActivity.class, viewHolder,taskList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt,releaseDateTxt,voteTxt,overviewTxt;
        ImageView movieImg;
        ConstraintLayout listItem;

        public MyViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            titleTxt = itemView.findViewById(R.id.titleTxt);
            overviewTxt = itemView.findViewById(R.id.overviewTxt);
            movieImg = itemView.findViewById(R.id.movieImg);
            releaseDateTxt = itemView.findViewById(R.id.releaseDateTxt);
            voteTxt = itemView.findViewById(R.id.voteTxt);
            listItem = itemView.findViewById(R.id.listItem);
        }
    }

    private void transitionToActivity(Class target, MyViewHolder viewHolder, Movies.Results sample) {
        if(Utils.isLollipopHigher()){
            final Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants(context, false,
                    new Pair<>(viewHolder.movieImg, "movieImage"),
                    new Pair<>(viewHolder.titleTxt, "movieName"));
            startActivity(target, pairs, sample);
        }
    }

    private void startActivity(Class target, Pair<View, String>[] pairs, Movies.Results sample) {
        if(Utils.isLollipopHigher()){
            Intent i = new Intent(context, target);
            ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(context, pairs);
            i.putExtra("movieImage",strImageUrl + sample.getPoster_path());
            i.putExtra("movieName",sample.getTitle());
            i.putExtra("movieDate",sample.getRelease_date());
            i.putExtra("movieOverview",sample.getOverview());
            i.putExtra("movieRating",sample.getVote_average());
            i.putExtra("movieId",sample.getId());
            i.putExtra("movieBannerImg",strImageUrl +sample.getBackdrop_path());
            context.startActivity(i, transitionActivityOptions.toBundle());
        }
    }

}