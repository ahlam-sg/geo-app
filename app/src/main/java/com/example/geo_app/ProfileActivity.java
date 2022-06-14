package com.example.geo_app;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.Objects;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends CustomToolbar {

    private FirebaseUser currentUser;
    private CircleImageView imageCIV;
    private TextView usernameTV, totalScoreTV;
    private String username = "", imageURL = "", totalScore = "0";
    private LinearProgressIndicator levelProgressIndicator;
    private TextView levelTV, nextLevelTV;
    private RelativeLayout userInfoLayout, levelInfoLayout;
    private NumberFormat numFormat;
    FloatingActionButton editUsernameFAB, editImageFAB, deleteImageFAB;
    boolean isFABMenuOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.custom_toolbar);
        setToolbar(toolbar);
        initializeObjects();
        showProgressbar();
    }

    @Override
    public void onStart() {
        super.onStart();
        UserModel.signInIfNotAuthenticated(getApplicationContext());
    }

    private void setLevel(){
        double levelDouble = ((double) Integer.parseInt(totalScore) / 10000);
        int levelInt = (int)Math.floor(levelDouble);
        if (levelInt < 1){
            levelInt = 1;
            levelDouble++;
        }
        String level = String.format(getResources().getString(R.string.level), numFormat.format(levelInt));
        levelTV.setText(level);
        double levelProgressStatus = (levelDouble - levelInt) * 100;
        levelProgressIndicator.setProgress((int)levelProgressStatus);
        int remainingPoints = (int)(((double)((100 - levelProgressStatus) / 100)) * 10000);
        String nextLevel = String.format(getResources().getString(R.string.next_level),
                numFormat.format(remainingPoints),
                numFormat.format(levelInt + 1));
        nextLevelTV.setText(nextLevel);
    }

    private void setProfile(){
        usernameTV.setText(username);
        totalScoreTV.setText(numFormat.format(Integer.parseInt(totalScore)));
        if(!imageURL.isEmpty()){
            Glide.with(getApplicationContext())
                    .load(imageURL)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageCIV);
        }
        else {
            imageCIV.setImageDrawable(getResources().getDrawable(R.drawable.default_user));
        }
        setLevel();
    }

    public void readUser(){
        FirebaseDatabase database = FirebaseDatabase.getInstance(Constants.DB_URL);
        DatabaseReference userDatabase = database.getReference().child(Constants.USERS_REFERENCE).child(currentUser.getUid());
        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                if (data.exists()){
                    if (data.child(Constants.USERNAME_REFERENCE).getValue() != null){
                        username = Objects.requireNonNull(data.child(Constants.USERNAME_REFERENCE).getValue()).toString();
                    }
                    if (data.child(Constants.IMAGE_URL_REFERENCE).getValue() != null){
                        imageURL = Objects.requireNonNull(data.child(Constants.IMAGE_URL_REFERENCE).getValue()).toString();
                    }
                    if (data.child(Constants.TOTAL_SCORE_REFERENCE).getValue() != null){
                        totalScore = Objects.requireNonNull(data.child(Constants.TOTAL_SCORE_REFERENCE).getValue()).toString();
                    }
                    setProfile();
                    hideProgressbar();
                    showProfile();
                }
                Log.d("ProfileActivity", "readUser: onDataChange");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ProfileActivity", "readUser: onCancelled", databaseError.toException());
            }
        });
    }

    private void hideProfile(){
        userInfoLayout.setVisibility(View.GONE);
        levelInfoLayout.setVisibility(View.GONE);
        findViewById(R.id.edit_fab_layout).setVisibility(View.GONE);
    }

    private void showProfile(){
        userInfoLayout.setVisibility(View.VISIBLE);
        levelInfoLayout.setVisibility(View.VISIBLE);
        findViewById(R.id.edit_fab_layout).setVisibility(View.VISIBLE);
    }

    private void showProgressbar(){
        hideProfile();
        findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        readUser();
    }

    private void hideProgressbar(){
        findViewById(R.id.progress_bar).setVisibility(View.GONE);
    }

    public void editFabMenu(View view){
        if(!isFABMenuOpen){
            openFABMenu();
        }
        else{
            closeFABMenu();
        }
    }

    private void openFABMenu(){
        isFABMenuOpen = true;
        editUsernameFAB.animate().translationY(-getResources().getDimension(R.dimen._50sdp));
        editImageFAB.animate().translationY(-getResources().getDimension(R.dimen._45sdp));
        editImageFAB.animate().translationX(-getResources().getDimension(R.dimen._50sdp));
        deleteImageFAB.animate().translationY(getResources().getDimension(R.dimen._10sdp));
        deleteImageFAB.animate().translationX(-getResources().getDimension(R.dimen._50sdp));
    }

    private void closeFABMenu(){
        isFABMenuOpen = false;
        editUsernameFAB.animate().translationY(0);
        editImageFAB.animate().translationY(0);
        editImageFAB.animate().translationX(0);
        deleteImageFAB.animate().translationY(0);
        deleteImageFAB.animate().translationX(0);
    }

    public void editUsername(View view){
        UpdateProfile.showUpdateUsernameDialog(ProfileActivity.this);
    }

    public void pickImage(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, Constants.PICK_IMAGE_REQ);
    }

    public void deleteImage(View view){
        UpdateProfile.showDeleteImageDialog(ProfileActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.PICK_IMAGE_REQ && resultCode == Activity.RESULT_OK) {
            try {
                InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                UpdateProfile.uploadImageToStorage(inputStream);
                Log.d("ProfileActivity", "onActivityResult: pickImage success");
            } catch (FileNotFoundException e) {
                Log.d("ProfileActivity", "onActivityResult: " + e);
            }
        }
    }

    private void initializeObjects(){
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        numFormat = NumberFormat.getNumberInstance(getResources().getConfiguration().locale);
        imageCIV = findViewById(R.id.image_CIV);
        usernameTV = findViewById(R.id.user_name_tv);
        totalScoreTV = findViewById(R.id.total_score_tv);
        levelProgressIndicator = findViewById(R.id.level_lpi);
        levelTV = findViewById(R.id.level_tv);
        nextLevelTV = findViewById(R.id.next_level_tv);
        userInfoLayout = findViewById(R.id.user_info_layout);
        levelInfoLayout = findViewById(R.id.level_info_layout);
        editUsernameFAB = (FloatingActionButton) findViewById(R.id.edit_username_fab);
        editImageFAB = (FloatingActionButton) findViewById(R.id.edit_image_fab);
        deleteImageFAB = (FloatingActionButton) findViewById(R.id.delete_image_fab);
    }
}