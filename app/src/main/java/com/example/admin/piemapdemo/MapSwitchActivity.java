package com.example.admin.piemapdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import pie.core.DimensionMode;
import pie.core.MapView;
import pie.core.SceneMode;
import pie.map.MapViews;
import pie.map.gesture.MapGestureController;

/**
 * 地图切换
 * */
public class MapSwitchActivity extends AppCompatActivity implements View.OnClickListener {


    private MapViews mMapViews;
    private MapView mSwitchMapView;
    private Button mSwitchButton;
    private MapView testMapView;


    private boolean isSwitch = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_switch);


        initView();//初始化控件
        testView();
        openMap("pieimage");
        openTestMap("googlemap");
    }





    private void initView() {
        mMapViews = (MapViews) findViewById(R.id.mvs_pie_basic_map);
        mSwitchMapView = mMapViews.getMapView();
        mSwitchButton = (Button) findViewById(R.id.btn_pie_switch_map);
        mSwitchButton.setOnClickListener(this);
        setMapView();
    }


    private void  testView() {
        testMapView = new MapView(this);
        DisplayMetrics metric = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int densityDpi = metric.densityDpi;
        testMapView.setDeviceDPI(densityDpi);
        // 打开地图之前设置
        testMapView.setDimensionMode(DimensionMode.D2DMode);


    }


    private void setMapView() {
        DisplayMetrics metric = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int densityDpi = metric.densityDpi;
        mSwitchMapView.setDeviceDPI(densityDpi);
        // 打开地图之前设置
        mSwitchMapView.setDimensionMode(DimensionMode.D3DMode);
        mSwitchMapView.setMapGestureController(new MapGestureController());
    }

    private void openMap(String name) {

        boolean flag = false;

        if (mSwitchMapView != null) {
            // 关联工作空间
            mSwitchMapView.attachWorkspace(PieMapApiApplication.mWorkspace);
            // 打开地图
            flag = mSwitchMapView.openMap(name);

        }
        if (!flag) {
            Toast.makeText(this, "打开地图失败", Toast.LENGTH_SHORT).show();
            finish();
        }

        mSwitchMapView.setPitch(true);// 设置是否可俯仰
        mSwitchMapView.setRotate(true);// 设置是否可旋转

        mSwitchMapView.setSceneMode(SceneMode.PlaneMode);
        mSwitchMapView.viewEntire();
        // mBasicMapView.viewEntire();

    }



    private void openTestMap(String name) {

        boolean flag = false;

        if (testMapView != null) {
            // 关联工作空间
            testMapView.attachWorkspace(PieMapApiApplication.mWorkspace);
            // 打开地图
            flag = testMapView.openMap(name);

        }
        if (!flag) {
            Toast.makeText(this, "打开地图失败", Toast.LENGTH_SHORT).show();
            finish();
        }

        testMapView.setPitch(true);// 设置是否可俯仰
        testMapView.setRotate(true);// 设置是否可旋转

        testMapView.setSceneMode(SceneMode.PlaneMode);
        testMapView.viewEntire();
        // mBasicMapView.viewEntire();

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pie_switch_map:
                switchMap();
            default:
                break;
        }

    }

    /**
     * 地图切换
     */
    private void switchMap() {
        if (isSwitch) {
            mSwitchMapView.closeMap();
            mSwitchMapView.openMap("googlemap");
            mSwitchMapView.setSceneMode(SceneMode.SphereMode);
            mSwitchMapView.setScale(mSwitchMapView.getScale() * 2);
        } else {
            mSwitchMapView.closeMap();
            mSwitchMapView.openMap("pieimage");
            mSwitchMapView.setSceneMode(SceneMode.PlaneMode);
        }
        isSwitch = !isSwitch;

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        // 先关闭地图，再销毁地图，再关闭工作区间
        if (mSwitchMapView != null) {
            mSwitchMapView.closeMap();
            mSwitchMapView.destroyMapWindow();
            mSwitchMapView = null;
        }


    }

}
