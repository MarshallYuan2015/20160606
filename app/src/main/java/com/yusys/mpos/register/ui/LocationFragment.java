package com.yusys.mpos.register.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.yusys.mpos.R;
import com.yusys.mpos.base.manager.LogUtil;
import com.yusys.mpos.base.ui.BaseFragment;
import com.yusys.mpos.base.ui.ListFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 登记店铺信息界面
 * 其中有重要的一步就是定位
 * parent是RegisterActivity,注册流程第7步
 *
 * @author yuanshuai (marshall.yuan@foxmail.com)
 * @since 2016-05-26 09:43
 */
public class LocationFragment extends BaseFragment {

    private View fragmentView;
    private RegisterActivity parentActivity;
    @Bind(R.id.tv_city)
    TextView tv_city;
    @Bind(R.id.tv_location)
    public TextView tv_location;
    @Bind(R.id.edt_bill)
    EditText edt_bill;
    SweetAlertDialog pDialog;

    public LocationClient mLocationClient;

    /**
     * 实现实位回调监听
     */
    public BDLocationListener listener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location != null) {
                tv_city.setText(location.getCity());
                tv_location.setText(location.getLocationDescribe());
                mLocationClient.unRegisterLocationListener(listener);
                mLocationClient.stop(); // 停止定位服务
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.cancel();
                }
            }
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            LogUtil.e("百度定位", sb.toString());
            ListFragment fragment = (ListFragment) parentActivity.fragments.get(9);
            if (fragment.array == null) {
                fragment.array = new ArrayList<>();
            } else {
                fragment.array.clear();
            }
            if (list != null) {
                for (Poi p : list) {
                    fragment.array.add(p.getName());
                }
                fragment.adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (fragmentView == null) {
            fragmentView = inflater.inflate(R.layout.fragment_location, container, false);
            ButterKnife.bind(this, fragmentView);
            parentActivity = (RegisterActivity) getActivity();
            mLocationClient = new LocationClient(getActivity().getApplicationContext());
            mLocationClient.registerLocationListener(listener); // 注册监听函数
            LocationClientOption option = new LocationClientOption();
            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
            int span = 1000;
            option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
            option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
            option.setOpenGps(true);//可选，默认false,设置是否使用gps
            option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
            option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
            option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
            option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
            option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
            option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
            mLocationClient.setLocOption(option);
            mLocationClient.start();
        }
        // 缓存Fragment,避免重新执行onCreateView
        ViewGroup parentView = (ViewGroup) fragmentView.getParent();
        if (parentView != null) {
            parentView.removeView(fragmentView);
        }
        return fragmentView;
    }

    @Override
    public void onReveal() {
        super.onReveal();
        parentActivity.toolbar_title.setText("登记经营信息");
        parentActivity.toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentActivity.showFragment(parentActivity.fragments.get(5));
            }
        });
    }

    /**
     * 得到地址
     */
    @SuppressWarnings("unused")
    @OnClick(R.id.ll_get_location)
    void getLocation(View view) {
        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("正在获得地址...");
        pDialog.setCancelable(false);
        pDialog.show();
        mLocationClient.stop();
        tv_city.setText("");
        tv_location.setText("");
        mLocationClient.registerLocationListener(listener);
        mLocationClient.start();
    }

    /**
     * 显示地址列表
     */
    @SuppressWarnings("unused")
    @OnClick(R.id.ll_location)
    void showLocationList(View view) {
        parentActivity.showFragment(parentActivity.fragments.get(9));
    }

    /**
     * 下一步
     */
    @SuppressWarnings("unused")
    @OnClick(R.id.btn_next_step)
    void nextStep(View view) {
        String city = tv_city.getText().toString().trim();
        String address = tv_location.getText().toString().trim();
        String bill = edt_bill.getText().toString().trim();
        if (city.isEmpty() || address.isEmpty()) {
            Toast.makeText(getActivity(), "请获得地址", Toast.LENGTH_SHORT).show();
            return;
        }
        if (bill.isEmpty()) {
            Toast.makeText(getActivity(), "请输入票据", Toast.LENGTH_SHORT).show();
            return;
        }
        parentActivity.city = city;
        parentActivity.address = address;
        parentActivity.bill = bill;
        parentActivity.showFragment(parentActivity.fragments.get(7));
    }
}