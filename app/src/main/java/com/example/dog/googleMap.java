package com.example.dog;

import android.app.FragmentManager;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.TimerTask;
import java.util.Timer;

public class googleMap extends AppCompatActivity implements OnMapReadyCallback {
    public FragmentManager fragmentManager;
    public MapFragment mapFragment;
    private GoogleMap mMap;
    private DatabaseReference mUsers;
    private FirebaseDatabase database;
    public double popup_latitude;
    public double popup_longitude;
    public double send_distance;
    public boolean markerpoint = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.googlemap);
        database = FirebaseDatabase.getInstance();
        fragmentManager = getFragmentManager();
        mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mUsers = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;
        googleMap.setOnInfoWindowClickListener(infoWindowClickListener);

        Timer timer = new Timer();
        timer.schedule(tt, 1000, 10000);

    }
    //정보창 클릭 리스너
    GoogleMap.OnInfoWindowClickListener infoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            Intent intent = new Intent(getBaseContext(), popup.class);
            intent.putExtra("latt", popup_latitude);
            intent.putExtra("long", popup_longitude);
            startActivity(intent);
        }
    };

    TimerTask tt = new TimerTask(){
        @Override
        public void run(){
            getGPS();
            GPSservice();
        }
    };

    private void getGPS(){

        mUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ignored : dataSnapshot.getChildren()) {
                    UserInformation user = dataSnapshot.getValue(UserInformation.class);
                    assert user != null;

                    LatLng NOW = new LatLng(user.latitude, user.longitude);

                    if (markerpoint == true) {
                        mMap.clear();
                        markerpoint = false;
                    }
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(NOW);
                    String markerTitle = getCurrentAddress(NOW);
                    markerOptions.title(markerTitle);
                    markerOptions.snippet("위도 : " + Double.toString(user.latitude) + ", "
                            + "경도 : " + Double.toString(user.longitude));
                    mMap.addMarker(markerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(NOW));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                    popup_latitude = user.latitude;
                    popup_longitude = user.longitude;

                    user.beforelat = user.latitude;
                    user.beforelon = user.longitude;

                    send_distance = DistanceByDegreeAndroid(user.user_latitude, user.user_longitude, user.latitude, user.longitude);

                    markerpoint = true;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void GPSservice(){
        Intent intent = new Intent(this, MyService.class); // 실행시키고픈 서비스클래스 이름
        // intent.putExtra("", ""); 필요시 인텐트에 필요한 데이터를 담아준다

        if(send_distance > 200){
            intent.putExtra(MyService.MESSAGE_KEY, true);
        }else{
            intent.putExtra(MyService.MESSAGE_KEY, false);
        }
        intent.putExtra(String.valueOf(MyService.Service_distance), send_distance);
        startService(intent); // 서비스 실행!
    }


    public double DistanceByDegreeAndroid(double _latitude1, double _longitude1, double _latitude2, double _longitude2){
        Location startPos = new Location("PointA");
        Location endPos = new Location("PointB");

        startPos.setLatitude(_latitude1);
        startPos.setLongitude(_longitude1);
        endPos.setLatitude(_latitude2);
        endPos.setLongitude(_longitude2);

        double distance = startPos.distanceTo(endPos);

        return distance;
    }

    String getCurrentAddress(LatLng latlng) {
        // 위치 정보와 지역으로부터 주소 문자열을 구한다.
        List<Address> addressList = null ;
        Geocoder geocoder = new Geocoder( this, Locale.getDefault());

        // 지오코더를 이용하여 주소 리스트를 구한다.
        try {
            addressList = geocoder.getFromLocation(latlng.latitude,latlng.longitude,1);
        } catch (IOException e) {
            Toast. makeText( this, "위치로부터 주소를 인식할 수 없습니다. 네트워크가 연결되어 있는지 확인해 주세요.", Toast.LENGTH_SHORT ).show();
            e.printStackTrace();
            return "주소 인식 불가" ;
        }

        if (addressList.size() < 1) { // 주소 리스트가 비어있는지 비어 있으면
            return "해당 위치에 주소 없음" ;
        }

        // 주소를 담는 문자열을 생성하고 리턴
        Address address = addressList.get(0);
        StringBuilder addressStringBuilder = new StringBuilder();
        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
            addressStringBuilder.append(address.getAddressLine(i));
            if (i < address.getMaxAddressLineIndex())
                addressStringBuilder.append("\n");
        }

        return addressStringBuilder.toString();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent t = new Intent(googleMap.this, MainActivity.class);
        startActivity(t);
        finish();


    }
}