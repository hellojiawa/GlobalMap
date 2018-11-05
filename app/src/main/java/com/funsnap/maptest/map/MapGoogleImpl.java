package com.funsnap.maptest.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * TODO
 * version: V1.0 <描述当前版本功能>
 * fileName: com.funsnap.maptest.map.MapGoogleImpl
 * author: liuping
 * date: 2018/11/5 14:59
 */
public class MapGoogleImpl implements IMap, OnMapReadyCallback {

    private MapView mMapView;
    private GoogleMap mMap;
    Marker mUserMarker;
    private Circle mCircle;

    @Override
    public void init(Context context, ViewGroup viewGroup) {
        mMapView = new MapView(context);

        mMapView.onCreate(null);
        mMapView.onResume();
        viewGroup.addView(mMapView);

        mMapView.getMapAsync(this);
    }

    @Override
    public void updateLocation(double longitude, double latitude, Bitmap bitmap) {
        if (mMap == null) return;

        LatLng latLng = new LatLng(latitude, longitude);
        
        if (mUserMarker == null) {
            CameraPosition position = CameraPosition.fromLatLngZoom(latLng, 17);
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));

            mUserMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap)).zIndex(0));

            mCircle = mMap.addCircle(new CircleOptions().center(latLng).
                    radius(MapConst.RADIUS).strokeColor(MapConst.STROKE_COLOR).fillColor(MapConst.FILL_COLOR).strokeWidth(MapConst.STROKE_WIDTH));
        } else {
            //谷歌地图再次定位
            mUserMarker.setPosition(latLng);
            mCircle.setCenter(latLng);

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(latLng,mMap.getCameraPosition().zoom)));
        }
    }

    @Override
    public void onPause() {
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setIndoorLevelPickerEnabled(true);

    }
}
