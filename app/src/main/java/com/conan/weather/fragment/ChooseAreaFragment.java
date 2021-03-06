package com.conan.weather.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.conan.weather.MainActivity;
import com.conan.weather.R;
import com.conan.weather.activity.WeatherHomeActivity;
import com.conan.weather.bean.CityListBean;
import com.conan.weather.db.City;
import com.conan.weather.db.County;
import com.conan.weather.db.Province;
import com.conan.weather.utils.HttpService;
import com.conan.weather.utils.HttpUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Author        JY
 * PublishDate   17/3/27
 * Description   选择区域的fragment
 * Version       1.0
 * Updated       JY
 */
public class ChooseAreaFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTY = 2;

    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private Province selectProvince;
    private City selectCity;
    private County selectCounty;
    private int currentLevel = -1;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.lv_city)
    ListView lvCity;
    Unbinder unbinder;

    private HttpService service;
    private Observer<List<CityListBean>> observer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_area, container, false);
        unbinder = ButterKnife.bind(this, view);
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, dataList);
        lvCity.setAdapter(adapter);
        service = HttpUtil.http().create(HttpService.class);
        observer = new Observer<List<CityListBean>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<CityListBean> value) {
                switch (currentLevel) {
                    default:
                        for (int i = 0; i < value.size(); i++) {
                            Province province = new Province();
                            province.setProvinceName(value.get(i).getName());
                            province.setProvinceCode(value.get(i).getId());
                            province.save();
                        }
                        queryProvinces();
                        break;
                    case LEVEL_PROVINCE:
                        for (int i = 0; i < value.size(); i++) {
                            City city = new City();
                            city.setCityName(value.get(i).getName());
                            city.setCityCode(value.get(i).getId());
                            city.setProvinceId(selectProvince.getProvinceCode());
                            city.save();
                        }
                        queryCities();
                        break;
                    case LEVEL_CITY:
                        for (int i = 0; i < value.size(); i++) {
                            County county = new County();
                            county.setCountyName(value.get(i).getName());
                            county.setWeatherId(value.get(i).getWeather_id());
                            county.setCityId(selectCity.getCityCode());
                            county.save();
                        }
                        queryCounties();
                        break;
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {

            }
        };
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        queryProvinces();
        lvCity.setOnItemClickListener(this);
    }

    private void queryProvinces() {
        tvTitle.setText("中国");
        imgBack.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            lvCity.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            service.getProvince()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer);
        }
    }

    private void queryCities() {
        tvTitle.setText(selectProvince.getProvinceName());
        imgBack.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceId = ?", String.valueOf(selectProvince.getProvinceCode())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            lvCity.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            service.getCity(selectProvince.getProvinceCode())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer);
        }
    }

    private void queryCounties() {
        tvTitle.setText(selectCity.getCityName());
        imgBack.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityId = ?", String.valueOf(selectCity.getCityCode())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            lvCity.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            service.getCounty(selectProvince.getProvinceCode(), selectCity.getCityCode())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.img_back)
    public void onViewClicked() {
        backClick();
    }

    private void backClick() {
        if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {
            getActivity().finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (currentLevel) {
            case LEVEL_PROVINCE:
                selectProvince = provinceList.get(position);
                queryCities();
                break;
            case LEVEL_CITY:
                selectCity = cityList.get(position);
                queryCounties();
                break;
            case LEVEL_COUNTY:
                selectCounty = countyList.get(position);
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE).edit();
                editor.putString("weatherId", selectCounty.getWeatherId());
                editor.apply();
                if (getActivity() instanceof MainActivity) {
                    WeatherHomeActivity.instance(getActivity(), selectCounty.getWeatherId());
                    getActivity().finish();
                } else if (getActivity() instanceof WeatherHomeActivity) {
                    WeatherHomeActivity activity = (WeatherHomeActivity) getActivity();
                    activity.drawerLayout.closeDrawers();
                    activity.refreshLayout.setRefreshing(true);
                    activity.requestWeather(selectCounty.getWeatherId());
                }
                break;
            default:
                break;
        }
    }
}
