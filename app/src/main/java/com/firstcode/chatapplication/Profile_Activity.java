package com.firstcode.chatapplication;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.firstcode.chatapplication.Model.Users;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile_Activity extends AppCompatActivity {

    TextView Settings, UserName, UserId;
    CircleImageView ProfileImage;
    Button editProfile, Logout;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    StorageReference storageReference;
    private static final int REQUEST = 1;
    private Uri imageUri;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Settings = findViewById(R.id.Settings_TextView);
        UserName = findViewById(R.id.UserName_TextView);
        UserId = findViewById(R.id.UserId_TextView);
        editProfile = findViewById(R.id.EditProfile_Button);
        ProfileImage = findViewById(R.id.Profile_ImageView);
        Logout = findViewById(R.id.Logout_Button);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("Uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("MyUsers")
                .child(firebaseUser.getUid());

        UserName.setText(firebaseUser.getUid());
        UserId.setText(firebaseUser.getEmail());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                assert users != null;
                UserName.setText(users.getUsername());

                Log.d("Profile_Activity", "Image URL: " + users.getImageUrl());

                if (users.getImageUrl().equals("default")) {
                    ProfileImage.setImageResource(R.mipmap.ic_launcher_round);
                } else {
                    Glide.with(Profile_Activity.this).load(users.getImageUrl()).into(ProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Logout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            navigateToLoginActivity();
        });

        ProfileImage.setOnClickListener(view -> {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(i, REQUEST);
        });
    }

    private String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver = Profile_Activity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    private void upLoadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(Profile_Activity.this);
        progressDialog.setMessage("Uploading");
        progressDialog.show();

        if (imageUri != null) {
            final StorageReference storageReference1 = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            uploadTask = storageReference1.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storageReference1.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        // Use the correct key "imageUrl" instead of "imageURL"
                        databaseReference = FirebaseDatabase.getInstance().getReference("MyUsers")
                                .child(firebaseUser.getUid());

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageUrl", mUri); // Use "imageUrl" instead of "imageURL"
                        databaseReference.updateChildren(map);

                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(Profile_Activity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Profile_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(Profile_Activity.this, "Upload in Progress", Toast.LENGTH_SHORT).show();
            } else {
                upLoadImage();
            }
        }
    }

    // Method to navigate to Login_Activity and clear the back stack
    private void navigateToLoginActivity() {
        Intent intent = new Intent(Profile_Activity.this, Login_Activity.class);
        intent.setFlags((Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)/* & Intent.FLAG_ACTIVITY_CLEAR_TOP */);
        startActivity(intent);
        finish();
    }
}
