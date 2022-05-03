package com.example.geo_app;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends FirebaseRecyclerAdapter<UserModel, UserAdapter.UserViewholder> {

    LottieAnimationView progressBar;
    Context context;

    public UserAdapter(@NonNull FirebaseRecyclerOptions<UserModel> options, LottieAnimationView progressBar, Context context) {
        super(options);
        this.progressBar = progressBar;
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewholder holder, int position, @NonNull UserModel model) {
        holder.rankTV.setText(String.valueOf((getItemCount()-position)));
        holder.usernameTV.setText(model.getUsername());
        holder.highScoreTV.setText(String.valueOf(model.getHighScore()));
        holder.totalScoreTV.setText(String.valueOf(model.getTotalScore()));
        if (!model.getImageURL().isEmpty()){
            Glide.with(context)
                    .load(model.getImageURL())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.profileCIV);
        }
        else {
            holder.profileCIV.setImageDrawable(context.getResources().getDrawable(R.drawable.defualt_user));
        }
        Log.d("UserAdapter", "setAdapter: onBindViewHolder");
    }


    private boolean isImgURLNull(UserModel model){
        return model.getImageURL().isEmpty();
    }

    @NonNull
    @Override
    public UserViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_score, parent, false);
        Log.d("UserAdapter", "setAdapter: onCreateViewHolder");
        return new UserAdapter.UserViewholder(view);
    }

    class UserViewholder extends RecyclerView.ViewHolder {
        TextView rankTV, usernameTV, highScoreTV, totalScoreTV;
        CircleImageView profileCIV;
        public UserViewholder(@NonNull View itemView){
            super(itemView);
            rankTV = itemView.findViewById(R.id.rank_tv);
            usernameTV = itemView.findViewById(R.id.username_tv);
            highScoreTV = itemView.findViewById(R.id.high_score_tv);
            totalScoreTV = itemView.findViewById(R.id.total_score_tv);
            profileCIV = itemView.findViewById(R.id.profile_civ);
        }
    }

    @Override
    public void onDataChanged() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }
}
