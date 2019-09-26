package com.example.map3d;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.navi.services.search.model.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private MapView mapView;
    private AMap aMap;

//    private OnLocationChangedListener mListener;
    //mLocationClient对象
    public AMapLocationClient mLocationClient = null;
    //mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    //比例尺组件
    private UiSettings mUiSettings;
    //搜索组件
    private GeocodeSearch geocoderSearch;
    private Marker geoMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView=findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
        handler.post(task);
    }

    public void init(){
        if (aMap == null) {
            aMap = mapView.getMap();
        }

    }

//    private Polyline polyline;
//    private MsgInfo msgInfo;
//
//    public MsgInfo getMsgInfo() {
//        return msgInfo;
//    }
//
//    public void setMsgInfo(MsgInfo msgInfo) {
//        this.msgInfo = msgInfo;
//    }
//
//    @Override
//    protected void initViewsAndEvents() {
//        msgInfo = (MsgInfo) getIntent().getSerializableExtra("date");
//
//        if (aMap == null) {
//            aMap = mapView.getMap();
//            mUiSettings = aMap.getUiSettings();
//            mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);// 设置地图logo显示在左下方
//            mUiSettings.setScaleControlsEnabled(true);  //设置地图的比例尺显示
//
//            setUpMap();
//            geoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
//                    .icon(BitmapDescriptorFactory
//                            .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
//        }
//
//        geocoderSearch = new GeocodeSearch(this);
//        geocoderSearch.setOnGeocodeSearchListener(this);
//
//        //按时间对结果集进行排序
//        Collections.sort(msgInfo.getPageList(), new SortByTime());
//
//
//
//        List<LatLng> latLngs = new ArrayList<LatLng>();
//        for (MsgInfo.MsgBody body : msgInfo.getPageList()) {
//            //添加点集合
//            drawPoint(body.getLat(), body.getLng(), DateUtils.dateFormat(body.getDate().longValue() * 1000));
//            //添加线集合
//            latLngs.add(new LatLng(body.getLat(), body.getLng()));
//            polyline = aMap.addPolyline(new PolylineOptions().
//                    addAll(latLngs).width(5).color(Color.argb(255, 255, 1, 1)));
//        }
//
//
//        //移动到第一个位置
//        getAddress(msgInfo.getPageList().get(0).getLat(), msgInfo.getPageList().get(0).getLng());
//
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();

    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }

//    private void setUpMap() {
//        aMap.moveCamera(CameraUpdateFactory.zoomTo(0)); //设置缩放为0，则一进来就显示整个中国大陆
//        aMap.setLocationSource(this);// 设置定位监听
//
//        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
//        //添加标记
//        // aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 卫星地图模式
//
//
//        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
//        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
//        //  aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
//
//        aMap.setOnMarkerClickListener(onMarkerClickListener);
//    }
//
//    protected View getLoadingTargetView() {
//        return null;
//    }
//
//    @Override
//    public void onLocationChanged(AMapLocation amapLocation) {
//        if (mListener != null && amapLocation != null) {
//            if (amapLocation != null
//                    && amapLocation.getErrorCode() == 0) {
//                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
//            } else {
//                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
//                Log.e("AmapErr", errText);
//            }
//        }
//
//
//    }
//
//    @Override
//    public void activate(OnLocationChangedListener listener) {
//
//        mListener = listener;
//        if (mLocationClient == null) {
//            mLocationClient = new AMapLocationClient(this);
//            mLocationOption = new AMapLocationClientOption();
//            //设置定位监听
//            mLocationClient.setLocationListener(this);
//            //设置为高精度定位模式
//            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//            //设置定位参数
//            mLocationClient.setLocationOption(mLocationOption);
//            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
//            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
//            // 在定位结束后，在合适的生命周期调用onDestroy()方法
//            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
//
//            //这里无需一直对自己定位
//            //mLocationClient.startLocation();
//        }
//    }
////
//    @Override
//    public void deactivate() {
//
//        mListener = null;
//        if (mLocationClient != null) {
//            mLocationClient.stopLocation();
//            mLocationClient.onDestroy();
//        }
//        mLocationClient = null;
//    }
//
//    String addressName;
//    LatLonPoint latLonPoint;
//    @Override
//    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
//
//        if (rCode == 1000) {
//            if (result != null && result.getRegeocodeAddress() != null
//                    && result.getRegeocodeAddress().getFormatAddress() != null) {
//                addressName = result.getRegeocodeAddress().getFormatAddress()
//                        + "附近";
//                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
//                        AMapUtil.convertToLatLng(latLonPoint), 15));
//                geoMarker.setPosition(AMapUtil.convertToLatLng(latLonPoint));
////                ToastUtils.showToast(this, "最近位置：" + addressName);
//                Toast.makeText(MainActivity.this, "最近位置：" + addressName,Toast.LENGTH_SHORT).show();
//            } else {
////                ToastUtils.showToast(this, "没有查询到结果");
//                Toast.makeText(MainActivity.this,"没有查询到结果",Toast.LENGTH_SHORT).show();
//            }
//        } else {
////            ToastUtils.showToast(this, rCode);
//            Toast.makeText(MainActivity.this,rCode,Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public void onGeocodeSearched(GeocodeResult result, int rCode) {
//        if (rCode == 1000) {
//            if (result != null && result.getGeocodeAddressList() != null
//                    && result.getGeocodeAddressList().size() > 0) {
//                GeocodeAddress address = result.getGeocodeAddressList().get(0);
//                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
//                        AMapUtil.convertToLatLng(address.getLatLonPoint()), 15));
//                geoMarker.setPosition(AMapUtil.convertToLatLng(address.getLatLonPoint()));
//                addressName = "经纬度值:" + address.getLatLonPoint() + "\n位置描述:"
//                        + address.getFormatAddress();
////                ToastUtils.showToast(this, addressName);
//                Toast.makeText(MainActivity.this,addressName,Toast.LENGTH_SHORT).show();
//            } else {
////                ToastUtils.showToast(this, "没有查询到结果~！");
//                Toast.makeText(MainActivity.this,"没有查询到结果",Toast.LENGTH_SHORT).show();
//            }
//        } else {
////            ToastUtils.showToast(this, rCode);
//            Toast.makeText(MainActivity.this,rCode,Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    /**
//     * 响应地理编码
//     */
//    public void getLatlon(final String name) {
//        //showDialog();
//        GeocodeQuery query = new GeocodeQuery(name, "0086");// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
//        geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
//    }
//
//
//    private void getAddress(double v, double v1) {
//        this.latLonPoint = new LatLonPoint(v, v1);
//        RegeocodeQuery query = new RegeocodeQuery(latLonPoint,100, GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
//        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
//    }
//
//
//    private void drawPoint(double v, double v1, String time) {
//        MarkerOptions markerOption = new MarkerOptions();
//        markerOption.position(new LatLng(v, v1));
//        markerOption.title("定位时间:\n"+time);
//        // markerOption.snippet("西安市：111");
//        markerOption.draggable(true);
//        markerOption.setFlat(true);
//        aMap.addMarker(markerOption);
//
//    }
//
//    AMap.OnMarkerClickListener onMarkerClickListener = new AMap.OnMarkerClickListener() {
//
//        @Override
//        public boolean onMarkerClick(Marker arg0) {
//            arg0.showInfoWindow();
//            return false;
//        }
//    };
//
//
private Handler handler = new Handler();

    private Runnable task =new Runnable() {
        public void run() {
            // TODOAuto-generated method stub
            handler.postDelayed(this,5*1000);//设置延迟时间，此处是5秒
            //需要执行的代码
        }
    };






}


