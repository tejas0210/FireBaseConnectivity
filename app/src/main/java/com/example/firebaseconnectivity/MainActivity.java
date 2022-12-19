package com.example.firebaseconnectivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText edtEmail, edtName, edtPassword;
    private Button btnSignUp, btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        edtEmail = findViewById(R.id.edtEmail);
        edtName = findViewById(R.id.edtName);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){

        }

    }

    private void signUp(){
        mAuth.createUserWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseDatabase.getInstance().getReference().child("my_users")
                                    .child(task.getResult().getUser().getUid())
                                    .child("username")
                                    .setValue(edtName.getText().toString());

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(edtName.getText().toString())
                                    .build();

                            FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(MainActivity.this,"Display name updated. ",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            transitionToSocialMediaActivity();
                            Toast.makeText(MainActivity.this,"Sign Up successful",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this,"Sign Up failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signIn(){
        mAuth.signInWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){


                    transitionToSocialMediaActivity();
                    Toast.makeText(MainActivity.this,"Sign In successful",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,"Sign In failed",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void transitionToSocialMediaActivity(){
        Intent intent = new Intent(MainActivity.this,SocialMediaActivity.class);
        startActivity(intent);
        finish();
    }

}