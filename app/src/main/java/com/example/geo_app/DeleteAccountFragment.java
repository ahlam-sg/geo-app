package com.example.geo_app;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.util.List;
import java.util.Objects;


public class DeleteAccountFragment extends Fragment {

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
        deleteAccountBtn.setOnClickListener(view -> {
            isInputValid = true;
            checkUserInput();
            if (isInputValid) {
                authenticateUserWithEmailProvider();
            }
        });
        return rootView;
    }

    private void authenticateUserWithEmailProvider(){
        AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), passwordET.getText().toString());
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Log.d("DeleteAccountFragment", "authenticateUserWithEmailProvider: User re-authenticated.");
                DeleteAccount.showDeleteAccountDialog(requireActivity(), requireContext(), provider);
            }
            else {
                passwordTIL.setError(getResources().getString(R.string.password_incorrect));
            }
        });
    }

    private void initializeObjects(View rootView){
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