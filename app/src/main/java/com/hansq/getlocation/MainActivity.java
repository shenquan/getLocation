package com.hansq.getlocation;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int SHOW_LOCATION = 0;
    public Toast mToast;
    public Context mContext;
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
    private TextView positionTextview;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_LOCATION:
                    String currentPosition = (String) msg.obj;
                    positionTextview.setText(currentPosition);
                    break;
                default:
                    break;
            }
        }
    };
    private LocationManager locationManager;
    private String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();
        try {
            PackageManager pm = getPackageManager();
            PackageInfo pack = pm.getPackageInfo("com.hansq.getlocation", PackageManager.GET_PERMISSIONS);
            String[] permissionStrings = pack.requestedPermissions;
//            showToast("权限清单--->"+ permissionStrings[0].toString(),Toast.LENGTH_LONG);
            //获取应用的所有的权限，用Log.d不出来
            for (int i = 0; i < permissionStrings.length; i++) {
                Log.d("HSQdebug", permissionStrings[i]);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        positionTextview = (TextView) findViewById(R.id.position_text_view);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            showToast("No location provider to use", Toast.LENGTH_SHORT);
            return;
        }
        //需要改写
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            showLocation(location);
        }
        locationManager.requestLocationUpdates(provider, 5000, 1, locationListener);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    private void showLocation(final Location location) {
       /* String currentPosition = "latitude is " + location.getLatitude() + "\n" +
                "longitude is " + location.getLongitude();
        positionTextview.setText(currentPosition);*/
        //增加经纬度解析为当前城市代码
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    /*StringBuilder urlTemp = new StringBuilder();
                    urlTemp.append("http://maps.googleapis.com/maps/api/geocode/json?latlng=");
                    urlTemp.append(location.getLatitude()).append(",");
                    urlTemp.append(location.getLongitude());
                    urlTemp.append("&sensor=false");*/
//                    String urlTemp="http://maps.googleapis.com/maps/api/geocode/json?latLng=31.318861370714846,121.3854713826456&sensor=false";
//                    URL url = new URL(urlTemp);//出问题在于这个不是一个服务器地址，是一段json数据
                    URL url = new URL("http://www.baidu.com");//经测试这个完全可以
                    Log.i("HSQ-url", url.toString());
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true); //允许输入流，即允许下载
                    connection.setDoOutput(true); //允许输出流，即允许上传
                    connection.setUseCaches(false); //不使用缓冲
                    connection.setRequestMethod("GET");
                    /*connection.setRequestProperty("Content-type", "text/html");
                    connection.setRequestProperty("Accept-Charset", "utf-8");
                    connection.setRequestProperty("contentType", "utf-8");*/
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    Log.i("HSQ_responseCode", "" + connection.getResponseCode());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
                    String response = "";

                    String line=null;

                    while ((line = reader.readLine()) != null) {
                        response+=line;
                    }
                    Log.i("HSQ-response",response);
                    Message message = Message.obtain();
                    message.what = SHOW_LOCATION;
                    message.obj = response.toString();

                    mHandler.sendMessage(message);

                    Looper.prepare();
                    showToast(response.toString(),3000);
                    Looper.loop();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();

    }

    public void showToast(String msg, int time) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, msg, time);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }


}
