package com.firstcode.chatapplication.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firstcode.chatapplication.Adapters.UserAdapter;
import com.firstcode.chatapplication.Model.ChatList;
import com.firstcode.chatapplication.Model.Users;
import com.firstcode.chatapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private UserAdapter userAdapter;
    private RecyclerView recyclerView;
    private List<Users> usersList;

    //firebase
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    private List<ChatList> chatLists;
    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.ChatFragmentRvView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        chatLists = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatLists.clear();

                //Loop for all users getting them
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ChatList chatList = dataSnapshot.getValue(ChatList.class);
                    chatLists.add(chatList);
                }
                getAllUsersChatted();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }

    private void getAllUsersChatted() {

        //getting all Chatted Users
        usersList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("MyUsers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Users users = dataSnapshot.getValue(Users.class);
                    for (ChatList chatList : chatLists){
                        assert users != null;
                        if(users.getId().equals(chatList.getId())){
                            usersList.add(users);  // Note Important
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext() ,usersList, true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}