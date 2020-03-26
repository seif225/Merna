package com.example.merna;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {


    @BindView(R.id.mail_et)
    EditText mailEt;
    @BindView(R.id.textInputEmail)
    TextInputLayout textInputEmail;
    @BindView(R.id.password_et)
    EditText passwordEt;
    @BindView(R.id.textInputPassword)
    TextInputLayout textInputPassword;
    @BindView(R.id.cirLoginButton)
    CircularProgressButton cirLoginButton;
    @BindView(R.id.no_account)
    TextView noAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        noAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToRegister();
            }
        });

        cirLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail, password;
                mail = mailEt.getText().toString();
                password = passwordEt.getText().toString();
                if (mail.isEmpty()) {
                    mailEt.requestFocus();
                    mailEt.setError("you have to insert your mail");

                } else if (password.isEmpty()) {
                    passwordEt.requestFocus();
                    passwordEt.setError("insert your password");

                } else {


                    cirLoginButton.startAnimation();
                    logUserIn(mail, password);
                }


            }
        });

    }

    private void logUserIn(String mail, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    sendUserToMain();
                } else {
                    Toast.makeText(LoginActivity.this, "Error has occurred , try again later", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendUserToMain() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void sendUserToRegister() {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);

    }
}
