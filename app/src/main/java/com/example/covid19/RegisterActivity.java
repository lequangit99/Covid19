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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private Button RegButton;
    private EditText RegEmail, RegPasswd;
    private TextView AlreadyHaveAcc;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference RootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        InitializeFields();

        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();

        AlreadyHaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToLoginActivity();
            }
        });

        RegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAcc();
            }
        });

    }

    private void CreateNewAcc() {
        String email = RegEmail.getText().toString();
        String password = RegPasswd.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Nhập email...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Nhập mật khẩu...", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setTitle("Đang tạo tài khoản");
            progressDialog.setMessage("Chúng tôi đang tạo tài khoản, vui lòng đợi");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                        RootRef.child("Users").child(currentUserID).setValue("");

                        Toast.makeText(RegisterActivity.this, "Đã tạo tài khoản thành công", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        SendToMainActivity();
                    } else {
                        String msg = Objects.requireNonNull(task.getException()).toString();
                        Toast.makeText(RegisterActivity.this, "Lỗi: " + msg, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

    private void SendToMainActivity() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void SendToLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    private void InitializeFields() {
        RegButton = (Button) findViewById(R.id.register_button);
        RegEmail = (EditText) findViewById(R.id.reg_email);
        RegPasswd = (EditText) findViewById(R.id.reg_password);
        AlreadyHaveAcc = (TextView) findViewById(R.id.already_have_acc);
        progressDialog = new ProgressDialog(this);
    }
}
