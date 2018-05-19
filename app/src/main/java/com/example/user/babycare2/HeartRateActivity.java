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


public class HeartRateActivity extends AppCompatActivity{

    TextView maxHeartRateNum;
    TextView minHeartRateNum;
    TextView averageHeartRateNum;
    ListView HeartListView;


    ArrayAdapter<String> MyArrayAdapter;

    int max=0;
    int min=1000;
    float average=0;
    int repeat =0;

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

        maxHeartRateNum = (TextView) findViewById(R.id.maxheartRateNum);
        minHeartRateNum = (TextView) findViewById(R.id.minheartRateNum);
        averageHeartRateNum = (TextView) findViewById(R.id.averageheartRateNum);
        HeartListView = (ListView) findViewById(R.id.listView);

        MyArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        HeartListView.setAdapter(MyArrayAdapter);


        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                Message message=new Message();
                message.what=0;
                mHandler.sendMessage(message);
            }
        }, 500, 1000);

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
                        LineDataSet lineDataSet = new LineDataSet(entries, "心律");
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
                        mLineChart.setData(linedata);
                        LimitLine limitLine = new LimitLine(135,"高心律警示線"); //得到限制线
                        limitLine.setLineWidth(4f); //宽度
                        limitLine.setTextSize(12f);
                        limitLine.setTextColor(Color.BLUE);  //颜色
                        limitLine.setLineColor(Color.RED);
                        rightYAxis.setEnabled(false);
                        leftYAxis.setAxisMinimum(40f);
                        leftYAxis.setAxisMaximum(160f);
                        xAxis.setGranularity(1f);
                        leftYAxis.addLimitLine(limitLine); //Y轴添加限制线
                        mLineChart.invalidate();
                        ////////////////////////////////////////////////////////////////////

                        for(int i=0;i<10;i++)
                        {
                            if(HRrate[(data.hr.length)-10+i]>max)
                                max=HRrate[(data.hr.length)-10+i];
                        }
                        for(int i=0;i<10;i++)
                        {
                            if(HRrate[(data.hr.length)-10+i]<min)
                                min=HRrate[(data.hr.length)-10+i];
                        }
                        for(int i=0;i<10;i++)
                        {
                            average+=HRrate[(data.hr.length)-10+i];
                        }
                        average=average/10;
                        runOnUiThread(new Runnable(){
                            public void run() {
                                String minNum = Integer.toString(min);
                                String maxNum = Integer.toString(max);
                                String averageNum = Float.toString(average);
                                maxHeartRateNum.setText(maxNum);
                                minHeartRateNum.setText(minNum);
                                averageHeartRateNum.setText(averageNum);
                                average=0;
                            }});
                        if(HRrate[(data.hr.length)-1]>135 && HRrate[(data.hr.length)-1] != repeat)
                        {
                            repeat=HRrate[(data.hr.length)-1];
                            final String WarningHeartRate = "在"+data.hr[(data.hr.length)-1].Time+"偵測到"+data.hr[(data.hr.length)-1].HR+"的心律";
                            runOnUiThread(new Runnable(){
                                public void run() {
                                    MyArrayAdapter.add(WarningHeartRate);
                                    MyArrayAdapter.notifyDataSetChanged();
                                }});

                        }
                    }
                });
            }
        }
    };


}
