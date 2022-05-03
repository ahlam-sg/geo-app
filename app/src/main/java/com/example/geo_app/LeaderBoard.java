package com.example.geo_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class LeaderBoard extends AppCompatActivity {

    private Query query;
    private FirebaseRecyclerOptions<UserModel> options;
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        setQuery();
        setOptions();
        setReviewRecyclerView();
    }

    @Override
    public void onStart() {
        super.onStart();
        UserModel.signInIfNotAuthenticated(getApplicationContext());
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void setQuery(){
        query = FirebaseDatabase.getInstance(Constants.DB_URL)
                .getReference()
                .child(Constants.USERS_REFERENCE)
                .orderByChild(Constants.TOTAL_SCORE_REFERENCE)
                .limitToLast(20);
    }

    private void setOptions(){
        options = new FirebaseRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class)
                .build();
    }

    private void setReviewRecyclerView(){
        RecyclerView leaderboardRV = findViewById(R.id.leaderboard_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        leaderboardRV.setLayoutManager(linearLayoutManager);
        adapter = new UserAdapter(options, findViewById(R.id.progress_bar_lav));
        leaderboardRV.setAdapter(adapter);
        Log.d("LeaderBoard", "setReviewRecyclerView");
    }
}