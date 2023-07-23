package com.firstcode.chatapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Login_Activity extends AppCompatActivity {
    private View decorView; //for full Screen
    TextInputEditText Email, password;
    Button Register,SignIn;
    //FireBase
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    //We Made a On Start Method Kindly Check Below
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Email = findViewById(R.id.Login_Email_Edittext);
        password = findViewById(R.id.Login_Password_Edittext);
        Register = findViewById(R.id.Login_Register_Button);
        SignIn = findViewById(R.id.Login_SignIn_Button);

        // for full Screen
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if(visibility == 0)
                decorView.setSystemUiVisibility(HideSystemBar());
        });

        //Firebase Authentication
        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //checking for Users existence
        if(firebaseUser != null){
            Intent i = new Intent(Login_Activity.this,MainActivity.class);
            startActivity(i);
            finish();
        }

        //Registration activity
        Register.setOnClickListener(view -> {
            Intent i = new Intent(Login_Activity.this,Registration_Activity.class);
            startActivity(i);
        });

        //Login Button Functionality
        SignIn.setOnClickListener(view -> {
            String Username = Objects.requireNonNull(Email.getText()).toString();
            String Password = Objects.requireNonNull(password.getText()).toString();

            //checking if it is empty
            if(TextUtils.isEmpty(Username) || TextUtils.isEmpty(Password)){
                Toast.makeText(Login_Activity.this, "Please Fill All The Fields", Toast.LENGTH_SHORT).show();
            }else {
                auth.signInWithEmailAndPassword(Username,Password)
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                Intent i = new Intent(Login_Activity.this,MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();
                            }else {
                                Toast.makeText(Login_Activity.this, "Login Failed Miserably", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //checking for users existence: Saving the current User
        if(firebaseUser != null){
            Intent i = new Intent(Login_Activity.this,MainActivity.class);
            startActivity(i);
            finish();
        }
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