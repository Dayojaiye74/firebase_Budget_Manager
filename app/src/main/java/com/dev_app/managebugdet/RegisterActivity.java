package com.dev_app.managebugdet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button btnSignIn;
    private Button btnSignUp;

    //Firebase...
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

        email = findViewById(R.id.email_signup);
        password = findViewById(R.id.password_signup);
        btnSignUp = findViewById(R.id.btn_signup_up);
        btnSignIn = findViewById(R.id.btn_signin_up);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                String mEmail = email.getText().toString().trim();
                String mPassword = password.getText().toString().trim();

                if (TextUtils.isEmpty(mEmail)) {
                    email.setError("Required field...");
                }
                if (TextUtils.isEmpty(mPassword)){
                    password.setError("Required field...");
                }

                dialog.setMessage("Processing..."); dialog.show();

                mAuth.createUserWithEmailAndPassword(mEmail,mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                           //Snackbar.make(view,"Registration Successful",Snackbar.LENGTH_SHORT).setAction("Action",null).show();

                            Toast.makeText(RegisterActivity.this, "Registration Successful..", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(RegisterActivity.this,HomeActivity.class));
                            dialog.dismiss();
                        } else{
                            //Snackbar.make(view,"Registration Error",Snackbar.LENGTH_LONG).setAction("Action",null).show();

                            Toast.makeText(RegisterActivity.this, "Error.. ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
