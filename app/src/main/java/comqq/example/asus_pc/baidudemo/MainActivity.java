package comqq.example.asus_pc.baidudemo;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.bikenavi.BikeNavigateHelper;
import com.baidu.mapapi.bikenavi.adapter.IBEngineInitListener;
import com.baidu.mapapi.bikenavi.adapter.IBRoutePlanListener;
import com.baidu.mapapi.bikenavi.model.BikeRoutePlanError;
import com.baidu.mapapi.bikenavi.params.BikeNaviLauchParam;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.List;

public class MainActivity extends Activity {
    BikeNavigateHelper mNaviHelper;
    BikeNaviLauchParam param;
    boolean isFirstLocate = true;
    private Button btn_searcher;
    private Button btn_delicacy;
    private Button btn_clean;
    private Button btn_myself;
    private Button btn_quanjing;
    private BDLocation mlocation;
    private MapView mMapView = null;
    private PoiSearch mPoiSearch;
    private BaiduMap mbaidumap;
    private TextView txt;
    public LocationClient mLocationClient = null;
    private PoiResult mpoiResult;
    final int DELICACY_CODE = 2;
    final int SERCHER_CODE = 1;
    private int code = -1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        setContentView(R.layout.activity_main);
        initView();
        mLocationClient.start();
    }


    private void initView() {
        btn_myself = (Button) findViewById(R.id.btn_myself);
        btn_searcher = (Button) findViewById(R.id.btn_searcher);
        btn_delicacy = (Button) findViewById(R.id.btn_delicacy);
        btn_clean = (Button) findViewById(R.id.btn_clean);
        btn_quanjing = (Button) findViewById(R.id.btn_quanjin);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mbaidumap = mMapView.getMap();
        btn_myself.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng ll = new LatLng(mlocation.getLatitude(), mlocation.getLongitude());
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
                mbaidumap.setMapStatus(update);
//            mbaidumap.animateMapStatus(update);
                update = MapStatusUpdateFactory.zoomTo(16f);
                mbaidumap.animateMapStatus(update);
            }
        });
        btn_searcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (code != 1) {
                    code = 1;
                    mbaidumap.clear();
                    initseacher();
                    mPoiSearch.searchInCity((new PoiCitySearchOption())
                            .city("广州")
                            .keyword("学校")
                            .pageNum(10));
                }
            }
        });
        btn_delicacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (code != 2) {
                    code = 2;
                    mbaidumap.clear();
                    initseacher();
                    mPoiSearch.searchInCity((new PoiCitySearchOption())
                            .city("广州")
                            .keyword("美食")
                            .pageNum(10));
                }
            }
        });
        btn_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mbaidumap.clear();
            }
        });
        btn_quanjing.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, PanoramaDemoActivityMain.class);
                //23.18781,113.360469
                LatLng latLng = new LatLng(23.18781, 113.360469);
                intent.putExtra("postion", latLng);
                startActivity(intent);
            }
        });
        mbaidumap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, PanoramaDemoActivityMain.class);
                intent.putExtra("postion", marker.getPosition());
                startActivity(intent);
                return false;
            }
        });
        mbaidumap.setOnMyLocationClickListener(new BaiduMap.OnMyLocationClickListener() {
            @Override
            public boolean onMyLocationClick() {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, PanoramaDemoActivityMain.class);
                //113.360469,23.18781
                LatLng latLng = new LatLng(23.18781, 113.360469);
                intent.putExtra("postion", latLng);
                startActivity(intent);
                return false;
            }
        });
        mbaidumap.setMyLocationEnabled(true);
    }

    private void initseacher() {
        MapStatusUpdate update = MapStatusUpdateFactory.zoomTo(14f);
        mbaidumap.animateMapStatus(update);
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                mpoiResult = poiResult;
                addmaplocation();
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        });

    }

    private void addmaplocation() {
        for (int i = 0; i < 10; i++) {
            Toast.makeText(MainActivity.this, mpoiResult.getAllPoi().get(i).name + mpoiResult.getAllPoi().get(i).address, Toast.LENGTH_LONG).show();
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.mipmap.ic_launcher);
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions()
                    .position(mpoiResult.getAllPoi().get(i).location)
                    .icon(bitmap);
//在地图上添加Marker，并显示
            OverlayOptions option1 = new TextOptions()
                    .text(mpoiResult.getAllPoi().get(i).name)
                    .fontSize(24)
                    .position(mpoiResult.getAllPoi().get(i).location);
            mbaidumap.addOverlay(option);
            mbaidumap.addOverlay(option1);
        }
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系

        int span = 1000;
        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要

        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps

        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

        mLocationClient.setLocOption(option);
    }

    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    private void navigateto(BDLocation location) {
        if (isFirstLocate) {
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            mbaidumap.setMapStatus(update);
//            mbaidumap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            mbaidumap.animateMapStatus(update);
            isFirstLocate = false;
        }
        //113.360469,23.18781
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(23.18781);
        locationBuilder.longitude(113.360469);
        MyLocationData locationData = locationBuilder.build();
        mbaidumap.setMyLocationData(locationData);

    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            mlocation = location;
            navigateto(location);
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }
}
