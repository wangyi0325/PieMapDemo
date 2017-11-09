package com.example.admin.piemapdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Toast;


import com.example.admin.piemapdemo.constats.Path;

import pie.core.DimensionMode;
import pie.core.GisNative;
import pie.core.MapView;
import pie.core.Workspace;
import pie.map.MapViews;
import pie.map.gesture.MapGestureController;
/**
 * 地图打打开与关闭功能
 */
public class MainActivity extends AppCompatActivity {

    private MapViews map_views;//获取地图组件
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //初始化控件
        initview();
        //设置数据
        setView();
        //打开地图
        openMap("googlemap");

    }



    private void initview() {
        map_views = (MapViews) findViewById(R.id.map_views);
        mapView = map_views.getMapView();
    }


    private void setView() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int densityDpi = displayMetrics.densityDpi;
        mapView.setDeviceDPI(densityDpi);//设置屏幕分辨率
        mapView.setDimensionMode(DimensionMode.D2DMode);//设置地图打开的维度（二维或三维）

    }


    private void openMap(String name) {
        boolean flag = false;

        if(mapView != null){
            mapView.attachWorkspace(PieMapApiApplication.mWorkspace);//关联工作空间
            flag = mapView.openMap(name);//打开地图
        }

        if(!flag){
            Toast.makeText(this, "打开地图失败", Toast.LENGTH_SHORT).show();
            finish();
        }

        mapView.setPitch(false);// 设置是否可俯仰
        mapView.setRotate(true);// 设置是否可旋转
        // 地图全屏显示
        mapView.viewEntire();
        mapView.setMapGestureController(new MapGestureController());

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mapView != null) {
            mapView.closeMap();
            mapView.destroyMapWindow();
            mapView = null;
        }

    }
}
