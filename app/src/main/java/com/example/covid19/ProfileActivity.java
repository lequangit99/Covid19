package com.example.covid19;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        userID = getIntent().getExtras().get("userID").toString();

        Toast.makeText(this, "UserID:"+userID, Toast.LENGTH_SHORT).show();
    }
}
