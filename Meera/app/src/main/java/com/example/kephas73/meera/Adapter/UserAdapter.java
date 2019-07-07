package com.example.kephas73.meera.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.kephas73.meera.Const;
import com.example.kephas73.meera.Database;
import com.example.kephas73.meera.MessengeActivity;
import com.example.kephas73.meera.Model.User;
import com.example.kephas73.meera.R;

import java.util.List;

// Set từng item để bỏ vào fragment user
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mcontext;
    private List<User>  mUsers;
    private boolean isChat;

    public UserAdapter(Context mcontext, List<User> mUsers, boolean isChat) {
        this.mUsers = mUsers;
        this.mcontext = mcontext;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.user_item, parent ,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final User user = mUsers.get(position);
        holder.userName.setText(user.getUserName());
        if (user.getImageURL().equals("default")) {
            holder.profileImage.setImageResource(R.drawable.ic_action_default);
        } else {
            Glide.with(mcontext).load(user.getImageURL()).into(holder.profileImage);
        }

        if (isChat) {
            if (user.getStatus().equals(Const.STATUS_ONLINE)) {
                holder.imageOnline.setVisibility(View.VISIBLE);
                holder.imageOffline.setVisibility(View.GONE);
            } else {
                holder.imageOnline.setVisibility(View.GONE);
                holder.imageOffline.setVisibility(View.VISIBLE);
            }
        } else {
            holder.imageOnline.setVisibility(View.GONE);
            holder.imageOffline.setVisibility(View.GONE);
        }

        holder.addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mcontext, " Xin chào", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, MessengeActivity.class);
                intent.putExtra(Database.TABLE_USER_ID, user.getUserId());
                intent.putExtra(Database.TABLE_USER_IMAGE, user.getImageURL());
                mcontext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView userName;
        public ImageView profileImage;
        public ImageView imageOnline;
        public ImageView imageOffline;
        public ImageView addFriend;

        public ViewHolder(View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName);
            profileImage = itemView.findViewById(R.id.profileImage);
            imageOnline = itemView.findViewById(R.id.imageOn);
            imageOffline = itemView.findViewById(R.id.imageOff);
            addFriend = itemView.findViewById(R.id.addFriend);

        }
    }
}
