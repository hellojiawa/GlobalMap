package com.funsnap.maptest.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

/**
 * TODO
 * version: V1.0 <描述当前版本功能>
 * fileName: com.funsnap.maptest.map.MapAmapImpl
 * author: liuping
 * date: 2018/11/5 14:58
 */
public class MapAmapImpl implements IMap {

    private AMap mMap;
    private TextureMapView mMapView;
    private Marker mUserMarker;
    private Circle mCircleGD;

    @Override
    public void init(Context context, ViewGroup viewGroup) {
        mMapView = new TextureMapView(context);

        mMap = mMapView.getMap();
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(false);//隐藏缩放按钮
        mUiSettings.setScaleControlsEnabled(true);//比例尺
        mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);// 设置地图logo显示在右下方

        mMapView.onCreate(null);
        mMapView.onResume();
        viewGroup.addView(mMapView);
    }

    @Override
    public void updateLocation(double longitude, double latitude, Bitmap bitmap) {
        double[] doubles = GPSUtils.gps84_To_Gcj02(latitude, longitude);
        LatLng latLng = new LatLng(doubles[0], doubles[1]);

        if (mUserMarker == null) {
            CameraPosition cameraPosition = CameraPosition.fromLatLngZoom(latLng, 17);
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            mUserMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap)).zIndex(0));

            mCircleGD = mMap.addCircle(new CircleOptions().center(latLng)
                    .radius(MapConst.RADIUS).strokeColor(MapConst.STROKE_COLOR)
                    .fillColor(MapConst.FILL_COLOR).strokeWidth(MapConst.STROKE_WIDTH));
        } else {
            mUserMarker.setPosition(latLng);
            mCircleGD.setCenter(latLng);

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
}
