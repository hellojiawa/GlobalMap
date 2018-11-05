package com.funsnap.maptest.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.amap.api.maps.CoordinateConverter;
import com.funsnap.maptest.R;
import com.funsnap.maptest.location.GPSLocationListener;
import com.funsnap.maptest.location.GPSLocationManager;
import com.funsnap.maptest.map.IMap;
import com.funsnap.maptest.map.MapAmapImpl;
import com.funsnap.maptest.map.MapGoogleImpl;

public class MainActivity extends AppCompatActivity {

    private FrameLayout mMapContainer;
    IMap mIMap;
    private Bitmap mBitmapUser;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapContainer = findViewById(R.id.map_container);
        mProgressBar = findViewById(R.id.progress_bar);

        mBitmapUser = BitmapFactory.decodeResource(getResources(), R.drawable.current_location).copy(Bitmap.Config.ARGB_8888, true);

        //手机自带定位
        GPSLocationManager.getInstances(this).start(new LocationListener());
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mIMap != null) mIMap.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mIMap != null) mIMap.onDestroy();
        GPSLocationManager.getInstances(this).stop();
    }

    //GPS位置改变后回调
    class LocationListener implements GPSLocationListener {

        private boolean firstLoad = true;

        @Override
        public void UpdateLocation(Location location) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();

            if (firstLoad) {
                firstLoad = false;
                boolean available = CoordinateConverter.isAMapDataAvailable(latitude, longitude);
                if (available) {
                    mIMap = new MapAmapImpl();
                } else {
                    mIMap = new MapGoogleImpl();
                }
                mIMap.init(MainActivity.this, mMapContainer);
                mProgressBar.setVisibility(View.GONE);
            }

            mIMap.updateLocation(longitude, latitude, mBitmapUser);
        }

        @Override
        public void UpdateStatus(String provider, int status, Bundle extras) {

        }

        @Override
        public void UpdateGPSProviderStatus(int gpsStatus) {

        }
    }
}
