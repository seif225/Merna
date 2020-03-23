package com.example.merna;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.name_et)
    EditText nameEt;
    @BindView(R.id.phone_et)
    EditText phoneEt;
    @BindView(R.id.mail_et)
    EditText mailEt;
    @BindView(R.id.password_et)
    EditText passwordEt;
    @BindView(R.id.register_button)
    Button registerButton;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEt.getText().toString();
                String phone = phoneEt.getText().toString();
                String email = mailEt.getText().toString();
                String password = passwordEt.getText().toString();

                if (name.isEmpty()){
                    nameEt.requestFocus();
                    nameEt.setError("you must choose a name");
                }
                else if (phone.isEmpty()){
                    phoneEt.requestFocus();
                    phoneEt.setError("you must insert yout phone number");
                }
                else if (email.isEmpty()){
                    mailEt.requestFocus();
                    mailEt.setError("you must insert your email");
                }
                else if (password.isEmpty()){
                    passwordEt.requestFocus();
                    passwordEt.setError("you must add a password");
                }
                    else{
                    Log.e(TAG, "onClick: "+ email + " " + password );
                    UserRegisterModel model = new UserRegisterModel();
                        model.setEmail(email);
                        model.setPassword(password);
                        model.setPhone(phone);
                        model.setName(name);
                    registerUser(model);
                }


            }
        });

    }

    private void registerUser(UserRegisterModel model) {

        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(model.getEmail(), model.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    uploadUserData(model);

                }
            }
        });


    }

    private void uploadUserData(UserRegisterModel model) {

        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful()){
                sendUserToMainActivity();
            }
            }
        });

    }

    private void sendUserToMainActivity() {
        Intent i = new Intent(this,MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

    }


}
