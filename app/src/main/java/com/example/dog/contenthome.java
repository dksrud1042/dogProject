package com.example.dog;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class contenthome extends AppCompatActivity {

    private TextView dogname;
    private TextView dogage;
    private TextView humanname;
    private ImageView imageView;
    private DatabaseReference mUsers;
    private FirebaseDatabase database;
    private FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_home);

        mUsers = FirebaseDatabase.getInstance().getReference();
        database = FirebaseDatabase.getInstance();

        imageView = findViewById(R.id.imageView);
        dogname = findViewById(R.id.dogname);
        dogage = findViewById(R.id.dogage);
        humanname = findViewById(R.id.humanname);

    }

    @Override
    protected void onStart() {
        super.onStart();

        mUsers.child("profile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for(DataSnapshot t : dataSnapshot.getChildren()){
                    Userprofile userprofile = t.getValue(Userprofile.class);


                    dogage.setText(userprofile.dogage);
                    dogname.setText(userprofile.dogname);
                    humanname.setText(userprofile.humanname);
                    imageView.setImageURI(Uri.parse(userprofile.imageUrl));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
