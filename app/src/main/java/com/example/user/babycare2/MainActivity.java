package com.example.user.babycare2;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import javax.xml.transform.Result;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolboar;
    private BroadcastReceiver myBroadcasReceiver;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mToolboar=(Toolbar)findViewById(R.id.nav_action);
        setSupportActionBar(mToolboar);//Toolbar取代原本的ActionBar

        mDrawerLayout=(DrawerLayout)findViewById(R.id.drawerLayout);
        mToggle=new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);//必須用字串資源檔
        mDrawerLayout.addDrawerListener(mToggle);//監聽選單按鈕是否被觸擊
        mToggle.syncState();//隱藏顯示箭頭返回
        //讓 ActionBar 中的返回箭號置換成 Drawer 的三條線圖示。並且把這個觸發器指定給 layDrawer 。
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);//清單觸發監聽事件


        Intent intent = new Intent(this, MyService.class);
        this.startService(intent);


    }

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

        if (id == R.id.nav_heart) {
            Intent i =new Intent();
            i.setClass(MainActivity.this, HeartRateActivity.class);
            startActivityForResult(i,0);

        }
        else if (id == R.id.nav_temperature)
        {
            Intent i =new Intent();
            i.setClass(MainActivity.this, TemperatureActivity.class);
            //startActivityForResult(i,0);
            startActivity(i);

        }
        return true;
    }
}
