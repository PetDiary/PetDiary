package com.example.petdiary.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.petdiary.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class kon_AnimalProfileActivity extends AppCompatActivity {

    ImageView editBtn;
    ImageView aniProfileImage;
    EditText aniName;
    Button saveBtn;
    Button cancelBtn;

    String postImgPath;
    String preImage;    // 편집 전 이미지
    String preName;     // 편집 전 이름

    boolean isAddMode = false; // 펫 추가 버튼을 눌렀을시에만 true
    boolean isEditMode = false;
    boolean isPressedSaveBtn = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kon_activity_animal_profile_edit);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);

        //데이터 수신
        Intent intent = getIntent();
        isAddMode = getIntent().getBooleanExtra("isAddMode", false);
        isEditMode = getIntent().getBooleanExtra("isEditMode",false);

        editBtn = findViewById(R.id.animalPage_edit);
        aniProfileImage = findViewById(R.id.animalPage_Image);
        aniName = findViewById(R.id.animalPage_name);
        saveBtn = findViewById(R.id.animalPage_save);
        cancelBtn = findViewById(R.id.animalPage_cancel);

        editBtn.setOnClickListener(onClickListener);
        aniProfileImage.setOnClickListener(onClickListener);
        saveBtn.setOnClickListener(onClickListener);
        cancelBtn.setOnClickListener(onClickListener);

        if(isAddMode) {
            setEditIcon(false);
            setEditMode(true);
        }


    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId())
            {
                case R.id.animalPage_edit:
                    if (editBtn.isClickable()) {
                        isEditMode = true;
                    }
                    break;
                case R.id.animalPage_Image:
                    if (isEditMode)
                        startGalleryActivity();
                    break;
                case R.id.animalPage_save:
                    if(isAddMode) {

                    }
                    else {
                        isPressedSaveBtn = true;
                        isEditMode = false;


                        setProfileImg(postImgPath);
                     //   saveDataToFirebase();

                        // 두개 합쳐도 될것같은데..?
                        setEditIcon(true);
                        setEditMode(false);

                        preName = aniName.getText().toString();
                        //preMemo = userMemo.getText().toString();
                        preImage = postImgPath;

                    }
                    break;
                case R.id.animalPage_cancel:
                    // isImageEdit = false;
                    isEditMode = false;
                    postImgPath = null;
                    postImgPath = preImage;
                    setProfileImg(preImage);
                    aniName.setText(preName);
                //    userMemo.setText(preMemo);
                    setEditIcon(true);
                    setEditMode(false);
                    onBackPressed();
                    break;


            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: // 갤러리에서 이미지 선택시
                if (resultCode == RESULT_OK) {
                    postImgPath = data.getStringExtra("postImgPath");
                    setProfileImg(postImgPath);
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isPressedSaveBtn) {
            Intent intent = new Intent();
          //  intent.putExtra("profileImg", postImgPath);
         //   intent.putExtra("aniName", aniName.getText().toString());
            //intent.putExtra("memo", userMemo.getText().toString());

          //  setResult(Activity.RESULT_OK, intent);
            finish();
        }
        super.onBackPressed();
    }


    // 편집버튼 상태 변경 on/off
    private void setEditIcon(boolean isShown) {
        if (isShown)
            editBtn.setVisibility(View.VISIBLE);
        else
            editBtn.setVisibility(View.INVISIBLE);

        editBtn.setClickable(isShown);
    }


    // 편집 버튼 상태에 따른 이름, 메모, 저장, 취소버튼 상태 변경
    private void setEditMode(boolean isEditMode) {
        if (isEditMode) {
            //  isEdit = true;
            aniName.setBackgroundColor(getBaseContext().getResources().getColor(R.color.colorAccent));
            aniName.setFocusableInTouchMode(true);

           // userMemo.setBackgroundColor(getBaseContext().getResources().getColor(R.color.colorAccent));
         //   userMemo.setFocusableInTouchMode(true);

            saveBtn.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.VISIBLE);
        } else {
            // isEdit = false;
            aniName.setBackground(null);
            aniName.setFocusableInTouchMode(false);
            aniName.setFocusable(false);

         //   userMemo.setBackground(null);
          //  userMemo.setFocusableInTouchMode(false);
          //  userMemo.setFocusable(false);

            saveBtn.setVisibility(View.INVISIBLE);
            cancelBtn.setVisibility(View.INVISIBLE);
        }
    }

    // 프로필 이미지 변경 함수
    private void setProfileImg(String profileImg) {
        //Activity activity = ProfileEditActivity.this;
        //if (activity.isFinishing())
            //return;

        Glide.with(this).load(profileImg).centerCrop().override(500).into(aniProfileImage);
    }



    // 갤러리 열기 위한 팝업생성 함수
    private void startGalleryActivity() {
        Intent intent = new Intent(getApplicationContext(), ImageChoicePopupActivity.class);
        startActivityForResult(intent, 0);
    }

    private void saveDataToFirebase() {
        // 이미지 변경시
        if(postImgPath.compareTo(preImage) !=0)
            setProfileImageToFirebase();

        // 텍스트 변경시
      //  if(preMemo.compareTo(userMemo.getText().toString()) !=0 || preName.compareTo(userName.getText().toString()) != 0)
        if( !preName.equals(aniName.getText().toString()))
            setProfileTextToFirebase();
    }
    private void setProfileImageToFirebase() {
        // String postImgPath = preImage;
        final String[] profileImg = new String[1];

        // 파이어베이스 스토리지에 이미지 저장
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference();
        final UploadTask[] uploadTask = new UploadTask[1];

        // 로컬 파일에서 업로드(스토리지)
        final Uri file = Uri.fromFile(new File(postImgPath));
        StorageReference riversRef = storageRef.child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "_profileImage.jpg");
        uploadTask[0] = riversRef.putFile(file);

        uploadTask[0].addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                // 파이어베이스의 스토리지에 저장한 이미지의 다운로드 경로를 가져옴
                final StorageReference ref = storageRef.child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "_profileImage.jpg");
                uploadTask[0] = ref.putFile(file);

                Task<Uri> urlTask = uploadTask[0].continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            profileImg[0] = downloadUri.toString();

                            postImgPath = profileImg[0];

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = user.getUid();

                            // 클라우드 파이어스토어의 users에 프로필 이미지 주소 저장
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference documentUserReference = db.collection("users").document(uid);

                            String temp = aniName.getText().toString();
                            documentUserReference
                                    .update("profileImg", profileImg[0])
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("ProfileEditActivity", "DocumentSnapshot successfully updated!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("ProfileEditActivity", "Error updating document", e);
                                        }
                                    });
                            setProfileImg(postImgPath);
                        } else {
                        }
                    }
                });

            }
        });

    }

    // 닉네임, 메모 저장
    private void setProfileTextToFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        //  클라우드 파이어스토어의 users에 프로필 이미지 주소 저장
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentUserReference = db.collection("users").document(uid);

        documentUserReference
                .update(
                        "aniName", aniName.getText().toString()
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("ProfileEditActivity", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("ProfileEditActivity", "Error updating document", e);
                    }
                });
    }

}