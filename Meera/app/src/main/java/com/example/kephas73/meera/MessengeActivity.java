package com.example.kephas73.meera;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.kephas73.meera.Adapter.MesssgerAdapter;
import com.example.kephas73.meera.Fragment.ChatsFragment;
import com.example.kephas73.meera.Fragment.ProfileFragment;
import com.example.kephas73.meera.Fragment.UsersFragment;
import com.example.kephas73.meera.Model.Chats;
import com.example.kephas73.meera.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessengeActivity extends AppCompatActivity {

    private CircleImageView mProfileImage;
    private TextView mUserName;
    private TabLayout tabLayout;
    private EditText mSend;
    private ImageButton btnSend;
    private Intent intent;
    private RecyclerView recyclerView;
    private MesssgerAdapter messsgerAdapter;
    private ArrayList<Chats> mChats;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenge);
        Mapping();

        // Lấy dữ liệu người nhân
        final String reciver = intent.getStringExtra(Database.TABLE_USER_ID);
        final String imageURLReciver = intent.getStringExtra(Database.TABLE_USER_IMAGE);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // Dữ liệu người gửi
        final String sender = firebaseUser.getUid();

        // Đổ dữ liệu vào
        ShowProfileReciver(reciver);
       // ReadListMessage(sender,reciver, imageURLReciver);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = mSend.getText().toString();
                if (CheckSend(msg) ==true) {
                    Send(reciver, sender, msg);
                    mSend.setText("");

                }
            }
        });
    }

    // Ánh xạ
    private void Mapping() {

        mProfileImage = findViewById(R.id.profileImage);
        mUserName = findViewById(R.id.userName);
        mSend = findViewById(R.id.txtSend);
        btnSend = findViewById(R.id.btnSend);
        intent = getIntent();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        // Thay đổi hình nền chat theo thời gian
        ChangeBackgroundChats(GetTimeNow());
        //--------------------------------------
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);
        android.support.v7.widget.Toolbar toolBar = findViewById(R.id.toolBal);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Lấy giờ hiện tại.
    private int GetTimeNow(){
        int flag = 0;
        Calendar calendar = Calendar.getInstance();
        int time = calendar.getTime().getHours();
        if ( 2 < time  && time < 5) {
            // Thời gian khuya3: night_2
            flag = 1;
        } else if ( 5 <= time  && time < 7) {
            // Thời gian sáng 1: mornning
            flag = 2;
        } else if ( 7 <= time  && time < 11) {
            // Thời gian sáng 2: morning_1
            flag = 3;
        } else if ( 11 <= time  && time < 14) {
            // Thời gian trưa 1: afternoon
            flag = 4;
        }  else if ( 14 <= time  && time < 17) {
            // Thời gian trưa 2: afternoon_2
            flag = 5;
        } else if ( 17 <= time  && time < 19) {
            // Thời gian trưa 3: afternoon_3
            flag = 6;
        } else if ( 19 <= time && time < 22) {
            // Thời gian khuya2: night
            flag = 7;
        }  else  {
            // Thời gian khuya2: night_1
            flag = 8;
        }
        return flag;
    }

    // Thay đổi hình nền chát
    private void  ChangeBackgroundChats(int timeNow) {
        switch (timeNow) {
            case 1:
                recyclerView.setBackgroundResource(R.drawable.night_2);
                break;
            case 2:
                recyclerView.setBackgroundResource(R.drawable.mornning);
                break;
            case 3:
                recyclerView.setBackgroundResource(R.drawable.mornning_1);
                break;
            case 4:
                recyclerView.setBackgroundResource(R.drawable.afternoon);
                break;
            case 5:
                recyclerView.setBackgroundResource(R.drawable.afternoon_2);
                break;
            case 6:
                recyclerView.setBackgroundResource(R.drawable.afternoon_3);
                break;
            case 7:
                recyclerView.setBackgroundResource(R.drawable.night);
                break;
            case 8:
                recyclerView.setBackgroundResource(R.drawable.night_1);
                break;
            default:
                break;
        }
    }


    private void ShowProfileReciver(final String userId) {
        reference = FirebaseDatabase.getInstance().getReference("User").child(userId);

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
                    Glide.with(MessengeActivity.this).load(user.getImageURL()).into(mProfileImage);
                }

                ReadListMessage(firebaseUser.getUid(),userId, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Send(String reciver, String sender, String msg) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Database.TABLE_CHATS_RECIVER, reciver);
        hashMap.put(Database.TABLE_CHATS_SENDER, sender);
        hashMap.put(Database.TABLE_CHATS_MSSG,msg);

        reference.child(Database.TABLE_CHATS).push().setValue(hashMap);
    }

    private boolean CheckSend(String msg) {
        boolean flag = true;
        if (msg.equals(""))
        {
            Toast.makeText(MessengeActivity.this, Message.ERROR_SEND , Toast.LENGTH_SHORT).show();
            flag = false;
        }
        return flag;
    }

    private void ReadListMessage (final String myUserId, final String userId, final String imageURL) {
        mChats = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Database.TABLE_CHATS);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mChats.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chats chats = new Chats();
                    chats.setMessg(snapshot.child(Database.TABLE_CHATS_MSSG).getValue().toString());
                    chats.setReviver(snapshot.child(Database.TABLE_CHATS_RECIVER).getValue().toString());
                    chats.setSender(snapshot.child(Database.TABLE_CHATS_SENDER).getValue().toString());
                    // Gán giá trị cho Chats
                    if ((chats.getReviver().equals(myUserId) && chats.getSender().equals(userId)) ||
                            (chats.getReviver().equals(userId) && chats.getSender().equals(myUserId))) {
                        mChats.add(chats);
                    }
                }
                // Bỏ từng item vào màn hình
                messsgerAdapter = new MesssgerAdapter(MessengeActivity.this, mChats,imageURL);
                recyclerView.setAdapter(messsgerAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
