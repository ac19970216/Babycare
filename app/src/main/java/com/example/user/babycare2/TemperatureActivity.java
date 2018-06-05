package com.example.user.babycare2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
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
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import at.grabner.circleprogress.CircleProgressView;


public class TemperatureActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    ColorArcProgressBar progressbar;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mToggle;
    Toolbar mToolboar;
    NavigationView navigationView;
    TextView Temperature;

    int i =0 ;
    int j =0;
    float floatnum = 0.0f;
    float floatTemp = 0.0f;

    class Data{
        Temperature[] temperature;
        class Temperature{
            String Time;
            String Temp;
            String TempEnvir;
            String TempFace;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        Temperature = (TextView) findViewById(R.id.Temprate);
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

                Request requset = new Request.Builder().url("http://192.168.100.7/get_temperature.php/").build();

                Call call = mOkHttpClient.newCall(requset);

                call.enqueue(new com.squareup.okhttp.Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {

                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        Gson gson = new Gson();
                        Data data = gson.fromJson(response.body().string(),Data.class);

                        String[] list_item = new String[data.temperature.length];
                        for(int i=0;i<data.temperature.length;i++){
                            list_item[i]=new String();
                            list_item[i]+="\n時間:"+data.temperature[i].Time;
                            list_item[i]+="\n溫度:"+data.temperature[i].Temp;
                            list_item[i]+="\n環境溫度:"+data.temperature[i].TempEnvir;
                            list_item[i]+="\n臉溫度:"+data.temperature[i].TempFace;
                        }
                        String[] strArr1=data.temperature[data.temperature.length-1].Temp.split("[.]");
                        floatTemp =Float.parseFloat(data.temperature[data.temperature.length-1].Temp);
                        floatnum = (floatTemp % 1);
                        if(floatnum !=0.0f)
                        {
                            i = Integer.parseInt(strArr1[0]);
                            j = Integer.parseInt(strArr1[1]);
                        }


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(floatnum ==0.0f)
                                {
                                    progressbar = (ColorArcProgressBar) findViewById(R.id.bar1);
                                    progressbar.setCurrentValues(floatTemp);
                                    progressbar.setUnit("°C");
                                }
                                else{
                                    progressbar = (ColorArcProgressBar) findViewById(R.id.bar1);
                                    progressbar.setCurrentValues(i);
                                    progressbar.setUnit("."+j+"°C");
                                }


                            }
                        });

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
                        final float[] Temprate = new float[data.temperature.length];
                        for(int i =0 ; i<data.temperature.length;i++)
                        {
                            Temprate[i]=Float.parseFloat(data.temperature[i].Temp);
                            //System.out.println(HRrate[i]);
                        }
                        for (int i = 0; i < 10; i++) {
                            entries.add(new Entry(i+1, Temprate[(data.temperature.length)-10+i]));
                        }
                        //一个LineDataSet就是一条线
                        LineDataSet lineDataSet = new LineDataSet(entries, "體溫");
                        LineData linedata = new LineData(lineDataSet);
                        lineDataSet.setValueFormatter(new IValueFormatter() {
                            @Override
                            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                                float IValue = (float) value;
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
                        //LimitLine limitLine = new LimitLine(38,"發燒"); //得到限制线
                        //limitLine.setLineWidth(2f); //宽度
                       // limitLine.setTextSize(12f);
                        //limitLine.setTextColor(Color.BLUE);  //颜色
                       // limitLine.setLineColor(Color.RED);
                        rightYAxis.setEnabled(false);
                        leftYAxis.setAxisMinimum(10f);
                        leftYAxis.setAxisMaximum(42f);
                        xAxis.setGranularity(1f);
                        //leftYAxis.addLimitLine(limitLine); //Y轴添加限制线
                        mLineChart.invalidate();
                        ////////////////////////////////////////////////////////////////////

                        //////////////////////////////////////////////////////////////////////圖表

                        LineChart mLineChart2 = (LineChart) findViewById(R.id.lineChart2);
                        //显示边界

                        mLineChart2.setDrawBorders(true);
                        //设置数据
                        XAxis xAxis2 = mLineChart2.getXAxis();
                        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis2.setAxisMinimum(1f);
                        xAxis2.setAxisMaximum(10f);

                        YAxis leftYAxis2 = mLineChart2.getAxisLeft();
                        YAxis rightYAxis2 = mLineChart2.getAxisRight();
                        List<Entry> entries2 = new ArrayList<>();
                        final float[] TemprateEnvir = new float[data.temperature.length];
                        for(int i =0 ; i<data.temperature.length;i++)
                        {
                            TemprateEnvir[i]=Float.parseFloat(data.temperature[i].TempEnvir);
                            //System.out.println(HRrate[i]);
                        }
                        for (int i = 0; i < 10; i++) {
                            entries2.add(new Entry(i+1, TemprateEnvir[(data.temperature.length)-10+i]));
                        }
                        //一个LineDataSet就是一条线
                        LineDataSet lineDataSet2 = new LineDataSet(entries2, "室溫");
                        LineData linedata2 = new LineData(lineDataSet2);
                        lineDataSet2.setValueFormatter(new IValueFormatter() {
                            @Override
                            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                                float IValue = (float) value;
                                return String.valueOf(IValue);
                            }
                        });
                        Description description2 = new Description();
                        description2.setEnabled(false);
                        mLineChart2.setDescription(description2);
                        lineDataSet2.setValueTextSize(11f);
                        lineDataSet2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                        lineDataSet2.setDrawCircleHole(false);
                        lineDataSet2.setDrawFilled(true);
                        mLineChart2.setData(linedata2);
                        //LimitLine limitLine2 = new LimitLine(38,"發燒"); //得到限制线
                       // limitLine2.setLineWidth(2f); //宽度
                       // limitLine2.setTextSize(12f);
                       // limitLine2.setTextColor(Color.BLUE);  //颜色
                       // limitLine2.setLineColor(Color.RED);
                        rightYAxis2.setEnabled(false);
                        leftYAxis2.setAxisMinimum(10f);
                        leftYAxis2.setAxisMaximum(42f);
                        xAxis2.setGranularity(1f);
                        //leftYAxis2.addLimitLine(limitLine2); //Y轴添加限制线
                        mLineChart2.invalidate();
                        ////////////////////////////////////////////////////////////////////


                        /*//////////////////////////////////////////////////////////////////////圖表

                        LineChart mLineChart3 = (LineChart) findViewById(R.id.lineChart3);
                        //显示边界

                        mLineChart3.setDrawBorders(true);
                        //设置数据
                        XAxis xAxis3 = mLineChart3.getXAxis();
                        xAxis3.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis3.setAxisMinimum(1f);
                        xAxis3.setAxisMaximum(10f);

                        YAxis leftYAxis3 = mLineChart3.getAxisLeft();
                        YAxis rightYAxis3 = mLineChart3.getAxisRight();
                        List<Entry> entries3 = new ArrayList<>();
                        final float[] TemprateFace = new float[data.temperature.length];
                        for(int i =0 ; i<data.temperature.length;i++)
                        {
                            TemprateFace[i]=Float.parseFloat(data.temperature[i].TempFace);
                            //System.out.println(HRrate[i]);
                        }
                        for (int i = 0; i < 10; i++) {
                            entries3.add(new Entry(i+1, TemprateFace[(data.temperature.length)-10+i]));
                        }
                        //一个LineDataSet就是一条线
                        LineDataSet lineDataSet3 = new LineDataSet(entries3, "臉溫度");
                        LineData linedata3 = new LineData(lineDataSet3);
                        lineDataSet3.setValueFormatter(new IValueFormatter() {
                            @Override
                            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                                float IValue = (float) value;
                                return String.valueOf(IValue);
                            }
                        });
                        Description description3 = new Description();
                        description3.setEnabled(false);
                        mLineChart3.setDescription(description3);
                        lineDataSet3.setValueTextSize(11f);
                        lineDataSet3.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                        lineDataSet3.setDrawCircleHole(false);
                        lineDataSet3.setDrawFilled(true);
                        mLineChart3.setData(linedata3);
                       // LimitLine limitLine3 = new LimitLine(38,"發燒"); //得到限制线
                       // limitLine3.setLineWidth(2f); //宽度
                       // limitLine3.setTextSize(12f);
                      //  limitLine3.setTextColor(Color.BLUE);  //颜色
                      //  limitLine3.setLineColor(Color.RED);
                        rightYAxis3.setEnabled(false);
                        leftYAxis3.setAxisMinimum(10f);
                        leftYAxis3.setAxisMaximum(42f);
                        xAxis3.setGranularity(1f);
                      //  leftYAxis3.addLimitLine(limitLine3); //Y轴添加限制线
                        mLineChart3.invalidate();
                        ////////////////////////////////////////////////////////////////////*/


                        float temp = Float.parseFloat(data.temperature[data.temperature.length-1].Temp);
                        if(temp< 37.2 && temp>35){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Temperature.setText("體溫正常");
                                    Temperature.setTextColor(Color.parseColor("#FFFFFF"));
                                    Temperature.setBackground(getResources().getDrawable(R.drawable.shape_label_green));
                                }
                            });
                        }
                        else if(temp>= 37.2){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Temperature.setText("體溫過高");
                                    Temperature.setTextColor(Color.parseColor("#FFFFFF"));
                                    Temperature.setBackground(getResources().getDrawable(R.drawable.shape_label_red));
                                }
                            });
                        }
                        else if(temp<=35){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Temperature.setText("體溫偏低");
                                    Temperature.setTextColor(Color.parseColor("#FFFFFF"));
                                    Temperature.setBackground(getResources().getDrawable(R.drawable.shape_label_orange));
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
            //intent = new Intent(getApplicationContext(), MainActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //startActivity(intent);
            return true;
        }
        return false;
    }
}
