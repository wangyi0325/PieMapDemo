package com.example.admin.piemapdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.admin.piemapdemo.constats.Path;


import java.util.Observable;

import pie.core.DataSource;
import pie.core.DsConnection;
import pie.core.EngineType;
import pie.core.GeoPoint;
import pie.core.Geometry;
import pie.core.MapView;
import pie.core.Point2D;
import pie.core.Style;
import pie.map.MapLayoutParams;
import pie.map.MapViews;
import pie.map.gesture.MapGestureController;

import static pie.core.Style.MarkerType_Icon;


public class LocationActivity extends AppCompatActivity {

    private MapViews mapViews;
    private ImageButton mImgButton;
    private MapView mmapView;
    private String demDataPath;
    private DataSource mDataSource;
    private boolean mOpensource = false; // 数据源是否打开成功

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        demDataPath = Path.PATH_PIE_DEM_DATA_DEFAULT;
        mDataSource = new DataSource();

        initView();//初始化数据
        openMap("googlemap");
        openDataSource();
    }



    private void initView() {
        mapViews = (MapViews) findViewById(R.id.mvs_pie_dem_land_analysis_map);
        mmapView = mapViews.getMapView();
        mImgButton = (ImageButton) findViewById(R.id.ib_pie_location_to_dem);

        //点击定位
        mImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationToDem();
            }
        });


        setMapView();
    }



    private void openMap(String name) {

        boolean flag = false;

        if (mmapView != null) {
            // 关联工作空间
            mmapView
                    .attachWorkspace(PieMapApiApplication.mWorkspace);
            // 打开地图
            flag = mmapView.openMap(name);
            // 添加Dem数据，在打开地图后一定要设置自动投影
            mmapView.setAutoProjection(true);

        }
        if (!flag) {
            Toast.makeText(this, "打开地图失败", Toast.LENGTH_SHORT).show();
            finish();
        }

        Point2D centerPoint2d = new Point2D(115.74021993261361,
                40.7170782600845);
        centerPoint2d = mmapView.getPrjCoordSys()
                .latLngToProjection(centerPoint2d);

          mmapView.setMapCenter(centerPoint2d);

    }




    private void setMapView() {
        DisplayMetrics metric = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int densityDpi = metric.densityDpi;
        mmapView.setDeviceDPI(densityDpi);
        double scale_default = 6.089714055909923E-6;
        mmapView.setScale(scale_default);
        Point2D centerPoint2d = new Point2D(115.74021993261361,
                40.7170782600845);
        centerPoint2d = mmapView.getPrjCoordSys().latLngToProjection(
                centerPoint2d);
        mmapView.setMapCenter(centerPoint2d);
        mmapView.setMapGestureController(new MapGestureController());

    }


    /**
     * 打开数据源
     */
    private void openDataSource() {
        DsConnection DsConn = new DsConnection();
        DsConn.type = EngineType.Plugin.getValue();// Plugin数据源类型
        DsConn.bReadOnly = false;
        DsConn.strServer = demDataPath + "DEMTest.tif";// 为有效路径
        DsConn.strDatabase = "";
        DsConn.strAlias = "DEM25";
        mOpensource = mDataSource.open(DsConn);
    }


    /**
     * 定位到有Dem数据处
     */
    private void locationToDem() {
        double demX = 108.95; // 经度 DEMTest
        double demY = 34.27; // 纬度 DEMTest
        // double demX = 116.3; // 经度 BeijingTest
        // double demY = 40.66; // 纬度 BeijingTest

        Point2D startPoint = mmapView.getMapCenter();
        Point2D endPnt = mmapView.getPrjCoordSys().forward(new Point2D(demX,demY));
        mmapView.startPanAnimation(startPoint , endPnt);
        mmapView.setScale(2.105484291333595E-5);

       /* GeoPoint geopt = new GeoPoint(endPnt.x, endPnt.y);
//        Geometry geometry = new Geometry();
        Style pointStyle = new Style();
        pointStyle.markerStyle = 1;
        pointStyle.markerSize = 10;
        pointStyle.markerType = 1;
        geopt.setStyle(pointStyle);
        mmapView.getTrackingLayer().addGeometry("dempoint", geopt);*/

      /*添加定位图标方法一*/
        ImageView image= new ImageView(this);
        Bitmap  bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.location);
        image.setImageBitmap(bitmap);
        MapLayoutParams pararms = new MapLayoutParams(100,100,endPnt,1);
        mapViews.addView(image,pararms);

    }

}
