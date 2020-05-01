package com.example.covid19;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class SettingsActivity extends AppCompatActivity {

    private Button UpdateAccSettings;
    private EditText userName, userStatus, soDienThoai, diaChi;
    private CircleImageView userProfileImage;
    private TextView ngaySinh;
    private RadioButton rNam, rNu, rKhac;

    private String currentUserID, Sex;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    private static final int GalleryPick = 1;
    private StorageReference UserProfileImagesRef;
    private ProgressDialog loadingBar;
    private DatePickerDialog datePickerDialog;
    private Calendar calendar;

    private Toolbar SettingsToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        InitializeFields();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        userName.setVisibility(View.INVISIBLE);

        UpdateAccSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
            }
        });

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);
            }
        });

        RetrieveUserInfo();


        //Ngay Sinh
        ngaySinh.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                int ngay = calendar.get(Calendar.DAY_OF_MONTH);
                int thang = calendar.get(Calendar.MONTH);
                int nam = calendar.get(Calendar.YEAR);

                datePickerDialog = new DatePickerDialog(SettingsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        ngaySinh.setText(dayOfMonth+"/"+month+"/"+year);
                    }
                }, ngay, thang , nam);
                datePickerDialog.show();
            }
        });

        soDienThoai.setInputType(InputType.TYPE_CLASS_PHONE);

        //Gioi Tinh
        rNam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sex = "nam";
            }
        });
        rNu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sex = "nu";
            }
        });
        rKhac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sex = "khac";
            }
        });
    }

    private void RetrieveUserInfo() {
        RootRef.child("Users").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("image"))))
                        {
                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrievesStatus = dataSnapshot.child("status").getValue().toString();
                            String retrieveProfileImage = dataSnapshot.child("image").getValue().toString();
                            String retrievePhoneNumber = dataSnapshot.child("phone").getValue().toString();
                            String retrieveNgaySinh = dataSnapshot.child("ngaysinh").getValue().toString();
                            String retrieveSex = dataSnapshot.child("sex").getValue().toString();
                            String retrieveDiaChi = dataSnapshot.child("diachi").getValue().toString();


                            userName.setText(retrieveUserName);
                            userStatus.setText(retrievesStatus);
                            soDienThoai.setText(retrievePhoneNumber);
                            if (retrieveSex.equals("nu")){
                                rNu.setChecked(true);
                            }
                            if (retrieveSex.equals("nam")){
                                rNam.setChecked(true);
                            }
                            if (retrieveSex.equals("khac")){
                                rKhac.setChecked(true);
                            }
                            ngaySinh.setText(retrieveNgaySinh);
                            diaChi.setText(retrieveDiaChi);
                            Picasso.get().load(retrieveProfileImage).into(userProfileImage);
                        }
                        else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")))
                        {
                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrievesStatus = dataSnapshot.child("status").getValue().toString();
                            String retrievePhoneNumber = dataSnapshot.child("phone").getValue().toString();
                            String retrieveNgaySinh = dataSnapshot.child("ngaysinh").getValue().toString();
                            String retrieveSex = dataSnapshot.child("sex").getValue().toString();
                            String retrieveDiaChi = dataSnapshot.child("diachi").getValue().toString();


                            userName.setText(retrieveUserName);
                            userStatus.setText(retrievesStatus);
                            soDienThoai.setText(retrievePhoneNumber);
                            if (retrieveSex.equals("nu")){
                                rNu.setChecked(true);
                            }
                            if (retrieveSex.equals("nam")){
                                rNam.setChecked(true);
                            }
                            if (retrieveSex.equals("khac")){
                                rKhac.setChecked(true);
                            }
                            ngaySinh.setText(retrieveNgaySinh);
                            diaChi.setText(retrieveDiaChi);
                        }
                        else
                        {
                            userName.setVisibility(View.VISIBLE);
                            try {
                                String retrieveProfileImage = dataSnapshot.child("image").getValue().toString();
                                Picasso.get().load(retrieveProfileImage).into(userProfileImage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void UpdateSettings() {
        String setUserName = userName.getText().toString();
        String setStatus = userStatus.getText().toString();
        String setStd = soDienThoai.getText().toString();
        String setNgaySinh = ngaySinh.getText().toString();
        String setDiaChi = diaChi.getText().toString();

        if (TextUtils.isEmpty(setUserName) || TextUtils.isEmpty(setStatus) || TextUtils.isEmpty(setStd) ||
                TextUtils.isEmpty(setNgaySinh) || TextUtils.isEmpty(setDiaChi) || TextUtils.isEmpty(Sex))
        {
            Toasty.error(this, "Nhập vào đầy đủ thông tin",Toast.LENGTH_SHORT, true).show();
        }
        else
        {
            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserID);
            profileMap.put("name", setUserName);
            profileMap.put("status", setStatus);
            profileMap.put("phone", setStd);
            profileMap.put("sex", Sex);
            profileMap.put("ngaysinh", setNgaySinh);
            profileMap.put("diachi", setDiaChi);

            RootRef.child("Users").child(currentUserID).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                SendToMainActivity();
                                Toasty.success(SettingsActivity.this, "Cập nhật thành công",Toast.LENGTH_SHORT, true).show();
                            }
                            else
                            {
                                String message = Objects.requireNonNull(task.getException()).toString();
                                Toast.makeText(SettingsActivity.this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK)
            {
                loadingBar.setTitle("Ảnh đại diện");
                loadingBar.setMessage("Đang tải lên ảnh của bạn, vui lòng đợi");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                Uri resultUri = result.getUri();

                StorageReference filePath = UserProfileImagesRef.child(currentUserID + ".jpg");

                final UploadTask uploadTask = filePath.putFile(resultUri);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downloaedUrl = uri.toString();

                                RootRef.child("Users").child(currentUserID).child("image")
                                    .setValue(downloaedUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                Toasty.success(SettingsActivity.this, "Đăng ảnh thành công",Toast.LENGTH_SHORT, true).show();

                                                loadingBar.dismiss();
                                            }
                                            else
                                            {
                                                String message = task.getException().toString();
                                                Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });
                            }
                        });
                    }
                });
            }
        }
    }

    private void SendToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void InitializeFields() {
        UpdateAccSettings = findViewById(R.id.update_settings_button);
        userName = findViewById(R.id.set_user_name);
        userStatus = findViewById(R.id.set_profile_status);
        userProfileImage = findViewById(R.id.set_profile_image);
        ngaySinh = findViewById(R.id.txt_chon_ngaysinh);
        soDienThoai = (EditText) findViewById(R.id.set_profile_sdt);
        diaChi = findViewById(R.id.set_profile_diachi);

        rNam = findViewById(R.id.select_nam);
        rNu = findViewById(R.id.select_nu);
        rKhac = findViewById(R.id.select_khac);

        loadingBar = new ProgressDialog(this);

        SettingsToolBar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(SettingsToolBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Cài đặt Tài khoản");
    }

}
