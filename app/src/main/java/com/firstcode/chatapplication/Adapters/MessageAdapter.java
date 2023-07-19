package com.firstcode.chatapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firstcode.chatapplication.Model.Chat;
import com.firstcode.chatapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private final Context context;
    private final List<Chat> chats;
    private final String imageUrl;
    // Firebase
    FirebaseUser firebaseUser;

    public static final  int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT  = 1;

    public MessageAdapter(Context context, List<Chat> chats, String imageUrl) {
        this.context = context;
        this.chats = chats;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == MSG_TYPE_RIGHT){
            view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
        }else {
            view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Chat chat = chats.get(position);

        holder.message.setText(chat.getMessage());
        if(imageUrl.equals("default")){
            holder.profileImage.setImageResource(R.mipmap.ic_launcher_round);
        }else {
            Glide.with(context).load(imageUrl).into(holder.profileImage);
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //hey if the messages have id similar to the my id then put it to the right
        // else put it to the left
        if(chats.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView message;
        private final ImageView profileImage;
        private TextView messageSeen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.Message);
            profileImage = itemView.findViewById(R.id.Profile_Image);
            messageSeen = itemView.findViewById(R.id.Message_Seen_Status);
        }
    }
}
