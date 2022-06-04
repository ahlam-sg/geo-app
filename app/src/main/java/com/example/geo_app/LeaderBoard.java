package com.example.geo_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.NumberFormat;

public class LeaderBoard extends MainToolbar {

    private Query query;
    private FirebaseRecyclerOptions<UserModel> options;
    private UserAdapter adapter;
    private Toolbar toolbar;
    private NumberFormat numFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        toolbar = findViewById(R.id.main_toolbar);
        setToolbar(toolbar);
        numFormat = NumberFormat.getNumberInstance(getResources().getConfiguration().locale);

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
                .limitToLast(50);
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
        adapter = new UserAdapter(options, findViewById(R.id.progress_bar), getBaseContext(), numFormat);
        leaderboardRV.setAdapter(adapter);
        Log.d("LeaderBoard", "setReviewRecyclerView");
    }
}