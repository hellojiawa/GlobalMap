package com.funsnap.maptest.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;

/**
 * TODO
 * version: V1.0 <描述当前版本功能>
 * fileName: com.funsnap.maptest.map.IMap
 * author: liuping
 * date: 2018/11/5 15:10
 */
public interface IMap {

    void init(Context context, ViewGroup viewGroup);

    void updateLocation(double longitude, double latitude, Bitmap bitmap);

    void onPause();

    void onDestroy();

}
