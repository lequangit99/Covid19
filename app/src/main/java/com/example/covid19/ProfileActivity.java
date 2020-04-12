package com.example.covid19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String userID, senderUserID, current_State;

    private CircleImageView userProfileImage;
    private TextView userProfileName, userProfileStatus;
    private Button SendMessRequestButton;
    private FirebaseAuth mAuth;

    private DatabaseReference UserRef, ChatRequestRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");

        userID = getIntent().getExtras().get("userID").toString();
        senderUserID = mAuth.getCurrentUser().getUid();
        Toast.makeText(this, "UserID:"+userID, Toast.LENGTH_SHORT).show();


        userProfileImage = (CircleImageView) findViewById(R.id.visit_profile_image);
        userProfileName = (TextView) findViewById(R.id.visit_user_name);
        userProfileStatus = (TextView) findViewById(R.id.visit_profile_status);
        SendMessRequestButton = (Button) findViewById(R.id.send_message_request_button);
        current_State = "new";

        RetrieveUserInfo();
    }

    private void RetrieveUserInfo() {
        UserRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()&& dataSnapshot.hasChild("image")){
                    String userImage = dataSnapshot.child("image").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();

                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(userProfileImage);

                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);

                    ManageChatRequests();
                }

                else {
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();

                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);

                    ManageChatRequests();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ManageChatRequests() {

        ChatRequestRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userID)){
                    String request_type = dataSnapshot.child(userID).child("request_type").getValue().toString();

                    if (request_type.equals("sent")){
                        current_State = "request_sent";
                        SendMessRequestButton.setText("Huỷ yêu cầu");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (!senderUserID.equals(userID)){
            SendMessRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendMessRequestButton.setEnabled(false);

                    if (current_State.equals("new")){
                        SendChatRequest();
                    }
                    if (current_State.equals("request_sent")){
                        CancelChatRequest();
                    }
                }
            });
        }
        else {
            SendMessRequestButton.setVisibility((View.INVISIBLE));
        }
    }

    private void CancelChatRequest() {
        ChatRequestRef.child(senderUserID).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    ChatRequestRef.child(userID).child(senderUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                SendMessRequestButton.setEnabled(true);
                                current_State = "new";
                                SendMessRequestButton.setText("Gửi tin nhắn");
                            }
                        }
                    });
                }
            }
        });
    }

    private void SendChatRequest() {
        ChatRequestRef.child(senderUserID).child(userID).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    ChatRequestRef.child(userID).child(senderUserID).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            SendMessRequestButton.setEnabled(true);
                            current_State = "request_sent";
                            SendMessRequestButton.setText("Huỷ yêu cầu");
                        }
                    });
                }
            }
        });
    }
}
