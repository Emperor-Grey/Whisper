package com.firstcode.chatapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firstcode.chatapplication.Message_Activity;
import com.firstcode.chatapplication.Model.Users;
import com.firstcode.chatapplication.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.userViewHolder> {
    private final Context context;
    private final List<Users> usersList;
    private final Boolean isChat;

    //Constructor
    public UserAdapter(Context context, List<Users> usersList, Boolean isChat) {
        this.context = context;
        this.usersList = usersList;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public userViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new userViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull userViewHolder holder, int position) {
        Users users = usersList.get(position);
        holder.userName.setText(users.getUsername());

        if(users.getImageUrl().equals("default")){
            holder.userImage.setImageResource(R.mipmap.ic_launcher_round);
        }else {
            //Loading the image from the internet
            Glide.with(context).load(users.getImageUrl()).into(holder.userImage);
        }
        holder.itemView.setOnClickListener(view -> {
            Intent i = new Intent(context, Message_Activity.class);
            i.putExtra("userId",users.getId());
            context.startActivity(i);
        });

        //Status Check
        if (isChat) {
            if(users.getStatus().equals("Online")){
                holder.status.setText("Online");
            }else {
                holder.status.setText("Offline");
            }
        } else {
            holder.status.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }


    public static class userViewHolder extends RecyclerView.ViewHolder{
        private final TextView userName;
        private final CircleImageView userImage;
        private final TextView status;

        public userViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.User_Name);
            userImage = itemView.findViewById(R.id.User_Image);
            status = itemView.findViewById(R.id.Status);
        }
    }
}
