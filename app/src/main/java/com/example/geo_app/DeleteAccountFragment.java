package com.example.geo_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;


public class DeleteAccountFragment extends Fragment {

    private TextView enterPasswordTV;
    private EditText passwordET;
    private TextInputLayout passwordTIL;
    private Button deleteAccountBtn;
    private FirebaseUser user;
    private boolean isInputValid;
    private String provider;

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
        showUIObjectBaseOnSignInProvider();
        deleteAccountBtn.setOnClickListener(view -> {
            if (provider.equalsIgnoreCase(Constants.GOOGLE_PROVIDER)){
                authenticateUserWithGoogleProvider();
            }
            else if (provider.equalsIgnoreCase(Constants.EMAIL_PROVIDER)) {
                isInputValid = true;
                checkUserInput();
                if (isInputValid) {
                    authenticateUserWithEmailProvider();
                }
            }
        });
        return rootView;
    }

    private void showUIObjectBaseOnSignInProvider(){
        if (provider.equalsIgnoreCase(Constants.GOOGLE_PROVIDER)){
            enterPasswordTV.setVisibility(View.GONE);
            passwordET.setVisibility(View.GONE);
            passwordTIL.setVisibility(View.GONE);
        }
        else if (provider.equalsIgnoreCase(Constants.EMAIL_PROVIDER)){
            enterPasswordTV.setVisibility(View.VISIBLE);
            passwordET.setVisibility(View.VISIBLE);
            passwordTIL.setVisibility(View.VISIBLE);
        }
    }

    private void authenticateUserWithEmailProvider(){
        AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), passwordET.getText().toString());
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Log.d("DeleteAccountFragment", "authenticateUserWithEmailProvider: User re-authenticated.");
                showDeleteAccountDialog();
            }
            else {
                passwordTIL.setError(getResources().getString(R.string.password_incorrect));
            }
        });
    }

    private void authenticateUserWithGoogleProvider(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignIn.getClient(getActivity(), gso).silentSignIn().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                AuthCredential credential = GoogleAuthProvider.getCredential(task.getResult().getIdToken(), null);
                user.reauthenticate(credential).addOnCompleteListener(authenticateTask -> {
                    if (authenticateTask.isSuccessful()) {
                        Log.d("DeleteAccountFragment", "authenticateUserWithGoogleProvider: User re-authenticated.");
                        showDeleteAccountDialog();

                    }
                    else {
                        Log.d("DeleteAccountFragment", "authenticateUserWithGoogleProvider: Failed to re-authenticate user.");
                    }
                });
            }
        });
    }

    private void revokeAccessOfUserSignedInWithGoogle(){
        GoogleSignIn.getClient(
                requireContext(),
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).revokeAccess();
    }

    private void signOutUserSignedInWithGoogle(){
        GoogleSignIn.getClient(
                requireContext(),
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut();
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

    private void showDeleteAccountDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AlertDialog);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.custom_dialog_view, null);
        TextView messageTV = dialogView.findViewById(R.id.message_tv);
        messageTV.setText(R.string.confirm_delete_account);
        LottieAnimationView imgLAV = dialogView.findViewById(R.id.img_lav);
        imgLAV.setAnimation(R.raw.question_mark);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.ok, (dialog, whichButton) -> {
            Log.d("DeleteAccountFragment", "showDeleteAccountDialog: PositiveButton");
            FirebaseAuth.getInstance().signOut();
            if (provider.equalsIgnoreCase(Constants.GOOGLE_PROVIDER)){
                revokeAccessOfUserSignedInWithGoogle();
                signOutUserSignedInWithGoogle();
            }
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
    private void initializeObjects(View rootView){
        enterPasswordTV = rootView.findViewById(R.id.enter_password_tv);
        passwordET = rootView.findViewById(R.id.password_et);
        passwordTIL = rootView.findViewById(R.id.password_til);
        deleteAccountBtn = rootView.findViewById(R.id.delete_account_btn);
        user = FirebaseAuth.getInstance().getCurrentUser();
        List<? extends UserInfo> providerData = Objects.requireNonNull(user).getProviderData();
        provider = providerData.get(1).getProviderId();
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

}