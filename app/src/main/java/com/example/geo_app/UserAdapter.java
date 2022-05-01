package com.example.geo_app;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class UserAdapter extends FirebaseRecyclerAdapter<UserModel, UserAdapter.UserViewholder> {

    public UserAdapter(@NonNull FirebaseRecyclerOptions<UserModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewholder holder, int position, @NonNull UserModel model) {
        holder.usernameTV.setText(model.getUsername());
        holder.highScoreTV.setText(String.valueOf(model.getHighScore()));
        holder.totalScoreTV.setText(String.valueOf(model.getTotalScore()));
        Log.d("LeaderBoard", "setAdapter: onBindViewHolder");
    }

    @NonNull
    @Override
    public UserViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_score, parent, false);
        Log.d("LeaderBoard", "setAdapter: onCreateViewHolder");
        return new UserAdapter.UserViewholder(view);
    }

    class UserViewholder extends RecyclerView.ViewHolder {
        TextView usernameTV, highScoreTV, totalScoreTV;
        public UserViewholder(@NonNull View itemView){
            super(itemView);
            usernameTV = itemView.findViewById(R.id.username_tv);
            highScoreTV = itemView.findViewById(R.id.high_score_tv);
            totalScoreTV = itemView.findViewById(R.id.total_score_tv);
        }
    }
}
