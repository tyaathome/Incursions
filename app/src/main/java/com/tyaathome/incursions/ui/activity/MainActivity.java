package com.tyaathome.incursions.ui.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.tyaathome.incursions.R;
import com.tyaathome.incursions.util.CommonUtil;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private Calendar firstCalendar;
    private Calendar startCalendar;
    private Calendar endCalendar;
    // 入侵活动时间
    private static final int INCURSIONS_ACTIVITY_TIME = 7;
    // 入侵休息时间
    private static final int INCURSIONS_INACTIVITY_TIME = 12;
    private TextView tvDesc;
    private TextView tvTimeDesc;
    private TextView tvHoursDesc, tvMinutesDesc, tvSecondsDesc;
    private TextView tvHoursValue, tvMinutesValue, tvSecondsValue;
    private Disposable disposable;
    private boolean isActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViewsAndEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(disposable != null && !disposable.isDisposed()) {
            disposable.isDisposed();
        }
    }

    private void initViewsAndEvents() {
        tvDesc = findViewById(R.id.tv_desc);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/JosefinSans-Bold.ttf");
        tvDesc.setTypeface(typeface, Typeface.BOLD);
        tvTimeDesc = findViewById(R.id.tv_time_desc);
        tvHoursDesc = findViewById(R.id.tv_hours_desc);
        tvMinutesDesc = findViewById(R.id.tv_minutes_desc);
        tvSecondsDesc = findViewById(R.id.tv_seconds_desc);
        tvHoursValue = findViewById(R.id.tv_hours_value);
        tvMinutesValue = findViewById(R.id.tv_minutes_value);
        tvSecondsValue = findViewById(R.id.tv_seconds_value);

        tvHoursDesc.setTypeface(typeface);
        tvMinutesDesc.setTypeface(typeface);
        tvSecondsDesc.setTypeface(typeface);
        tvHoursValue.setTypeface(typeface);
        tvMinutesValue.setTypeface(typeface);
        tvSecondsValue.setTypeface(typeface);
    }

    private void initData() {
        firstCalendar = Calendar.getInstance();
        // 开始时间12月15日7点
        firstCalendar.set(2018, 11, 15, 7, 0, 0);
        isActivity = checkIncursins();
        updateIncursinsUI(isActivity);

        long initialDelay = 1000 - Calendar.getInstance().get(Calendar.MILLISECOND);
        Observable.interval(initialDelay, 1000L, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        MainActivity.this.disposable = disposable;
                    }
                })
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Long aLong) {
                        long diff;
                        if(isActivity) {
                            diff = endCalendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
                        } else {
                            diff = startCalendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
                        }

                        int hours, minutes, seconds;
                        if(diff > 1000) {
                            hours = (int) (diff/(60*60*1000));
                            diff = diff % (60*60*1000);
                            minutes = (int) (diff/(60*1000));
                            diff = diff % (60*1000);
                            seconds = (int) (diff / 1000d);
                        } else if(diff > 0) {
                            updateIncursinsUI(!isActivity);
                            hours = !isActivity ? INCURSIONS_ACTIVITY_TIME : INCURSIONS_INACTIVITY_TIME;
                            minutes = 0;
                            seconds = 0;
                        } else {
                            isActivity = checkIncursins();
                            if(isActivity) {
                                diff = endCalendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
                            } else {
                                diff = startCalendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
                            }
                            hours = (int) (diff/(60*60*1000));
                            diff = diff % (60*60*1000);
                            minutes = (int) (diff/(60*1000));
                            diff = diff % (60*1000);
                            seconds = (int) (diff / 1000d);
                        }
                        tvHoursValue.setText(String.valueOf(hours));
                        tvMinutesValue.setText(String.valueOf(minutes));
                        tvSecondsValue.setText(String.valueOf(seconds));

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void updateIncursinsUI(boolean isActivity) {
        if(isActivity) {
            tvDesc.setText(getString(R.string.activity));
            tvDesc.setTextColor(getResources().getColor(R.color.colorActivity));
            tvTimeDesc.setText(getString(R.string.ending));
        } else {
            tvDesc.setText(getString(R.string.inactivity));
            tvDesc.setTextColor(getResources().getColor(R.color.colorInactivity));
            tvTimeDesc.setText(getString(R.string.starting));
        }
    }

    private boolean checkIncursins() {
        Calendar now = Calendar.getInstance();
        Calendar start = firstCalendar;
        while (true) {
            Calendar end = Calendar.getInstance();
            end.setTimeInMillis(start.getTimeInMillis());
            end.add(Calendar.HOUR_OF_DAY, INCURSIONS_ACTIVITY_TIME);
            int nowYear = now.get(Calendar.YEAR);
            int startYear = start.get(Calendar.YEAR);
            int endYear = end.get(Calendar.YEAR);
            int nowDay = now.get(Calendar.DAY_OF_YEAR);
            int startDay = start.get(Calendar.DAY_OF_YEAR);
            int endDay = end.get(Calendar.DAY_OF_YEAR);
            if((nowYear == startYear || nowYear == endYear) && (nowDay == startDay || nowDay == endDay)) {
                if(now.after(start) && now.before(end)) {
                    startCalendar = CommonUtil.copyCalendar(start);
                    endCalendar = CommonUtil.copyCalendar(end);
                    return true;
                } else if(now.before(start)) {
                    startCalendar = CommonUtil.copyCalendar(start);
                    endCalendar = CommonUtil.copyCalendar(end);
                    return false;
                }
            } else if(now.before(end)){
                startCalendar = CommonUtil.copyCalendar(start);
                endCalendar = CommonUtil.copyCalendar(end);
                return false;
            }
            start = Calendar.getInstance();
            start.setTimeInMillis(end.getTimeInMillis());
            start.add(Calendar.HOUR_OF_DAY, INCURSIONS_INACTIVITY_TIME);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            //hideSystemUI();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//                getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));
//            }
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}
