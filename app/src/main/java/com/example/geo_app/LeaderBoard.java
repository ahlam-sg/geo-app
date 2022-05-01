package com.example.geo_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class LeaderBoard extends AppCompatActivity {

    private Query query;
    private FirebaseRecyclerOptions<User> users;
    private FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        setQuery();
        setUsers();
        setAdapter();
        setReviewRecyclerView();
    }

    @Override
    public void onStart() {
        super.onStart();
        User.signInIfNotAuthenticated(getApplicationContext());
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void setUsers(){
        users = new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(query, User.class)
                        .build();
    }

    private void setQuery(){
        query = FirebaseDatabase.getInstance(Constants.DB_URL)
                .getReference()
                .child(Constants.USERS_REFERENCE);
    }

    private void setAdapter(){
        adapter = new FirebaseRecyclerAdapter<User, UserHolder>(users) {
            @NonNull
            @Override
            public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_score, parent, false);
                return new UserHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull UserHolder holder, int position, @NonNull User model) {
                holder.usernameTV.setText(model.getUsername());
                holder.highScoreTV.setText(model.getHighScore());
                holder.totalScoreTV.setText(model.getTotalScore());
            }
        };
    }

    static class UserHolder extends RecyclerView.ViewHolder{
        TextView usernameTV, highScoreTV, totalScoreTV;
        public UserHolder(View itemView) {
            super(itemView);
            usernameTV = itemView.findViewById(R.id.username_tv);
            highScoreTV = itemView.findViewById(R.id.high_score_tv);
            totalScoreTV = itemView.findViewById(R.id.total_score_tv);
        }
    }

    private void setReviewRecyclerView(){
        RecyclerView leaderboardRV = findViewById(R.id.leaderboard_rv);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        leaderboardRV.setLayoutManager(layoutManager);
        leaderboardRV.setAdapter(adapter);
    }

}