package com.example.geo_app;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.NumberFormat;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends FirebaseRecyclerAdapter<UserModel, UserAdapter.UserViewholder> {

    CircularProgressIndicator progressIndicator;
    Context baseContext;
    Context activity;
    NumberFormat numFormat;
    FirebaseUser user;
    boolean isUserInTopRanking;
    int count;

    public UserAdapter(@NonNull FirebaseRecyclerOptions<UserModel> options, CircularProgressIndicator progressIndicator,
                       Context baseContext, Context activity, NumberFormat numFormat) {
        super(options);
        this.progressIndicator = progressIndicator;
        this.baseContext = baseContext;
        this.activity = activity;
        this.numFormat = numFormat;
        user = FirebaseAuth.getInstance().getCurrentUser();
        isUserInTopRanking = false;
        count = 0;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewholder holder, int position, @NonNull UserModel model) {
        holder.rankTV.setText(numFormat.format((getItemCount() - position)));
        holder.usernameTV.setText(model.getUsername());
//        holder.highScoreTV.setText(String.valueOf(model.getHighScore()));
        holder.totalScoreTV.setText(numFormat.format(model.getTotalScore()));
        setLevel(holder, model);
        setUserBackground(holder, model);
        if (!model.getImageURL().isEmpty()){
            Glide.with(baseContext)
                    .load(model.getImageURL())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.profileCIV);
        }
        else {
            holder.profileCIV.setImageDrawable(baseContext.getResources().getDrawable(R.drawable.default_user));
        }
        count++;
        if (count == getItemCount()){
            sendUserRankingStatusBroadcast();
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
        String level = String.format(baseContext.getResources().getString(R.string.level_without_star), numFormat.format(levelInt));
        holder.levelTV.setText(level);
    }

    private void setUserBackground(UserViewholder holder, UserModel model){
        TypedValue typedValue = new TypedValue();
        activity.getTheme().resolveAttribute(R.attr.colorSecondary, typedValue, true);
        if (user.getUid().equals(model.getUid())){
            holder.userScoreLayout.setBackgroundResource(typedValue.resourceId);
            isUserInTopRanking = true;
        }
        else {
            holder.userScoreLayout.setBackgroundResource(0);
        }
    }

    @NonNull
    @Override
    public UserViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_score, parent, false);
        Log.d("UserAdapter", "setAdapter: onCreateViewHolder");
        return new UserViewholder(view);
    }

    static class UserViewholder extends RecyclerView.ViewHolder {
        TextView rankTV, usernameTV, highScoreTV, totalScoreTV, levelTV;
        CircleImageView profileCIV;
        RelativeLayout userScoreLayout;
        public UserViewholder(@NonNull View itemView){
            super(itemView);
            rankTV = itemView.findViewById(R.id.rank_tv);
            usernameTV = itemView.findViewById(R.id.username_tv);
//            highScoreTV = itemView.findViewById(R.id.high_score_tv);
            totalScoreTV = itemView.findViewById(R.id.total_score_tv);
            levelTV = itemView.findViewById(R.id.level_tv);
            profileCIV = itemView.findViewById(R.id.profile_civ);
            userScoreLayout = itemView.findViewById(R.id.user_score_layout);
        }
    }

    @Override
    public void onDataChanged() {
        if (progressIndicator != null) {
            progressIndicator.setVisibility(View.GONE);
        }
    }

    private void sendUserRankingStatusBroadcast(){
            Intent intent = new Intent(Constants.USER_TOP_RANKING_STATUS_ACTION);
            intent.putExtra(Constants.USER_TOP_RANKING_STATUS, isUserInTopRanking);
            baseContext.sendBroadcast(intent);
            Log.d("UserAdapter", "sendUserRankingStatusBroadcast");
    }
}
