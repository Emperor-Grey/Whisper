package com.firstcode.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Registration_Activity extends AppCompatActivity {
    private View decorView; //for full Screen
    TextView Welcome;
    TextInputEditText userName,Password,Email;
    Button Register;
    //FireBase Authentication
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Initializing the variables
        Welcome = findViewById(R.id.Welcome_TextView);
        userName = findViewById(R.id.UserName_EditText);
        Password = findViewById(R.id.Password_EditText);
        Email = findViewById(R.id.Email_EditText);
        Register = findViewById(R.id.Register_Button);

        // for full Screen
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if(visibility == 0)
                decorView.setSystemUiVisibility(HideSystemBar());
        });

        //Authentication
        firebaseAuth = FirebaseAuth.getInstance();
        //Adding Event Listener To The Button Register
        Register.setOnClickListener(view -> {
            //Getting the things from the Edittext
            String username = userName.getText().toString();
            String email = Email.getText().toString();
            String password = Password.getText().toString();

            if(TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                Toast.makeText(Registration_Activity.this, "Kindly Fill All The Fields", Toast.LENGTH_SHORT).show();
            }else{
                RegisterNow(username,email,password);
            }
        });
    }
    private void RegisterNow(final String Username,String Email,String Password){
        firebaseAuth.createUserWithEmailAndPassword(Email,Password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Registration_Activity.this, "Registration Completed", Toast.LENGTH_SHORT).show();
                        /*  Intent i = new Intent(Registration_Activity.this,MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();*/
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            assert user != null;
                            String userId = user.getUid();

                            databaseReference = FirebaseDatabase.getInstance()
                                    .getReference("MyUsers")
                                    .child(userId);

                            HashMap<String,String> hashMap = new HashMap<>();
                            hashMap.put("id",userId);
                            hashMap.put("username",Username);
                            hashMap.put("imageUrl","default");

                            //opening the Main Activity After Successful Registration
                            databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                     Intent i = new Intent(Registration_Activity.this,MainActivity.class);
                                     i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
                                     startActivity(i);
                                     finish();
                                    }else{
                                        Toast.makeText(Registration_Activity.this, "Invalid Credentials !!!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            decorView.setSystemUiVisibility(HideSystemBar());
        }
    }
    //since i can't write everytime let me just put that shit in a method
    private int HideSystemBar(){

        return    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
    }
}