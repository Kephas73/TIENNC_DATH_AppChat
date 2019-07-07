package com.example.kephas73.meera;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kephas73.meera.Fragment.ChatsFragment;
import com.example.kephas73.meera.Fragment.ProfileFragment;
import com.example.kephas73.meera.Fragment.UsersFragment;
import com.example.kephas73.meera.Message;
import com.example.kephas73.meera.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    CircleImageView mProfileImage;
    TextView mUserName;
    TabLayout tabLayout;
    ViewPager viewPager;

    FirebaseUser firebaseUser;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Mapping();
        ShowProfileUser();
    }

    private void Mapping() {
        mProfileImage = findViewById(R.id.profileImage);
        mUserName = findViewById(R.id.userName);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        android.support.v7.widget.Toolbar toolBar = findViewById(R.id.toolBal);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("");
    }

    private void ShowProfileUser() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mUserName.setText(user.getUserName());
                if (user.getImageURL().equals("default")) {
                    // Set hình ảnh có sẵn trong máy tính cửa bạn
                    mProfileImage.setImageResource(R.drawable.ic_action_default);
                } else {
                    // Set hình ảnh từ website khá
                    Glide.with(MainActivity.this).load(user.getImageURL()).into(mProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new ChatsFragment(), "Chat");
        viewPagerAdapter.addFragment(new UsersFragment(), "Suggestion");
        viewPagerAdapter.addFragment(new ProfileFragment(), "Profile");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.menu, menu);
       return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity( new Intent(MainActivity.this, StartActivity.class));
                finish();
                return true;
        }

        return false;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

    }

    // Cập nhật trạng thái hoạt động
    private void SetStatus(String status) {
        // Lấy data base của tài khoản đang đăng nhập hiện tại
        reference = FirebaseDatabase.getInstance().getReference(Database.TABLE_USER).child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Database.TABLE_USER_STATUS, status);

        reference.updateChildren(hashMap);
    }

    // Khi đang ở màn hình app hiện tại. Thì hiện on line

    @Override
    protected void onResume() {
        super.onResume();
        SetStatus(Const.STATUS_ONLINE);
    }
    // Khi không ở màn hình app hiện tại. Thì hiện offline
    @Override
    protected void onPause() {
        super.onPause();
        SetStatus(Const.STATUS_OFFLINE);
    }

}
