package com.example.user.babycare2;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.gson.Gson;
import com.shinelw.library.ColorArcProgressBar;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class HeartRateActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mToggle;
    Toolbar mToolboar;
    NavigationView navigationView;
    ColorArcProgressBar progressbar;
    TextView HeartrateHappen;

    int progressbarHeartrate=0;


    class Data{
        Hr[] hr;
        class Hr{
            String Time;
            String HR;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);

        HeartrateHappen = (TextView) findViewById(R.id.Heartratehappen);
        mToolboar=(Toolbar)findViewById(R.id.nav_action);
        setSupportActionBar(mToolboar);//Toolbar取代原本的ActionBar
        mDrawerLayout=(DrawerLayout)findViewById(R.id.drawerLayout);
        mToggle=new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);//必須用字串資源檔
        mDrawerLayout.addDrawerListener(mToggle);//監聽選單按鈕是否被觸擊
        mToggle.syncState();//隱藏顯示箭頭返回
        //讓 ActionBar 中的返回箭號置換成 Drawer 的三條線圖示。並且把這個觸發器指定給 layDrawer 。
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);//清單觸發監聽事件
        progressbar = (ColorArcProgressBar) findViewById(R.id.bar1);





        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                Message message=new Message();
                message.what=0;
                mHandler.sendMessage(message);
            }
        }, 500, 2000);

    }



    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            // TODO Auto-generated method stub
            if(msg.what == 0){

                //这里可以进行UI操作，如Toast，Dialog等
                OkHttpClient mOkHttpClient = new OkHttpClient();

                Request requset = new Request.Builder().url("http://192.168.100.7/get_Heartrate.php/").build();

                Call call = mOkHttpClient.newCall(requset);

                call.enqueue(new com.squareup.okhttp.Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {

                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        Gson gson = new Gson();
                        Data data = gson.fromJson(response.body().string(),Data.class);

                        String[] list_item = new String[data.hr.length];
                        for(int i=0;i<data.hr.length;i++){
                            list_item[i]=new String();
                            list_item[i]+="\n時間:"+data.hr[i].Time;
                            list_item[i]+="\n心跳:"+data.hr[i].HR;
                        }



                        //////////////////////////////////////////////////////////////////////圖表

                        LineChart mLineChart = (LineChart) findViewById(R.id.lineChart);
                        //显示边界

                        mLineChart.setDrawBorders(true);
                        //设置数据
                        XAxis xAxis = mLineChart.getXAxis();
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setAxisMinimum(1f);
                        xAxis.setAxisMaximum(10f);

                        YAxis leftYAxis = mLineChart.getAxisLeft();
                        YAxis rightYAxis = mLineChart.getAxisRight();
                        List<Entry> entries = new ArrayList<>();
                        int[] HRrate = new int[data.hr.length];
                        for(int i =0 ; i<data.hr.length;i++)
                        {
                            HRrate[i]=Integer.parseInt(data.hr[i].HR);
                            //System.out.println(HRrate[i]);
                        }
                        for (int i = 0; i < 10; i++) {
                            entries.add(new Entry(i+1, HRrate[(data.hr.length)-10+i]));
                        }
                        //一个LineDataSet就是一条线
                        LineDataSet lineDataSet = new LineDataSet(entries, "心率");
                        LineData linedata = new LineData(lineDataSet);
                        lineDataSet.setValueFormatter(new IValueFormatter() {
                            @Override
                            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                                int IValue = (int) value;
                                return String.valueOf(IValue);
                            }
                        });
                        Description description = new Description();
                        description.setEnabled(false);
                        mLineChart.setDescription(description);
                        lineDataSet.setValueTextSize(11f);
                        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                        lineDataSet.setDrawCircleHole(false);
                        lineDataSet.setDrawFilled(true);
                        mLineChart.setData(linedata);

                        rightYAxis.setEnabled(false);
                        leftYAxis.setAxisMinimum(80f);
                        leftYAxis.setAxisMaximum(180f);
                        xAxis.setGranularity(1f);

                        mLineChart.invalidate();
                        ////////////////////////////////////////////////////////////////////
                        progressbarHeartrate =HRrate[(data.hr.length)-1];

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressbar.setCurrentValues(progressbarHeartrate);

                            }
                        });

                        if(progressbarHeartrate>= 150){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    HeartrateHappen.setText("心率偏高");
                                    HeartrateHappen.setTextColor(Color.parseColor("#FFFFFF"));
                                    HeartrateHappen.setBackground(getResources().getDrawable(R.drawable.shape_label_red));
                                }
                            });
                        }
                        else if(progressbarHeartrate<150 && progressbarHeartrate>110){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    HeartrateHappen.setText("心率正常");
                                    HeartrateHappen.setTextColor(Color.parseColor("#FFFFFF"));
                                    HeartrateHappen.setBackground(getResources().getDrawable(R.drawable.shape_label_green));
                                }
                            });
                        }
                        else if(progressbarHeartrate<110){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    HeartrateHappen.setText("心率偏低");
                                    HeartrateHappen.setTextColor(Color.parseColor("#FFFFFF"));
                                    HeartrateHappen.setBackground(getResources().getDrawable(R.drawable.shape_label_orange));
                                }
                            });
                        }


                    }
                });
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){//當按下左上三條線或顯示工具列
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        if (id == R.id.nav_heart) {
            intent = new Intent(getApplicationContext(), HeartRateActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            //Intent i =new Intent();
            //i.setClass(MainActivity.this, HeartRateActivity.class);
            // startActivityForResult(i,0);

        }
        else if (id == R.id.nav_temperature)
        {
            intent = new Intent(getApplicationContext(), TemperatureActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            //Intent i =new Intent();
            //i.setClass(MainActivity.this, TemperatureActivity.class);
            //startActivityForResult(i,0);
            //startActivity(i);

        }
        mDrawerLayout.closeDrawers();
        return true;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent;
        if(keyCode == KeyEvent.KEYCODE_BACK ){
            finish();
            //intent = new Intent(this, MainActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //startActivity(intent);
            return true;
        }
        return false;
    }


}
