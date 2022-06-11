package com.example.geo_app;

import static java.lang.Integer.parseInt;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderBoardActivity extends MainToolbar {

    private Query query;
    private FirebaseRecyclerOptions<UserModel> options;
    private UserAdapter adapter;
    private Toolbar toolbar;
    private NumberFormat numFormat;
    private UserTopRankingStatusReceiver receiver = new UserTopRankingStatusReceiver();
    RelativeLayout currentUserScoreLayout;
    private String username = "", imageURL = "", totalScore = "0";
    long rank = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        toolbar = findViewById(R.id.main_toolbar);
        setToolbar(toolbar);
        numFormat = NumberFormat.getNumberInstance(getResources().getConfiguration().locale);
        this.registerReceiver(receiver, new IntentFilter(Constants.USER_TOP_RANKING_STATUS_ACTION));
        currentUserScoreLayout = findViewById(R.id.current_user_score_layout);
        currentUserScoreLayout.setVisibility(View.GONE);

        setQuery();
        setOptions();
        setReviewRecyclerView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
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
                .limitToLast(Constants.TOP_RANKING_LIMIT);
    }

    private void setOptions(){
        options = new FirebaseRecyclerOptions.Builder<UserModel>()
                .setQuery(query, new SnapshotParser<UserModel>() {
                    @NonNull
                    @Override
                    public UserModel parseSnapshot(@NonNull DataSnapshot snapshot) {
                        UserModel user = snapshot.getValue(UserModel.class);
                        user.setUid(snapshot.getKey());
                        return user;
                    }
                })
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
        Log.d("LeaderBoardActivity", "setReviewRecyclerView");
    }

    private class UserTopRankingStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();
            if (Objects.equals(intentAction, Constants.USER_TOP_RANKING_STATUS_ACTION)) {
                boolean isUserInTopRanking = intent.getBooleanExtra(Constants.USER_TOP_RANKING_STATUS, false);
                if (!isUserInTopRanking) {
                    readUser();
                    currentUserScoreLayout.setVisibility(View.VISIBLE);
                    Log.d("LeaderBoardActivity", "UserTopRankingStatusReceiver: onReceive");

                }
            }
        }
    }

    public void readUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference database = FirebaseDatabase.getInstance(Constants.DB_URL).getReference(Constants.USERS_REFERENCE);
        Query query = database.orderByChild(Constants.TOTAL_SCORE_REFERENCE);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    rank = snapshot.getChildrenCount();
                    for (DataSnapshot data : snapshot.getChildren()){
                        if(data.getKey().equals(user.getUid())){
                            if (data.child(Constants.USERNAME_REFERENCE).getValue() != null){
                                username = Objects.requireNonNull(data.child(Constants.USERNAME_REFERENCE).getValue()).toString();
                            }
                            if (data.child(Constants.IMAGE_URL_REFERENCE).getValue() != null){
                                imageURL = Objects.requireNonNull(data.child(Constants.IMAGE_URL_REFERENCE).getValue()).toString();
                            }
                            if (data.child(Constants.TOTAL_SCORE_REFERENCE).getValue() != null){
                                totalScore = Objects.requireNonNull(data.child(Constants.TOTAL_SCORE_REFERENCE).getValue()).toString();
                            }
                            setCurrentUserScore();
                            currentUserScoreLayout.setVisibility(View.VISIBLE);
                            Log.d("LeaderBoardActivity", "readUser: onDataChange");
                            return;
                        }
                        else {
                            rank--;
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("LeaderBoardActivity", "readUser: onCancelled");
            }
        });
    }

    private void setCurrentUserScore(){
        TextView usernameTV = findViewById(R.id.current_username_tv);
        TextView rankTV = findViewById(R.id.current_rank_tv);
        TextView levelTV = findViewById(R.id.current_level_tv);
        TextView totalScoreTV = findViewById(R.id.current_total_score_tv);
        CircleImageView profileCIV = findViewById(R.id.current_profile_civ);
        usernameTV.setText(username);
        totalScoreTV.setText(String.valueOf(numFormat.format(Integer.parseInt(totalScore))));
        rankTV.setText(String.valueOf(numFormat.format(rank)));
        double levelDouble = ((double) Integer.parseInt(totalScore) / 10000);
        int levelInt = (int)Math.floor(levelDouble);
        if (levelInt < 1){
            levelInt = 1;
        }
        String level = String.format(getResources().getString(R.string.level_without_star), String.valueOf(numFormat.format(levelInt)));
        levelTV.setText(level);
        if (!imageURL.isEmpty()){
            Glide.with(this)
                    .load(imageURL)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(profileCIV);
        }
        else {
            profileCIV.setImageDrawable(getDrawable(R.drawable.default_user));
        }
    }
}