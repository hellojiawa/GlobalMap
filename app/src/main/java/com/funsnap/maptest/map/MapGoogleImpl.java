package com.funsnap.maptest.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.view.ViewGroup;

import com.funsnap.maptest.R;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * TODO
 * version: V1.0 <描述当前版本功能>
 * fileName: com.funsnap.maptest.map.MapGoogleImpl
 * author: liuping
 * date: 2018/11/5 14:59
 */
public class MapGoogleImpl implements IMap, OnMapReadyCallback {

    private Context mContext;
    private MapView mMapView;
    private GoogleMap mMap;
    Marker mUserMarker;
    private Circle mCircle;
    private Paint mPaint;
    private Polyline mPolyline;

    @Override
    public void init(Context context, ViewGroup viewGroup) {
        mContext = context;
        mMapView = new MapView(context);

        mPaint = new Paint();
        mPaint.setTextSize(32);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextAlign(Paint.Align.CENTER);

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
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap)).zIndex(USER_MARKER_INDEX));

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
    public void addMarkers(PathMeasure pathMeasure) {
        //添加marker
        for (int i = 0;i < pathMeasure.getLength();i += 20){
            pathMeasure.getPosTan(i, pos, tan);
            Point point = new Point((int)pos[0], (int) pos[1]);
            MarkerBean markerBean = new MarkerBean();
            markerBean.mLatLngGoogle = mMap.getProjection().fromScreenLocation(point);

            boolean contains = GPSUtils.getDistance(markerBean.mLatLngGoogle, mCircle.getCenter()) < MapConst.RADIUS;
            if (contains){
                if (mMarkers.size() != 0) {
                    double distance = GPSUtils.getDistance(mMarkers.get(mMarkers.size() - 1).mLatLngGoogle, markerBean.mLatLngGoogle);
                    if (distance < MapConst.MARKER_DISTANCE_MIN) continue;    //30米内就不添加
                }

                mMarkers.add(markerBean);
                Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.round).copy(Bitmap.Config.ARGB_8888, true);
                Canvas canvas = new Canvas(bitmap);
                canvas.drawText(String.valueOf(mMarkers.size()), bitmap.getWidth() / 2, bitmap.getHeight() / 2, mPaint);

                mMap.addMarker(new MarkerOptions().position(markerBean.mLatLngGoogle).visible(true)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap)).zIndex(mMarkers.size()));
            }
        }

        //添加线
        ArrayList<LatLng> lngs = new ArrayList<>();
        for (MarkerBean bean : mMarkers) {
            lngs.add(bean.mLatLngGoogle);
        }

        if (mPolyline == null) {
            mPolyline = mMap.addPolyline(new PolylineOptions().width(16).color(Color.parseColor("#4ed3b8")).addAll(lngs));
        } else {
            mPolyline.setPoints(lngs);
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


        //点击地图
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });

        //点击marker
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });
    }
}
