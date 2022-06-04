package com.example.geo_app;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.text.NumberFormat;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends FirebaseRecyclerAdapter<UserModel, UserAdapter.UserViewholder> {

    CircularProgressIndicator progressIndicator;
    Context context;
    NumberFormat numFormat;

    public UserAdapter(@NonNull FirebaseRecyclerOptions<UserModel> options, CircularProgressIndicator progressIndicator, Context context, NumberFormat numFormat) {
        super(options);
        this.progressIndicator = progressIndicator;
        this.context = context;
        this.numFormat = numFormat;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewholder holder, int position, @NonNull UserModel model) {
        holder.rankTV.setText(String.valueOf(numFormat.format((getItemCount()-position))));
        holder.usernameTV.setText(model.getUsername());
//        holder.highScoreTV.setText(String.valueOf(model.getHighScore()));
        holder.totalScoreTV.setText(String.valueOf(numFormat.format(model.getTotalScore())));
        setLevel(holder, model);
        if (!model.getImageURL().isEmpty()){
            Glide.with(context)
                    .load(model.getImageURL())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.profileCIV);
        }
        else {
            holder.profileCIV.setImageDrawable(context.getResources().getDrawable(R.drawable.default_user));
        }
        Log.d("UserAdapter", "setAdapter: onBindViewHolder");
    }

    private void setLevel(UserViewholder holder, UserModel model){
        double levelDouble = ((double) model.getTotalScore() / 10000);
        int levelInt = (int)Math.floor(levelDouble);
        if (levelInt < 1){
            levelInt = 1;
            levelDouble++;
        }
        String level = String.format(context.getResources().getString(R.string.level_without_star), String.valueOf(numFormat.format(levelInt)));
        holder.levelTV.setText(level);
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
        TextView rankTV, usernameTV, highScoreTV, totalScoreTV, levelTV;
        CircleImageView profileCIV;
        public UserViewholder(@NonNull View itemView){
            super(itemView);
            rankTV = itemView.findViewById(R.id.rank_tv);
            usernameTV = itemView.findViewById(R.id.username_tv);
//            highScoreTV = itemView.findViewById(R.id.high_score_tv);
            totalScoreTV = itemView.findViewById(R.id.total_score_tv);
            levelTV = itemView.findViewById(R.id.level_tv);
            profileCIV = itemView.findViewById(R.id.profile_civ);
        }
    }

    @Override
    public void onDataChanged() {
        if (progressIndicator != null) {
            progressIndicator.setVisibility(View.GONE);
        }
    }
}
