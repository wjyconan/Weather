package com.conan.weather.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.conan.weather.R;
import com.conan.weather.bean.WeatherBean;
import com.conan.weather.utils.HttpService;
import com.conan.weather.utils.HttpUtil;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 *
 */
public class WeatherHomeActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static String WEATHER_ID = "weatherId";

    @BindView(R.id.txt_title_city)
    TextView txtTitleCity;
    @BindView(R.id.txt_title_update_time)
    TextView txtTitleUpdateTime;
    @BindView(R.id.txt_degree)
    TextView txtDegree;
    @BindView(R.id.txt_weather_info)
    TextView txtWeatherInfo;
    @BindView(R.id.llayout)
    LinearLayout llayout;
    @BindView(R.id.txt_aqi)
    TextView txtAqi;
    @BindView(R.id.txt_pm25)
    TextView txtPm25;
    @BindView(R.id.txt_comfort)
    TextView txtComfort;
    @BindView(R.id.txt_car_wash)
    TextView txtCarWash;
    @BindView(R.id.txt_sport)
    TextView txtSport;
    @BindView(R.id.llayout_main)
    LinearLayout llayoutMain;
    @BindView(R.id.img_bing_pic)
    ImageView imgBingPic;
    @BindView(R.id.refresh_layout)
    public SwipeRefreshLayout refreshLayout;
    @BindView(R.id.drawer_layout)
    public DrawerLayout drawerLayout;

    private String weatherId;
    private HttpService service;
    private CompositeDisposable disposable = new CompositeDisposable();

    public static void instance(Context context, String weatherId) {
        Intent intent = new Intent(context, WeatherHomeActivity.class);
        intent.putExtra(WEATHER_ID, weatherId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_home);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        llayoutMain.setVisibility(View.GONE);
        requestWeather(getIntent().getStringExtra(WEATHER_ID));
        service = HttpUtil.httpString().create(HttpService.class);
        Observable<String> observable = service.getBingPic();
        observable.subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(String value) {
                        Glide.with(WeatherHomeActivity.this).load(value).into(imgBingPic);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(WeatherHomeActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
    }

    public void requestWeather(String weatherId) {
        this.weatherId = weatherId;
        service = HttpUtil.http().create(HttpService.class);
        Observable<WeatherBean> observable = service.getWeather(weatherId);
        observable.subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<WeatherBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(WeatherBean value) {
                        WeatherBean.HeWeatherBean heWeatherBean = value.getHeWeather().get(0);
                        if (Objects.equals("ok", heWeatherBean.getStatus())) {
                            txtTitleCity.setText(heWeatherBean.getBasic().getCity());
                            txtTitleUpdateTime.setText(heWeatherBean.getBasic().getUpdate().getLoc().split(" ")[1]);
                            txtDegree.setText(heWeatherBean.getNow().getTmp() + "℃");
                            txtWeatherInfo.setText(heWeatherBean.getNow().getCond().getTxt());
                            List<WeatherBean.HeWeatherBean.DailyForecastBean> daily_forecast = heWeatherBean.getDaily_forecast();
                            llayout.removeAllViews();
                            for (WeatherBean.HeWeatherBean.DailyForecastBean forecastBean : daily_forecast) {
                                View view = LayoutInflater.from(WeatherHomeActivity.this).inflate(R.layout.activity_home_forecast_item, llayout, false);
                                TextView txtDate = (TextView) view.findViewById(R.id.txt_date);
                                TextView txtInfo = (TextView) view.findViewById(R.id.txt_info);
                                TextView txtMax = (TextView) view.findViewById(R.id.txt_max);
                                TextView txtMin = (TextView) view.findViewById(R.id.txt_min);
                                txtDate.setText(forecastBean.getDate());
                                txtInfo.setText(forecastBean.getCond().getTxt_d());
                                txtMax.setText(forecastBean.getTmp().getMax() + "℃");
                                txtMin.setText(forecastBean.getTmp().getMin() + "℃");
                                llayout.addView(view);
                            }
                            if (heWeatherBean.getAqi() != null) {
                                txtAqi.setText(heWeatherBean.getAqi().getCity().getAqi());
                                txtPm25.setText(heWeatherBean.getAqi().getCity().getPm25());
                            } else {
                                txtAqi.setText("--");
                                txtPm25.setText("--");
                            }
                            txtComfort.setText("舒适度：" + heWeatherBean.getSuggestion().getComf().getTxt());
                            txtCarWash.setText("洗车指数：" + heWeatherBean.getSuggestion().getCw().getTxt());
                            txtSport.setText("运动建议：" + heWeatherBean.getSuggestion().getSport().getTxt());
                            llayoutMain.setVisibility(View.VISIBLE);
                            refreshLayout.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getApplication(), e.toString(), Toast.LENGTH_LONG).show();
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onRefresh() {
        requestWeather(weatherId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}
