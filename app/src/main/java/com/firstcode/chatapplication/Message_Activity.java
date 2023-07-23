package com.firstcode.chatapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firstcode.chatapplication.Adapters.MessageAdapter;
import com.firstcode.chatapplication.Model.Chat;
import com.firstcode.chatapplication.Model.Users;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Message_Activity extends AppCompatActivity {
    private TextView userName;
    private CircleImageView userImage;

    private MessageAdapter messageAdapter;
    private RecyclerView messageRecyclerView;
    private List<Chat> chatsList;

    private TextInputEditText messageText;
    private String userId;

    //firebase
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    ValueEventListener SeenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // Initializing Widgets
        userName = findViewById(R.id.Toolbar_user_TextView);
        userImage = findViewById(R.id.Toolbar_User_imageView);
        ImageButton sentButton = findViewById(R.id.Message_Button_Send);
        messageText = findViewById(R.id.Message_Text_Send);

        //Recycler View
        messageRecyclerView = findViewById(R.id.Message_recyclerView);
        messageRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        messageRecyclerView.setLayoutManager(linearLayoutManager);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.Toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        //firebase
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("MyUsers").child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                assert users != null;
                userName.setText(users.getUsername());

                if (isActivityValid()) { // Check if the activity is still valid before using Glide
                    if (users.getImageUrl().equals("default")) {
                        userImage.setImageResource(R.mipmap.ic_launcher_round);
                    } else {
                        Glide.with(Message_Activity.this).load(users.getImageUrl()).into(userImage);
                    }
                }
                ReadMessages(firebaseUser.getUid(), userId, users.getImageUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sentButton.setOnClickListener(view -> {
            String message = Objects.requireNonNull(messageText.getText()).toString();
            if (!message.equals("")) {
                SendMessage(firebaseUser.getUid(), userId, message);
            } else {
                Toast.makeText(Message_Activity.this, "Type Something User!!!", Toast.LENGTH_SHORT).show();
            }
            messageText.setText("");
        });
        SeenMessage(userId);
    }

    private boolean isActivityValid() {
        return !isFinishing() && !isDestroyed();
    }

    private void SeenMessage(final String userid){
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        SeenListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    assert chat != null;
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ){

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendMessage(String sender, String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isSeen", false);

        reference.child("Chats").push().setValue(hashMap);

        //adding user to chat fragment: latest Chats with Contacts
        final DatabaseReference chatReference = FirebaseDatabase.getInstance()
                .getReference("ChatList")
                .child(firebaseUser.getUid())
                .child(userId);

        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    chatReference.child("id").setValue(userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ReadMessages(String myId, String userId, String imageUrl) {
        chatsList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                chatsList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    assert chat != null;
                    if (chat.getReceiver().equals(myId) && chat.getSender().equals(userId) ||
                            chat.getReceiver().equals(userId) && chat.getSender().equals(myId)) {
                        chatsList.add(chat);
                    }
                }

                messageAdapter = new MessageAdapter(Message_Activity.this, chatsList, imageUrl);
                messageRecyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void CheckStatus(String status) {
        databaseReference = FirebaseDatabase.getInstance().getReference("MyUsers")
                .child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        databaseReference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckStatus("Online"); // Set user status to "Online" when the app is opened
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (SeenListener != null) {
            databaseReference.removeEventListener(SeenListener);
        }
        CheckStatus("Offline");
    }
}