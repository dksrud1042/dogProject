package com.example.dog;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class popup extends FragmentActivity {

    TextView txtText;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.markerpopup);

        //UI 객체생성
        txtText = (TextView)findViewById(R.id.txtText);

    }

    //아니오 버튼 클릭
    public void gpsNo(View v){
        //데이터 전달하기
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
    }
    //네 버튼 클릭
    public void gpsYes(View v){
        //데이터 전달하기
        Intent intent = new Intent();
        Bundle extras = getIntent().getExtras();
        double insert_latt = extras.getDouble("latt");
        double insert_long = extras.getDouble("long");
        setResult(RESULT_OK, intent);

        databaseReference.child("user_latitude").setValue(insert_latt);
        databaseReference.child("user_longitude").setValue(insert_long);

        Toast. makeText( this, "사용자 지정위치 : " + "\n" + "위도 : " + insert_latt +
                "경도 : " + insert_long + "등록 했습니다.", Toast.LENGTH_SHORT ).show();

        finish();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}