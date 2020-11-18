package com.example.petdiary.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.petdiary.R;
import com.example.petdiary.fragment.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private String password = "";

    TextView toolbarNickName;
    ImageView genter_icon;
    BottomNavigationView bottomNavigationView;
    FragmentMain fragmentMain;
    FragmentSub fragmentSub;
    FragmentNewPost fragmentNewPost;
    FragmentMy fragmentMy;
    FragmentContentMain fragmentContentMain;

    private FragmentManager fragmentManager;

//    private void getAppKeyHash() {
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md;
//                md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String something = new String(Base64.encode(md.digest(), 0));
//                Log.e("Hash key", something);
//            }
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            Log.e("name not found", e.toString());
//        }
//    }

    private DrawerLayout drawerLayout;
    private View drawerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getAppKeyHash();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        if(intent != null) {//푸시알림을 선택해서 실행한것이 아닌경우 예외처리
            String notificationData = intent.getStringExtra("FCM_PetDiary");
            if(notificationData != null)
                Log.d("FCM_PetDiary", notificationData);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerView = (View) findViewById(R.id.drawerView);
        drawerLayout.setDrawerListener(listener);
        toolbarNickName = findViewById(R.id.toolbar_nickName);
        genter_icon = findViewById(R.id.genter_icon);

        if (user == null) {
            myStartActivity(LoginActivity.class);
            finish();
        } else {
            checkPassword();
        }
    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 햄버거메뉴

    public void blockFriendOnClick(View view) {
        myStartActivity2(SettingBlockFriendsActivity.class);
        //startToast("차단친구");
    }

    public void noticeOnClick(View view){
        myStartActivity2(SettingNotificationActivity.class);
        //startToast("알림 설정");
    }

    public void passwordSetOnClick(View view){
        myStartActivity2(SettingResetPasswordActivity.class);
        //startToast("비밀번호 변경");
    }

    public void customerCenterOnClick(View view){
        myStartActivity2(SettingCustomerActivity.class);
        //startToast("고객센터");
    }

    public void logOutOnClick(View view){
        startPopupActivity();
        //startToast("로그아웃");
    }

    public void unRegisterOnClick(View view){
        myStartActivity2(SettingLeaveActivity.class);
        //startToast("회원탈퇴");
    }

    public void AppInfoOnClick(View view){
        myStartActivity2(SettingAppInfoActivity.class);
        startToast("앱 정보");
    }

    private void startPopupActivity(){
        Intent intent = new Intent(getApplicationContext(), LogoutPopupActivity.class);
        startActivityForResult(intent, 0);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                if(drawerLayout.isDrawerOpen(drawerView)){
                    drawerLayout.closeDrawer(drawerView);
                } else {
                    drawerLayout.openDrawer(drawerView);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    DrawerLayout.DrawerListener listener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
        }

        @Override
        public void onDrawerStateChanged(int newState) {
        }
    };

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setFirst() {
        fragmentManager = getSupportFragmentManager();

        fragmentMain = new FragmentMain();

        fragmentManager.beginTransaction().replace(R.id.main_layout, fragmentMain).commit();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.tab1:
                        if(fragmentMain == null){
                            fragmentMain = new FragmentMain();
                            fragmentManager.beginTransaction().add(R.id.main_layout, fragmentMain).commit();
                        }
                        if(fragmentMain != null){
                            fragmentManager.beginTransaction().show(fragmentMain).commit();
                        }
                        if(fragmentSub != null){
                            fragmentManager.beginTransaction().hide(fragmentSub).commit();
                        }
                        if(fragmentNewPost != null){
                            fragmentManager.beginTransaction().hide(fragmentNewPost).commit();
                        }
                        if(fragmentMy != null){
                            fragmentManager.beginTransaction().hide(fragmentMy).commit();
                        }
                        if(fragmentContentMain != null){
                            fragmentManager.beginTransaction().hide(fragmentContentMain).commit();
                        }
                        return true;
                    case R.id.tab2:
                        if(fragmentSub == null){
                            fragmentSub = new FragmentSub();
                            fragmentManager.beginTransaction().add(R.id.main_layout, fragmentSub).commit();
                        }
                        if(fragmentSub != null){
                            fragmentManager.beginTransaction().show(fragmentSub).commit();
                        }
                        if(fragmentMain != null){
                            fragmentManager.beginTransaction().hide(fragmentMain).commit();
                        }
                        if(fragmentNewPost != null){
                            fragmentManager.beginTransaction().hide(fragmentNewPost).commit();
                        }
                        if(fragmentMy != null){
                            fragmentManager.beginTransaction().hide(fragmentMy).commit();
                        }
                        if(fragmentContentMain != null){
                            fragmentManager.beginTransaction().hide(fragmentContentMain).commit();
                        }
                        return true;
                    case R.id.tab3:
                        if(fragmentNewPost == null){
                            fragmentNewPost = new FragmentNewPost();
                            fragmentManager.beginTransaction().add(R.id.main_layout, fragmentNewPost).commit();
                        }
                        if(fragmentNewPost != null){
                            fragmentManager.beginTransaction().show(fragmentNewPost).commit();
                        }
                        if(fragmentMain != null){
                            fragmentManager.beginTransaction().hide(fragmentMain).commit();
                        }
                        if(fragmentSub != null){
                            fragmentManager.beginTransaction().hide(fragmentSub).commit();
                        }
                        if(fragmentMy != null){
                            fragmentManager.beginTransaction().hide(fragmentMy).commit();
                        }
                        if(fragmentContentMain != null){
                            fragmentManager.beginTransaction().hide(fragmentContentMain).commit();
                        }
                        return true;
                    case R.id.tab4:
                        if(fragmentMy == null){
                            fragmentMy = new FragmentMy();
                            fragmentManager.beginTransaction().add(R.id.main_layout, fragmentMy).commit();
                        }
                        if(fragmentMy != null){
                            fragmentManager.beginTransaction().show(fragmentMy).commit();
                        }
                        if(fragmentMain != null){
                            fragmentManager.beginTransaction().hide(fragmentMain).commit();
                        }
                        if(fragmentSub != null){
                            fragmentManager.beginTransaction().hide(fragmentSub).commit();
                        }
                        if(fragmentNewPost != null){
                            fragmentManager.beginTransaction().hide(fragmentNewPost).commit();
                        }
                        if(fragmentContentMain != null){
                            fragmentManager.beginTransaction().hide(fragmentContentMain).commit();
                        }
                        return true;
                    case R.id.tab5:
                        if(fragmentContentMain == null){
                            fragmentContentMain = new FragmentContentMain();
                            fragmentManager.beginTransaction().add(R.id.main_layout, fragmentContentMain).commit();
                        }
                        if(fragmentContentMain != null){
                            fragmentManager.beginTransaction().show(fragmentContentMain).commit();
                        }
                        if(fragmentMain != null){
                            fragmentManager.beginTransaction().hide(fragmentMain).commit();
                        }
                        if(fragmentSub != null){
                            fragmentManager.beginTransaction().hide(fragmentSub).commit();
                        }
                        if(fragmentNewPost != null){
                            fragmentManager.beginTransaction().hide(fragmentNewPost).commit();
                        }
                        if(fragmentMy != null){
                            fragmentManager.beginTransaction().hide(fragmentMy).commit();
                        }
                        return true;
                    default: return false;

                }
            }
        });
    }

    public void replaceFragment(){
        fragmentManager.beginTransaction().remove(fragmentNewPost).commit();
        fragmentNewPost = null;
        if(fragmentMain != null){
            fragmentManager.beginTransaction().show(fragmentMain).commit();
        }
        if(fragmentSub != null){
            fragmentManager.beginTransaction().hide(fragmentSub).commit();
        }
        if(fragmentNewPost != null){
            fragmentManager.beginTransaction().hide(fragmentNewPost).commit();
        }
        if(fragmentMy != null){
            fragmentManager.beginTransaction().hide(fragmentMy).commit();
        }
        if(fragmentContentMain != null){
            fragmentManager.beginTransaction().hide(fragmentContentMain).commit();
        }
    }


    public static boolean isValidPassword(String password) {
        boolean err = false;
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*\\W)(?=\\S+$).{8,20}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(password);
        if (m.matches()) {
            err = true;
        }
        return err;
    }

    private void myStartActivity2(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void checkPassword() {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    //Log.d("@@@", FirebaseAuth.getInstance().getCurrentUser().getUid()+"");
                    if (document != null) {
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            password = document.getData().get("password").toString();
                            toolbarNickName.setText(document.getData().get("nickName").toString() + " 님");
                            if(document.getData().get("profileImg").toString().length() > 0 ){
                                setImg();
                            }
                            Log.d("###1", password);
                            if (isValidPassword(password)) {
                                setFirst();
                            } else {
                                myStartActivity(SetPasswordActivity.class);
                                finish();
                            }
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void setImg() {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        storageRef.child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "_profileImage.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                String profileImg = uri.toString();
//                while(profileImg.length() == 0){
//                    continue;
//                }
                //Log.e("@@@!", profileImg);
                setProfileImg(profileImg);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    private void setProfileImg(String profileImg) {
        Glide.with(this).load(profileImg).centerCrop().override(500).into(genter_icon);
    }

    private long backKeyPressedTime = 0;
    private Toast toast;

    public void onBackPressed(){
        //super.onBackPressed();

        if(drawerLayout.isDrawerOpen(drawerView)){
            drawerLayout.closeDrawer(drawerView);
        } else {
            // 마지막으로 뒤로가기 버튼을 눌렀던 시간에 2초를 더해 현재시간과 비교 후
            // 마지막으로 뒤로가기 버튼을 눌렀던 시간이 2초가 지났으면 Toast Show
            // 2000 milliseconds = 2 seconds
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                toast = Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            // 마지막으로 뒤로가기 버튼을 눌렀던 시간에 2초를 더해 현재시간과 비교 후
            // 마지막으로 뒤로가기 버튼을 눌렀던 시간이 2초가 지나지 않았으면 종료
            // 현재 표시된 Toast 취소
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
                toast.cancel();
            }
        }
    }
}