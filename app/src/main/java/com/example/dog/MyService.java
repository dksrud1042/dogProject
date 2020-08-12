package com.example.dog;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.Objects;

public class MyService extends Service{

    private static final String TAG = "MyService";
    public static final String MESSAGE_KEY = "";        // GPS
    public static double Service_distance;
    public static double GPSDistance;
    //public static int CoolCount=10;          // 1당 10초

    public MyService(){

    }

    @Override
    public void onCreate(){
        super.onCreate();
        // 서비스는 한번 실행되면 계속 실행된 상태로 있는다.
        // 따라서 서비스 특성상 intent를 받아서 처리하기에 적합하지않다.
        // intent에 대한 처리는 onStartCommand()에서 처리해준다.
        Log.d(TAG, "onCreate() called");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        GPSDistance = Objects.requireNonNull(intent.getExtras()).getDouble(String.valueOf(MyService.Service_distance));

        boolean GPSalarm = Objects.requireNonNull(intent.getExtras()).getBoolean(MyService.MESSAGE_KEY);

        if(GPSalarm){
            Intent mMainIntent = new Intent(this, googleMap.class);
            PendingIntent mPendingIntent = PendingIntent.getActivity(
                    this, 1, mMainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            //CoolCount++;

        //if(CoolCount > 5)         // 1분에 한번 알림
                GPSNotification();

        }else {
        }
        return START_NOT_STICKY;
    }
    public void GPSNotification() {

        //알림(Notification)을 관리하는 관리자 객체를 운영체제(Context)로부터 소환하기
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //Notification 객체를 생성해주는 건축가객체 생성(AlertDialog 와 비슷)
        NotificationCompat.Builder builder = null;

        //Oreo 버전(API26 버전)이상에서는 알림시에 NotificationChannel 이라는 개념이 필수 구성요소가 됨.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channelID = "channel_01"; //알림채널 식별자
            String channelName = "MyChannel01"; //알림채널의 이름(별명)

            //알림채널 객체 만들기
            NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT);

            //알림매니저에게 채널 객체의 생성을 요청
            notificationManager.createNotificationChannel(channel);

            //알림건축가 객체 생성
            builder = new NotificationCompat.Builder(this, channelID);


        } else {
            //알림 건축가 객체 생성
            builder = new NotificationCompat.Builder(this, null);
        }

        //건축가에게 원하는 알림의 설정작업
        builder.setSmallIcon(android.R.drawable.ic_menu_view);

        //상태바를 드래그하여 아래로 내리면 보이는
        //알림창(확장 상태바)의 설정
        builder.setContentTitle("GPS 이상 신호");//알림창 제목
        builder.setContentText("사용자 지정 위치에서 " + (int) GPSDistance + "m 벗어났습니다.");//알림창 내용

        //건축가에게 알림 객체 생성하도록
        Notification notification = builder.build();

        //알림매니저에게 알림(Notify) 요청
        notificationManager.notify(1, notification);

        //알림 요청시에 사용한 번호를 알림제거 할 수 있음.
        //notificationManager.cancel(1);

        //CoolCount = 0;
    }


    @Override
    public void onDestroy(){
        super.onDestroy();

        Log.d(TAG, "onDestroy() called");
    }

    @Override
    public IBinder onBind(Intent intent){
        throw new UnsupportedOperationException("Not yet Implemented"); //자동으로 작성되는 코드
    }
}