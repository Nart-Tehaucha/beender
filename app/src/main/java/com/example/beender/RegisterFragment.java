package com.example.beender;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

// Fragment for registration
public class RegisterFragment extends Fragment {

    private static final String TAG = "EmailPassword";
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private Button registerButton;

    private EditText registerEmail;
    private EditText registerPassword;
    private EditText registerName;

    // Init login form
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Auto log-in :
//        if(currentUser != null){
//            updateUI(currentUser);
//        }
    }
    // [END on_start_check_user]

    // Init view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_register, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Views
        registerEmail = view.findViewById(R.id.registerEmail);
        registerPassword = view.findViewById(R.id.registerPassword);
        registerName = view.findViewById(R.id.registerName);

        // Buttons
        registerButton = view.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Login user and move to main screen if successful
                createAccount(registerEmail.getText().toString().trim(), registerPassword.getText().toString().trim(), registerName.getText().toString().trim());
            }
        });

    }




    // --------------- AUTH METHODS ----------------
    // Register new account to Firestore, and add it to the local DB
    private void createAccount(String email, String password, String fullName) {
        if (!validateForm()) {
            return;
        }

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getActivity(), task.getException().toString(),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END create_user_with_email]
    }

    // Check if the form is valid
    private boolean validateForm() {
        boolean valid = true;

        String email = registerEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            registerEmail.setError("Required.");
            valid = false;
        } else {
            registerEmail.setError(null);
        }

        String password = registerPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            registerPassword.setError("Required.");
            valid = false;
        } else {
            registerPassword.setError(null);
        }

        return valid;
    }

    // Start main activity, also send user's name, and isAdmin.
    private void updateUI(FirebaseUser user) {
        boolean isAdmin = false;
        if(user != null) {
            if(user.getEmail().equals("admin@admin.com")) {
                isAdmin = true;
            }

            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.putExtra("isAdmin", isAdmin);
            startActivity(intent);
        }
    }
}