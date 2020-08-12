package com.example.dog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class Information extends AppCompatActivity {
    private static final String TAG = "Information";
    private String imagePath;
    private Button btsave;
    private Button btgallery;
    private ImageView imageView;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference infor;
    private FirebaseStorage storage;
    private EditText dogname;
    private EditText dogage;
    private EditText humanname;
    private static final int GALLERY_CODE = 10;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        humanname = (EditText)findViewById(R.id.humanname);
        dogage = (EditText)findViewById(R.id.dogage);
        dogname = (EditText)findViewById(R.id.dogname);
        btgallery = (Button) findViewById(R.id.button_gallery);
        btsave = (Button) findViewById(R.id.button_save);
        imageView = (ImageView) findViewById(R.id.imageView);

        btgallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, GALLERY_CODE);

            }
        });

        btsave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                uploadFile(imagePath);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE) {

                imagePath = getPath(data.getData());
                File f = new File(imagePath);
                imageView.setImageURI(Uri.fromFile(f));

            }
    }



    public String getPath(Uri uri){

        String [] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this,uri,proj,null,null,null);

        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(index);
    }

    private void uploadFile(String uri) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("업로드중...");
            progressDialog.show();


            Uri file = Uri.fromFile(new File(uri));
            StorageReference storageRef =
                    storage.getReferenceFromUrl("gs://projectdog-ee3fd.appspot.com").child("images/");
            UploadTask uploadTask = storageRef.putFile(file);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> dowmloadUrl = taskSnapshot.getStorage().getDownloadUrl();
                    Userprofile userprofile = new Userprofile();
                    userprofile.dogage = dogage.getText().toString();
                    userprofile.dogname = dogname.getText().toString();
                    userprofile.humanname = humanname.getText().toString();
                    userprofile.imageUrl = dowmloadUrl.toString();

                    database.getReference().child("profile").setValue(userprofile);

                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                    Intent I = new Intent(Information.this, HomeActivity.class);
                    startActivity(I);
                }
            }) .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                }
            }) .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests")
                    double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                }
            });
        }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent t = new Intent(Information.this, MainActivity.class);
        startActivity(t);
        finish();


    }
}
