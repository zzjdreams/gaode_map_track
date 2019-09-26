# gaode_map_track
__请忽视部分：__  
_MainActive及其相关的布局文件:这部分是我直接复制别人的代码直接运行，结果发现他们要创建很多文件夹而且部分文件没展示出来导致我无法成功运行，但我又不想放弃这些代码的思路，于是就选择保存下来方便我借阅。_  
_被注释部分：这部分的代码都是我从高德开发文档抄录下来的代码和从别人博客中搬运过来的代码（被我乱改后的），这些代码是我遇到问题和不断修改的过程，所以我这部分也保留了大半，方便以后查看。_  
_上传图片太麻烦，我不上传了。_
  
__思路：__  
## 获取key值 
这一部分网络上一大堆，就不整理了（听说没了这部分高德地图的相关功能会用不了）
 
##下载高德地图SDK并将其配置给Android studio##  
我这里下载的是3D地图、定位、导航、搜索、猎鹰的整合jar包。  
*遇到的问题：  
1、使用2d地图加载时地图瓦片方式加载出来，而且有时会有一块加载不出来，需要放大缩小才加载完全，移动地图时地图加载较慢。其次是2d地图不支持3d地图的很多功能，例如导航功能。为了避免后期可能遇到的问题，因此果断选择3d地图。  
2、2d地图和3d地图不能显示在一个mapview中，且不能重复导入相同的SDK，不然会编译错误无法安装。（我就是同时导入了2d地图和其定位功能和3d地图的所有功能，结果一直编译报错，纠结了我很久。）  
3、解压完3d地图包后会发现有1个jar和5个文件夹。需要将jar包放在lib中（Android studio添加project structure），将其他5个文件夹放入 app\src\main\jniLibs 的目录下，没有自行创建，之后不用设置SourceSets*  
  
## 创建地图  
### layout文件   
3d地图使用：
<com.amap.api.maps.MapView  
        android:id="@+id/mapView"  
        android:layout_width="match_parent"  
        android:layout_height="match_parent"/>    
 2d地图使用：  
 <com.amap.api.maps2d.MapView  
        android:id="@+id/mapview"  
        android:layout_width="match_parent"  
        android:layout_height="0px"  
        android:layout_weight="6"/>    
   
### Activity   
#### 地图显示   
    private MapView mMapView;//显示地图的视图  
    private AMap aMap;//定义AMap 地图对象的操作方法与接口。  
      
    mMapView = (MapView) findViewById(R.id.mapview);    
    mMapView.onCreate(savedInstanceState);//必须调用    
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
   
#### 定位  
    private MyLocationStyle myLocationStyle;   
    private OnLocationChangedListener mListener;   
    private AMapLocationClient mlocationClient;   
    private AMapLocationClientOption mLocationOption;   
 public void init(){  
        if (aMap == null) {//初始化地图，加载地图时直接进行  
            aMap = mapView.getMap();  
        }  
        aMap=mapView.getMap();  
        aMap.setTrafficEnabled(true);// 显示实时交通状况  
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。  
        myLocationStyle.interval(10000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。  
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);  
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style  
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。  
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。  
        // 设置定位监听  
        aMap.setLocationSource((LocationSource) this);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false  
        aMap.setMyLocationEnabled(true);// 设置定位的类型为定位模式，有定位、跟随或地图根据面向方向旋转几种  
    }   
 @Override  
    public void onLocationChanged(AMapLocation aMapLocation) {  
        if (mListener != null&& aMapLocation != null) {  
            if (aMapLocation != null  
                    &&aMapLocation.getErrorCode() == 0) {  
                this.setCurrentLat(aMapLocation.getLatitude());//获取纬度实例化后的经纬度    
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
#### 标点和画线   
实例化Marker，具体见MarketBean。java，向其传递经度纬度，标题内容   
    private double currentLat;   
    private double currentLon;  
   
    private List<MarketBean> marketList;  
    private List<LatLng> lat;   
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
                aMap.addPolyline(new PolylineOptions().addAll(lat).width(10).color(Color.argb(150, 3, 255, 1)));//画线   
            }   
        }   
    };   
    
    //调用循环  
    handler.postDelayed(task, 5000);//每两秒执行一次runnable.   
    //清除所有数据   
    aMap.clear();  
    clicknum=0;  
    marketList.clear();  
    lat.clear();  
    handler.removeCallbacks(task);  
``` 
``` 
