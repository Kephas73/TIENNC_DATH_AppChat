package com.example.kephas73.meera.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kephas73.meera.Adapter.UserAdapter;
import com.example.kephas73.meera.Database;
import com.example.kephas73.meera.Model.User;
import com.example.kephas73.meera.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private ArrayList<User> mUsers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        Mapping(view);

        ReadListUsers();
        return view;
    }

    private void  Mapping(View view) {

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager( new LinearLayoutManager(getContext()));
    }

    private void ReadListUsers () {
        mUsers = new ArrayList<>();
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Database.TABLE_USER);

        reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mUsers.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Gán giá trị cho User
                        User user = new User();
                        user.setUserId(snapshot.child(Database.TABLE_USER_ID).getValue().toString());
                        user.setUserName(snapshot.child(Database.TABLE_USER_NAME).getValue().toString());
                        user.setImageURL(snapshot.child(Database.TABLE_USER_IMAGE).getValue().toString());

                        assert  user != null;
                        assert firebaseUser != null;

                        // không hiện user của mình
                        if (!user.getUserId().equals(firebaseUser.getUid())) {
                            mUsers.add(user);
                        }
                    }
                    // Bỏ từng item vào màn hình
                    userAdapter = new UserAdapter(getContext(), mUsers);
                    recyclerView.setAdapter(userAdapter);


                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
