package com.example.map3d;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class SportActivity extends AppCompatActivity implements LocationSource, AMapLocationListener, View.OnClickListener {

    private static final String TAG = "MainActivity";
    private MapView mapView;
    private Button changebtn;
    private Button clearbtn;
    private AMap aMap;

    private MyLocationStyle myLocationStyle;

    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    private static boolean isRun=false;
    private static int clicknum=0;
    private double currentLat;
    private double currentLon;

    private List<MarketBean> marketList;
    private List<LatLng> lat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport);
        mapView=findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        init();
        changebtn=findViewById(R.id.changebtn);
        changebtn.setOnClickListener(this);
        changebtn.setAlpha((float) 0.3);
        clearbtn=findViewById(R.id.clearbtn);
        clearbtn.setOnClickListener(this);
        clearbtn.setAlpha((float) 0.3);

    }

    public void init(){
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        aMap=mapView.getMap();

        aMap.setTrafficEnabled(true);// 显示实时交通状况
        //地图模式可选类型：MAP_TYPE_NORMAL,MAP_TYPE_SATELLITE,MAP_TYPE_NIGHT
//        aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 卫星地图模式
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(10000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        // 设置定位监听
        aMap.setLocationSource((LocationSource) this);
// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
// 设置定位的类型为定位模式，有定位、跟随或地图根据面向方向旋转几种



    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();

        deactivate();

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

    public void setCurrentLon(double currentLon) {
        this.currentLon = currentLon;
    }

    public double getCurrentLon() {
        return currentLon;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public double getCurrentLat() {
        return currentLat;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

        if (mListener != null&& aMapLocation != null) {
            if (aMapLocation != null
                    &&aMapLocation.getErrorCode() == 0) {
                this.setCurrentLat(aMapLocation.getLatitude()); ;//获取纬度
                this.setCurrentLon(aMapLocation.getLongitude());
                //获取经度
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                Toast.makeText(SportActivity.this,aMapLocation.getAddress(),Toast.LENGTH_SHORT).show();
                this.currentLat=currentLat;
                this.currentLon=currentLon;
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode()+ ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
                Toast.makeText(SportActivity.this,errText,Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            //初始化定位
            mlocationClient = new AMapLocationClient(this);
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位回调监听
            mlocationClient.setLocationListener(SportActivity.this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();//启动定位
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
        Toast.makeText(SportActivity.this,"close location",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        //添加坐标点
        if (marketList == null) {
            marketList = new ArrayList();
        }
        if(lat==null){
            lat=new ArrayList<>();
        }
        lat = new ArrayList<LatLng>();
        switch (view.getId()){
            case R.id.changebtn:
//                marketList.add(new MarketBean(this.getCurrentLat(), this.getCurrentLon(),
//                        "当前坐标第"+clicknum+"个", "经度："+this.getCurrentLat()+" "+"纬度："+this.getCurrentLon()));
//                aMap.addMarker(new MarkerOptions()
//                        .position(new LatLng(marketList.get(clicknum).getLatitude(),//设置纬度
//                                marketList.get(clicknum).getLongitude()))//设置经度
//                        .title(marketList.get(clicknum).getTitle())//设置标题
//                        .snippet(marketList.get(clicknum).getContent())//设置内容
//                        // .setFlat(true) // 将Marker设置为贴地显示，可以双指下拉地图查看效果
////                        .draggable(true) //设置Marker可拖动
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pen)));
////                lat.add((new LatLng(marketList.get(clicknum).getLatitude(),
////                        marketList.get(clicknum).getLongitude())));
//                for(int i=0;i<marketList.size();i++){
////                    lat.add(new LatLng(currentLat, currentLon));
//                    lat.add((new LatLng(marketList.get(i).getLatitude(),
//                        marketList.get(i).getLongitude())));
//                }
//
//                clicknum+=1;
////                Log.e(String.valueOf(SportActivity.this),"点击次数："+marketList.get(clicknum+1).getLatitude());
//                Log.e(String.valueOf(SportActivity.this),"点击次数："+clicknum);
//                Log.e(String.valueOf(SportActivity.this),"lat："+lat.size());
//                if(lat.size()!=1){
//                    aMap.addPolyline(new PolylineOptions().addAll(lat).width(10).color(Color.argb(150, 3, 1, 1)));
//                }

                handler.postDelayed(task, 5000);//每两秒执行一次runnable.

//                changebtn.setText("change_btn"+(num%6+1));
//                LatLng latLng = new LatLng(currentLat,currentLon);
//                final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).
//                        title("当前坐标").snippet("经度："+currentLat+" "+"纬度："+currentLon));
//                for(int i=0;i<5;i++){
//
//                    MarkerOptions markerOption = new MarkerOptions();
//                    markerOption.position(latLng);
//                    markerOption.title(""+i);
//                    markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
//                            .decodeResource(getResources(),R.mipmap.ic_launcher)));
//                    markerOption.setFlat(true);//设置marker平贴地图效果
//
//                }
//                addMoreMarket();
//                if(num%6==0){
////                    isRun=true;
////                    handler.post(task);
//                }else if(num%6==1){
//                    myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE);
//                }else if(num%6==2){
//                    myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
//                }else if(num%6==3){
//                    myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
//                }else if(num%6==4){
//                    myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);
//                }else if(num%6==5){
////                    isRun=false;
//                    aMap.clear();
//
//                    nlatLng=0;
//                    marketList.clear();
//                    lat.clear();
//                }
                break;
            case R.id.clearbtn:
                Log.e(String.valueOf(SportActivity.this),"click_clear："+clicknum);
                aMap.clear();
                clicknum=0;
                marketList.clear();
                lat.clear();
                handler.removeCallbacks(task);
                break;
            default:
                break;
        }
    }
    /**
     * 添加多个market
     */
//    private List<MarketBean> marketList;
//    private Polyline polyline;
//    private List<LatLng> lat = new ArrayList<LatLng>();

    private void addMoreMarket() {
        if (marketList == null) {
            marketList = new ArrayList();
        }
        //模拟6个假数据
        marketList.add(new MarketBean(22.675, 114.028, "标题1", "内容1"));
        marketList.add(new MarketBean(22.694, 114.099, "标题2", "内容2"));
        marketList.add(new MarketBean(22.667, 114.041, "标题3", "内容3"));
        marketList.add(new MarketBean(22.647, 114.023, "标题4", "内容4"));
        marketList.add(new MarketBean(22.688, 114.066, "标题5", "内容5"));
        marketList.add(new MarketBean(22.635, 114.077, "标题6", "内容6"));

        List<LatLng> lat = new ArrayList<LatLng>();
        for (int i = 0; i < marketList.size(); i++) {

            aMap.addMarker(new MarkerOptions()
                    .position(new LatLng(marketList.get(i).getLatitude(),//设置纬度
                            marketList.get(i).getLongitude()))//设置经度
                    .title(marketList.get(i).getTitle())//设置标题
                    .snippet(marketList.get(i).getContent())//设置内容
                    // .setFlat(true) // 将Marker设置为贴地显示，可以双指下拉地图查看效果
                    .draggable(true) //设置Marker可拖动
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));
            lat.add((new LatLng(marketList.get(i).getLatitude(),//设置纬度
                    marketList.get(i).getLongitude())));
        }aMap.addPolyline(new PolylineOptions().addAll(lat).width(10).color(Color.argb(150, 3, 1, 1)));
//        List<LatLng> lat = new ArrayList<LatLng>();
//        lat.add(new LatLng(39.999391,116.135972));
//        lat.add(new LatLng(39.898323,116.057694));
//        lat.add(new LatLng(39.900430,116.265061));
//        lat.add(new LatLng(39.955192,116.140092));
//        polyline =AMap.addPolyline(new PolylineOptions().
//                addAll(lat).width(10).color(Color.argb(255, 1, 1, 1)));
    }


//    开启线程(每隔一秒获取坐标，通过判断前后坐标来判断是否运动)
    private Thread thread;
    private static int nlatLng=0;
//    @Override
//    public void run() {
//        if (marketList == null) {
//            marketList = new ArrayList();
//        }
//        marketList.add(new MarketBean(currentLat, currentLon, "标题"+(nlatLng+1), "内容"+(nlatLng+1)));
//        while (isRunning()){
//            Toast.makeText(SportActivity.this,"线程运行中",Toast.LENGTH_SHORT).show();
//            Log.e(String.valueOf(SportActivity.this),"线程运行中");
//            nlatLng+=1;
//            try {
//
//                marketList.add(new MarketBean(currentLat, currentLon, "标题"+(nlatLng+1), "内容"+(nlatLng+1)));
//                aMap.addMarker(new MarkerOptions()
//                        .position(new LatLng(marketList.get(nlatLng).getLatitude(),//设置纬度
//                                marketList.get(nlatLng).getLongitude()))//设置经度
//                        .title(marketList.get(nlatLng).getTitle())//设置标题
//                        .snippet(marketList.get(nlatLng).getContent())); //设置内容
//                        // .setFlat(true) // 将Marker设置为贴地显示，可以双指下拉地图查看效果
////                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))
//                lat.add((new LatLng(marketList.get(nlatLng).getLatitude(),//设置纬度
//                        marketList.get(nlatLng).getLongitude())));
//                aMap.addPolyline(new PolylineOptions().addAll(lat).width(10).color(Color.argb(150, 1, 1, 1)));
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                Toast.makeText(SportActivity.this,"thread earo",Toast.LENGTH_SHORT).show();
//            }
//
//            Log.e(String.valueOf(SportActivity.this),"数字："+nlatLng);
//        }
//        Log.e(String.valueOf(SportActivity.this),"线程运行end");
//    }
//    public void start(){
//        if (thread == null) {
//            thread = new Thread (this);
//            thread.start ();
//            Toast.makeText(SportActivity.this,"线程开始",Toast.LENGTH_SHORT).show();
//            Log.e(String.valueOf(SportActivity.this),"线程开始");
//        }
//    }
//    public boolean isRunning(){
//        if(nlatLng-1<0){
//            return false;
//        }else{
//            if(marketList.get(nlatLng).getLatitude()==marketList.get(nlatLng-1).getLatitude()&&
//                    marketList.get(nlatLng).getLongitude()==marketList.get(nlatLng-1).getLongitude()){
//                return false;
//            }
//            return true;
//        }
//
//    }

    private Handler handler = new Handler();

    private Runnable task = new Runnable() {
        public void run() {
            // TODO Auto-generated method stub
            handler.postDelayed(this, 5 * 1000);//设置延迟时间，此处是5秒
            //需要执行的代码

            marketList.add(new MarketBean(SportActivity.this.getCurrentLat(), SportActivity.this.getCurrentLon(),
                    "当前坐标第"+clicknum+"个", "经度："+SportActivity.this.getCurrentLat()+" "+"纬度："+SportActivity.this.getCurrentLon()));
            aMap.addMarker(new MarkerOptions()
                    .position(new LatLng(marketList.get(clicknum).getLatitude(),//设置纬度
                            marketList.get(clicknum).getLongitude()))//设置经度
                    .title(marketList.get(clicknum).getTitle())//设置标题
                    .snippet(marketList.get(clicknum).getContent())//设置内容
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pen)));
            for(int i=0;i<marketList.size();i++){
                lat.add((new LatLng(marketList.get(i).getLatitude(),
                        marketList.get(i).getLongitude())));
            }
            clicknum+=1;
            Log.e(String.valueOf(SportActivity.this),"点击次数："+clicknum);
            Log.e(String.valueOf(SportActivity.this),"lat："+lat.size());
            if(lat.size()!=1){
                aMap.addPolyline(new PolylineOptions().addAll(lat).width(10).color(Color.argb(150, 3, 255, 1)));
            }

        }
    };

}
