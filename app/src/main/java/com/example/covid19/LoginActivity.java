package com.example.covid19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseUser currentUser;
    private Button LoginButton, PhoneLoginButton;
    private EditText UserEmail, UserPasswd;
    private TextView NewAcc, ForgetPasswd;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        InitializeFields();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        NewAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToRegActivity();
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowUserLogin();
            }
        });

        PhoneLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToPhoneLogin();
            }
        });
    }

    private void SendToPhoneLogin() {
        Intent phoneLogInIntent = new Intent(LoginActivity.this, PhoneLoginActivity.class);
        startActivity(phoneLogInIntent);
    }

    private void InitializeFields() {
        LoginButton = (Button) findViewById(R.id.login_button);
        PhoneLoginButton = (Button) findViewById(R.id.phone_login_button);
        UserEmail = (EditText) findViewById(R.id.login_email);
        UserPasswd = (EditText) findViewById(R.id.login_password);
        NewAcc = (TextView) findViewById(R.id.new_account);
        ForgetPasswd = (TextView) findViewById(R.id.forget_password_link);
        progressDialog = new ProgressDialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser != null) {
            SendToMainActivity();
        }
    }

    private void SendToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void SendToRegActivity() {
        Intent regIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(regIntent);
    }

    private void AllowUserLogin() {

        String email = UserEmail.getText().toString();
        String password = UserPasswd.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Nhập email...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Nhập mật khẩu...", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setTitle("Đang đăng nhập");
            progressDialog.setMessage("Vui lòng đợi");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();


            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                        SendToMainActivity();
                        progressDialog.dismiss();
                    } else {
                        String msg = task.getException().toString();
                        Toast.makeText(LoginActivity.this, "Lỗi:" + msg, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }
}
