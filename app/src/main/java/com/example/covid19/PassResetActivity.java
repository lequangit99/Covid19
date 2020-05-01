package com.example.covid19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import es.dmoral.toasty.Toasty;

public class PassResetActivity extends AppCompatActivity {

    private Toolbar PassToolBar;
    private EditText emailPass;
    private Button PassBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_reset);


        PassToolBar = findViewById(R.id.pass_reset_toolbar);
        emailPass = findViewById(R.id.pass_reset_email);
        PassBtn = findViewById(R.id.pass_reset_button);


        mAuth = FirebaseAuth.getInstance();


        setSupportActionBar(PassToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Đặt lại mật khẩu");


        PassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailPass.getText().toString();

                if (TextUtils.isEmpty(email)){
                    Toasty.error(PassResetActivity.this, "Nhập vào email", Toast.LENGTH_SHORT, true).show();
                }else {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toasty.success(PassResetActivity.this, "Đã gửi email đặt lại mật khẩu, kiểm tra email cỉa bạn...", Toast.LENGTH_SHORT, true).show();
                                startActivity(new Intent(PassResetActivity.this, LoginActivity.class));
                            }
                            else {
                                Toasty.error(PassResetActivity.this, "Không có địa chỉ email này", Toast.LENGTH_SHORT, true).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
