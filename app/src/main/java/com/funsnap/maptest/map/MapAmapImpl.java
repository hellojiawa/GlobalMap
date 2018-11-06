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
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.funsnap.maptest.R;

import java.util.ArrayList;

/**
 * TODO
 * version: V1.0 <描述当前版本功能>
 * fileName: com.funsnap.maptest.map.MapAmapImpl
 * author: liuping
 * date: 2018/11/5 14:58
 */
public class MapAmapImpl implements IMap {

    private Context mContext;
    private AMap mMap;
    private TextureMapView mMapView;
    private Marker mUserMarker;
    private Circle mCircle;
    private Paint mPaint;
    private Polyline mPolyline;

    @Override
    public void init(Context context, ViewGroup viewGroup) {
        mContext = context;
        mMapView = new TextureMapView(context);

        mPaint = new Paint();
        mPaint.setTextSize(32);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextAlign(Paint.Align.CENTER);

        mMap = mMapView.getMap();
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(false);//隐藏缩放按钮
        mUiSettings.setScaleControlsEnabled(true);//比例尺
        mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);// 设置地图logo显示在左下方
//        mUiSettings.setCompassEnabled(true);//指南针

        mMapView.onCreate(null);
        mMapView.onResume();
        viewGroup.addView(mMapView);

        //点击地图
        mMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });

        //点击marker
        mMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });
    }

    @Override
    public void updateLocation(double longitude, double latitude, Bitmap bitmap) {
        double[] doubles = GPSUtils.gps84_To_Gcj02(latitude, longitude);
        LatLng latLng = new LatLng(doubles[0], doubles[1]);

        if (mUserMarker == null) {
            CameraPosition cameraPosition = CameraPosition.fromLatLngZoom(latLng, 17);
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            mUserMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap)).zIndex(USER_MARKER_INDEX));

            mCircle = mMap.addCircle(new CircleOptions().center(latLng)
                    .radius(MapConst.RADIUS).strokeColor(MapConst.STROKE_COLOR)
                    .fillColor(MapConst.FILL_COLOR).strokeWidth(MapConst.STROKE_WIDTH));
        } else {
            mUserMarker.setPosition(latLng);
            mCircle.setCenter(latLng);

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(latLng, mMap.getCameraPosition().zoom)));
        }
    }

    @Override
    public void addMarkers(PathMeasure pathMeasure) {
        //添加marker
        for (int i = 0;i < pathMeasure.getLength();i += 20){
            pathMeasure.getPosTan(i, pos, tan);
            Point point = new Point((int)pos[0], (int) pos[1]);
            MarkerBean markerBean = new MarkerBean();
            markerBean.mLatLngGD = mMap.getProjection().fromScreenLocation(point);

            if (mCircle != null && mCircle.contains(markerBean.mLatLngGD)){
                if (mMarkers.size() != 0) {
                    double distance = GPSUtils.getDistance(mMarkers.get(mMarkers.size() - 1).mLatLngGD, markerBean.mLatLngGD);
                    if (distance < MapConst.MARKER_DISTANCE_MIN) continue;    //30米内就不添加
                }

                mMarkers.add(markerBean);
                Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.round).copy(Bitmap.Config.ARGB_8888, true);
                Canvas canvas = new Canvas(bitmap);
                canvas.drawText(String.valueOf(mMarkers.size()), bitmap.getWidth() / 2, bitmap.getHeight() / 2, mPaint);

                mMap.addMarker(new MarkerOptions().position(markerBean.mLatLngGD).visible(true).
                        icon(BitmapDescriptorFactory.fromBitmap(bitmap)).zIndex(mMarkers.size()));
            }
        }

        //添加线
        ArrayList<LatLng> lngs = new ArrayList<>();
        for (MarkerBean bean : mMarkers) {
            lngs.add(bean.mLatLngGD);
        }

        if (mPolyline == null) {
            mPolyline = mMap.addPolyline(new PolylineOptions().width(16).color(Color.parseColor("#4ed3b8")).addAll(lngs));
        } else {
            mPolyline.setOptions(new PolylineOptions().width(16).color(Color.parseColor("#4ed3b8")).addAll(lngs));
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
