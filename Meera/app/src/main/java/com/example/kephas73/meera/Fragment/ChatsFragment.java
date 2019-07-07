package com.example.kephas73.meera.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kephas73.meera.Adapter.UserAdapter;
import com.example.kephas73.meera.Const;
import com.example.kephas73.meera.Database;
import com.example.kephas73.meera.Model.Chats;
import com.example.kephas73.meera.Model.User;
import com.example.kephas73.meera.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatsFragment extends Fragment {

    RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUser;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    // Lưu id của tất cả người chat với mình
    private List<String> usersList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        Mapping(view);
        ShowUsersChat();
        return view;
    }

    // Ánh xạ
    private void Mapping(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewChats);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager( new LinearLayoutManager(getContext()));
    }


    private void ShowUsersChat() {
        usersList = new ArrayList<>();
        // Lấy user hiện tại đang đăng nhập
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // Lấy databse Chat
        reference = FirebaseDatabase.getInstance().getReference(Database.TABLE_CHATS);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren() ) {
                    Chats chats = new Chats();
                    chats.setMessg(snapshot.child(Database.TABLE_CHATS_MSSG).getValue().toString());
                    chats.setReviver(snapshot.child(Database.TABLE_CHATS_RECIVER).getValue().toString());
                    chats.setSender(snapshot.child(Database.TABLE_CHATS_SENDER).getValue().toString());

                    if (chats.getSender().equals(firebaseUser.getUid())) {
                        usersList.add(chats.getReviver());
                    }
                    if (chats.getReviver().equals(firebaseUser.getUid())){
                        usersList.add(chats.getSender());
                    }
                }

                // Hiển thị các  user đã lấy ở trên
                ShowUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void ShowUsers() {
        mUser = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference(Database.TABLE_USER);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = new User();
                    user.setUserId(snapshot.child(Database.TABLE_USER_ID).getValue().toString());
                    user.setUserName(snapshot.child(Database.TABLE_USER_NAME).getValue().toString());
                    user.setImageURL(snapshot.child(Database.TABLE_USER_IMAGE).getValue().toString());
                    user.setStatus(snapshot.child(Database.TABLE_USER_STATUS).getValue().toString());

                    for (String id : usersList) {
                        if (user.getUserId().equals(id)) {
                            // Tránh trường hợp một user thêm nhiều lần.
                            if (mUser.size() != 0) {
                                // Tránh trường hợp một user thêm nhiều lần.
                                int flag = 0;
                                for (int i = 0; i < mUser.size(); i++)
                                {
                                    if (mUser.get(i).getUserId() == user.getUserId()) {
                                        flag ++;
                                    }
                                }
                                if (flag == 0){
                                    mUser.add(user);
                                }

                            } else {
                                mUser.add(user);
                            }
                        }
                    }

                    userAdapter = new UserAdapter(getContext(), mUser, true);
                    recyclerView.setAdapter(userAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        }
}
