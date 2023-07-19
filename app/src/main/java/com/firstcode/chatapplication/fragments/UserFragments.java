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

public class UserFragments extends Fragment {
    private RecyclerView UserRecyclerView;
    private UserAdapter userAdapter;
    private List<Users> usersList;
    public UserFragments() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_fragments, container, false);
        UserRecyclerView = view.findViewById(R.id.Users_RecyclerView);
        UserRecyclerView.setHasFixedSize(true);
        UserRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        usersList = new ArrayList<>();
        ReadUsers();
        return view;
    }

    private void ReadUsers() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("MyUsers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();

                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);

                    assert users != null;
                    assert firebaseUser != null;
                    if(!users.getId().equals(firebaseUser.getUid())){
                        usersList.add(users);
                    }
                    userAdapter = new UserAdapter(getContext(),usersList);
                    UserRecyclerView.setAdapter(userAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}