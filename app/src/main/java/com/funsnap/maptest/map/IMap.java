package com.funsnap.maptest.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PathMeasure;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * TODO
 * version: V1.0 <描述当前版本功能>
 * fileName: com.funsnap.maptest.map.IMap
 * author: liuping
 * date: 2018/11/5 15:10
 */
public interface IMap {
    int USER_MARKER_INDEX = 100;

    ArrayList<MarkerBean> mMarkers = new ArrayList<>(); //航点数据

    float[] pos = new float[2];
    float[] tan = new float[2];


    /**
     * 初始化
     *
     * @param context
     * @param viewGroup
     */
    void init(Context context, ViewGroup viewGroup);

    /**
     * 更新用户位置
     *
     * @param longitude
     * @param latitude
     * @param bitmap
     */
    void updateLocation(double longitude, double latitude, Bitmap bitmap);

    /**
     * 添加航点数据
     *
     */
    void addMarkers(PathMeasure pathMeasure);

    void onPause();

    void onDestroy();
}
