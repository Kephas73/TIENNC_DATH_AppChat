package com.example.kephas73.meera.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.kephas73.meera.MessengeActivity;
import com.example.kephas73.meera.Model.Chats;
import com.example.kephas73.meera.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.Console;
import java.util.List;

public class MesssgerAdapter extends RecyclerView.Adapter<MesssgerAdapter.ViewHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    private Context mcontext;
    private List<Chats> mChats;
    private  String imageURL;
    FirebaseUser firebaseUser;

    public MesssgerAdapter(Context mcontext, List<Chats> mChast, String imageURL) {
        this.mChats = mChast;
        this.mcontext = mcontext;
        // Vì trong đoạn chat ko có hình
        this.imageURL = imageURL;
    }

    @NonNull
    @Override
    public MesssgerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(mcontext).inflate(R.layout.chat_item_left, parent ,false);
            return new MesssgerAdapter.ViewHolder(view, MSG_TYPE_LEFT);
        } else {
            View view = LayoutInflater.from(mcontext).inflate(R.layout.chat_item_right, parent ,false);
            return new MesssgerAdapter.ViewHolder(view, MSG_TYPE_RIGHT);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MesssgerAdapter.ViewHolder holder, int position) {

        Chats chats = mChats.get(position);
        holder.showMessg.setText(chats.getMessg());
        // Database ko có link hình
        if (imageURL.equals("default")) {
            holder.profileImage.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mcontext).load(imageURL).into(holder.profileImage);
        }

    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView showMessg;
        public ImageView profileImage;

        public ViewHolder(View itemView, int flag) {
            super(itemView);
            if (flag == MSG_TYPE_LEFT) {
                showMessg = itemView.findViewById(R.id.showMessgReciver);
                profileImage = itemView.findViewById(R.id.profileImageReciver);
            } else {
                showMessg = itemView.findViewById(R.id.showMessgSend);
                profileImage = itemView.findViewById(R.id.profileImageSend);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChats.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
