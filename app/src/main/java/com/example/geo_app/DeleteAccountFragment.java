package com.example.geo_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;


public class DeleteAccountFragment extends Fragment {

    private EditText passwordET;
    private TextInputLayout passwordTIL;
    private Button deleteAccountBtn;
    private FirebaseUser user;
    private boolean isInputValid;

    public DeleteAccountFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_delete_account, container, false);
        initializeObjects(rootView);

        deleteAccountBtn.setOnClickListener(view -> {
            isInputValid = true;
            checkUserInput();
            if(isInputValid){
                authenticateUser();
            }
        });
        return rootView;
    }

    private void authenticateUser(){
        AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), passwordET.getText().toString());
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Log.d("DeleteAccountFragment", "User re-authenticated.");
                showDeleteAccountDialog();
            }
            else {
                passwordTIL.setError(getResources().getString(R.string.password_incorrect));
            }
        });
    }


    private void deleteUserData(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance(Constants.DB_URL).getReference().child(Constants.USERS_REFERENCE);
        databaseReference.child(user.getUid()).removeValue();
        Log.d("DeleteAccountFragment","deleteUserData");
    }

    private void deleteUserAccount(){
        user.delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Dialogs.showSuccessMessageDialog(getContext(), getResources().getString(R.string.delete_account_success));
                        Log.d("DeleteAccountFragment", "deleteUserAccount: successful");
                    }
                    else {
                        Log.d("DeleteAccountFragment", "deleteUserAccount: failed");
                    }
                });
    }

    private void initializeObjects(View rootView){
        passwordET = rootView.findViewById(R.id.password_et);
        passwordTIL = rootView.findViewById(R.id.password_til);
        deleteAccountBtn = rootView.findViewById(R.id.delete_account_btn);
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void checkUserInput(){
        if (TextUtils.isEmpty(passwordET.getText().toString())){
            passwordTIL.setError(getResources().getString(R.string.empty_error));
            isInputValid = false;
        }
        else{
            passwordTIL.setError(null);
        }
    }

    private void showDeleteAccountDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AlertDialog);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_view, null);
        TextView messageTV = dialogView.findViewById(R.id.message_tv);
        messageTV.setText(R.string.confirm_delete_account);
        LottieAnimationView imgLAV = dialogView.findViewById(R.id.img_lav);
        imgLAV.setAnimation(R.raw.question_mark);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.ok, (dialog, whichButton) -> {
            Log.d("DeleteAccountFragment", "showDeleteAccountDialog: PositiveButton");
            FirebaseAuth.getInstance().signOut();
            deleteUserData();
            deleteUserAccount();
            Handler handler = new Handler();
            handler.postDelayed(this::startSignInActivity, Constants.START_ACTIVITY_DELAY);
        });
        builder.setNegativeButton(R.string.cancel, (dialog, whichButton) -> Log.w("DeleteAccountFragment", "showDeleteAccountDialog: NegativeButton"));
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void startSignInActivity(){
        Intent intent = new Intent(getContext(), SignInActivity.class);
        startActivity(intent);
        requireActivity().finishAffinity();
    }
}