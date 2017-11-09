package com.example.admin.piemapdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.piemapdemo.view.RotateImageView;

import java.text.DecimalFormat;

import pie.core.DimensionMode;
import pie.core.MapRotateChangedListener;
import pie.core.MapScaleChangedListener;
import pie.core.MapView;
import pie.core.Point2D;
import pie.core.SceneMode;
import pie.map.MapViews;
import pie.map.gesture.MapGestureController;

/**
 * 旋转缩放功平移功能
 * */
public class RotateAndZoomActivity extends AppCompatActivity implements View.OnClickListener {

    private MapViews mMapViews;
    private MapView mBasicOpMapView;

    private ImageButton zoomInButton;
    private ImageButton zoomOutButton;
    private RotateImageView compassButton;
    private TextView scaleTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotate_and_zoom);

        initView();
        openMap("googlemap");
    }



    private void initView() {
        mMapViews = (MapViews) findViewById(R.id.mvs_pie_basic_op_map);
        mBasicOpMapView = mMapViews.getMapView();
        // 2维和3维 在打开地图之前设置
        mBasicOpMapView.setDimensionMode(DimensionMode.D3DMode);
        zoomInButton = (ImageButton) findViewById(R.id.ib_pie_zoom_in);
        zoomOutButton = (ImageButton) findViewById(R.id.ib_pie_zoom_out);
        compassButton = (RotateImageView) findViewById(R.id.riv_pie_fix_compass);
        scaleTextView = (TextView) findViewById(R.id.tv_pie_scale_value);

        zoomInButton.setOnClickListener(this);
        zoomOutButton.setOnClickListener(this);
        compassButton.setOnClickListener(this);

        setMapView();
    }




    private void openMap(String name) {

        boolean flag = false;

        if (mBasicOpMapView != null) {
            // 关联工作空间
            mBasicOpMapView.attachWorkspace(PieMapApiApplication.mWorkspace);
            // 打开地图
            flag = mBasicOpMapView.openMap(name);

        }
        if (!flag) {
            Toast.makeText(this, "打开地图失败", Toast.LENGTH_SHORT).show();
            finish();
        }

        mBasicOpMapView.setPitch(true);// 设置是否可俯仰
        mBasicOpMapView.setRotate(true);// 设置是否可旋转

        mBasicOpMapView.setSceneMode(SceneMode.PlaneMode);

        // lon=115.74021993261361,lat=40.7170782600845
        Point2D centerPoint2d = new Point2D(115.74021993261361,
                40.7170782600845);
        centerPoint2d = mBasicOpMapView.getPrjCoordSys().latLngToProjection(
                centerPoint2d);

        Log.i("zrc", "x: " + centerPoint2d.x + "y: " + centerPoint2d.y);
        mBasicOpMapView.setMapCenter(centerPoint2d);
        mBasicOpMapView.refresh(true, false);
        setScaleTextView();
        // GisNative.LS_MW_Refresh(hMapWnd, bforce, bCleanCache)
        // mBasicOpMapView.refresh(true, false);
        // mBasicOpMapView.viewEntire(); //设置地图默认在屏幕内显示全部的区域地图，同时设置比例尺，比例尺设置无效

    }





    private void setMapView() {
        DisplayMetrics metric = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int densityDpi = metric.densityDpi;
        mBasicOpMapView.setDeviceDPI(densityDpi);
        double scale_default = 6.089714055909923E-6;
        mBasicOpMapView.setScale(scale_default);
        Point2D centerPoint2d = new Point2D(115.74021993261361,
                40.7170782600845);
        centerPoint2d = mBasicOpMapView.getPrjCoordSys().latLngToProjection(
                centerPoint2d);
        mBasicOpMapView.setMapCenter(centerPoint2d);
        mBasicOpMapView
                .setMapRotateChangedListener(new AngleRotateChangedListener());
        mBasicOpMapView
                .setMapScaleChangedListener(new ScaleValueChangedListener());
        mBasicOpMapView.setMapGestureController(new MapGestureController());

    }


    /**
     * 地图旋转角度改变监听
     *
     * @author pie
     */
    private class AngleRotateChangedListener implements
            MapRotateChangedListener {

        @Override
        public void onRotateChanged(double angle) {
            compassButton.setAngle(angle);
        }

    }

    /**
     * 地图缩放回调
     *
     * @author pie
     */
    private class ScaleValueChangedListener implements MapScaleChangedListener {

        @Override
        public void onScaleChanged(double scale) {
            setScaleTextView();
        }

    }


    /**
     * 设置显示缩放比例 TextView
     */
    private void setScaleTextView() {
        int value = getCurrentScaleValue() / 100;// 厘米 换算成米
        if (value < 1000) {
            scaleTextView.setText(value + "m");
        } else {
            double valueF = value / 1000.00;
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            String dfValue = decimalFormat.format(valueF);
            scaleTextView.setText(dfValue + "km");
        }
    }


    /**
     * 获取当前比例尺 对应 1：value
     *
     * @return 1:value,value值（单位 cm）
     */
    private int getCurrentScaleValue() {
        double scale = mBasicOpMapView.getScale();
        int scaleValue = (int) (1 / scale);
        return scaleValue;
    }




    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        // 先关闭地图，再销毁地图，再关闭工作区间
        if (mBasicOpMapView != null) {
            mBasicOpMapView.closeMap();
            mBasicOpMapView.destroyMapWindow();
            mBasicOpMapView = null;
        }

        super.onDestroy();

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_pie_zoom_in:
                mapZoomIn();
                break;
            case R.id.ib_pie_zoom_out:
                mapZoomOut();
                break;
            case R.id.riv_pie_fix_compass:
                resetMapAngle();
                break;
            default:
                break;
        }

    }


    /**
     * 地图放大
     */
    private void mapZoomIn() {
        mBasicOpMapView.zoomIn();
        Log.i("zrc", mBasicOpMapView.getScale() + "");

    }


    /**
     * 地图缩小
     */
    private void mapZoomOut() {
        mBasicOpMapView.zoomOut();
    }

    /**
     * 恢复地图指北
     */
    private void resetMapAngle() {

        int currAngle = (int) mBasicOpMapView.getRollAngle();
        if (currAngle == 0 || currAngle == 360) {
            return;
        }
        if (currAngle > 180) {
            mBasicOpMapView.startRotateAnimation(currAngle, 360);
        } else {
            mBasicOpMapView.startRotateAnimation(currAngle, 0);
        }
        mBasicOpMapView.setPitchAngle(0);

    }
}
