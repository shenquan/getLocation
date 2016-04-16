package com.hansq.getlocation;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView positionTextview;
    private LocationManager locationManager;
    private String provider;
    public  Toast mToast;
    public Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext=getApplicationContext();

        positionTextview = (TextView)findViewById(R.id.position_text_view);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        List<String> providerList = locationManager.getProviders(true);
        if(providerList.contains(LocationManager.GPS_PROVIDER)){
            provider  = LocationManager.GPS_PROVIDER;
        }else if(providerList.contains(LocationManager.NETWORK_PROVIDER)){
            provider = LocationManager.NETWORK_PROVIDER;
        }else {
            showToast("No location provider to use",Toast.LENGTH_SHORT);
            return;
        }
        //需要改写
        Location location = locationManager.getLastKnownLocation(provider);
        if(location!=null){
            showLocation(location);
        }
        locationManager.requestLocationUpdates(provider,5000,1,locationListener);


    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(locationManager!=null){
            locationManager.removeUpdates(locationListener);
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //更新当前设备的位置信息
            showLocation(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private void showLocation(Location location){
        String currentPosition ="latitude is "+ location.getLatitude()+"\n"+
                "longitude is " + location.getLongitude();
        positionTextview.setText(currentPosition);
    }

    public void showToast(String msg,int time){
        if(mToast==null){
            Toast.makeText(mContext,msg,time);
        }else {
            mToast.setText(msg);
        }
        mToast.show();

    }

}
