package com.example.petdiary.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petdiary.R;
import com.example.petdiary.fragment.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private String password = "";

    TextView toolbarNickName;
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerView = (View) findViewById(R.id.drawerView);
        drawerLayout.setDrawerListener(listener);
        toolbarNickName = findViewById(R.id.toolbar_nickName);

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

    public void blockFriendOnClick(View view){
        startToast("차단친구 메뉴");
    }

    public void noticeOnClick(View view){
        startToast("알림 설정 메뉴");
    }

    public void passwordSetOnClick(View view){
        startToast("비밀번호 변경 메뉴");
    }

    public void customerCenterOnClick(View view){
        startToast("고객센터 메뉴");
    }

    public void unRegisterOnClick(View view){
        startToast("회원탈퇴 메뉴");
    }

    public void AppInfoOnClick(View view){
        startToast("앱 정보 메뉴");
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                drawerLayout.openDrawer(drawerView);
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
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

}