package com.example.user.babycare2;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;

import com.google.gson.Gson;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {
    int repeat=0;
    int countHeartrate =0;
    
    public void onCreate(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                Message message=new Message();
                message.what=0;
                mHandler.sendMessage(message);
            }
        }, 500, 5000);


    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            // TODO Auto-generated method stub

            /*notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            final PendingIntent appIntent
                    = PendingIntent.getActivity(MyService.this, 0, notifyIntent, 0);*/


            if (msg.what == 0) {
                OkHttpClient mOkHttpClient = new OkHttpClient();

                Request requset = new Request.Builder().url("http://192.168.100.7/get_Heartrate.php/").build();

                Call call = mOkHttpClient.newCall(requset);

                call.enqueue(new com.squareup.okhttp.Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {

                    }
                    @TargetApi(Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(Response response) throws IOException {
                        Gson gson = new Gson();
                        HeartRateActivity.Data data = gson.fromJson(response.body().string(), HeartRateActivity.Data.class);

                        String[] list_item = new String[data.hr.length];
                        for (int i = 0; i < data.hr.length; i++) {
                            list_item[i] = new String();
                            list_item[i] += "\n時間:" + data.hr[i].Time;
                            list_item[i] += "\n心跳:" + data.hr[i].HR;
                        }

                        int[] HRrate = new int[data.hr.length];
                        for (int i = 0; i < data.hr.length; i++) {
                            HRrate[i] = Integer.parseInt(data.hr[i].HR);
                        }

                        if(HRrate[(data.hr.length)-1]>160 && HRrate[(data.hr.length)-1] != repeat && countHeartrate==0){
                            repeat=HRrate[(data.hr.length)-1];
                            countHeartrate = 10;
                            //System.out.println(HRrate[(data.hr.length)-1]);

                            Intent intent = new Intent(MyService.this, HeartRateActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this,0, intent, 0);

                            Notification.Builder mBuilder = new Notification.Builder(MyService.this,"HeartRateNotification")
                                    .setSmallIcon(R.drawable.ic_report_problem_black_24dp)
                                    .setContentTitle("警告")
                                    .setContentText("寶寶心率可能有異常!")
                                    //.setPriority(Notification.PRIORITY_DEFAULT)
                                    // Set the intent that will fire when the user taps the notification
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true);
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MyService.this);

// notificationId is a unique int for each notification that you must define
                            createNotificationChannel();   //創建Notification Channel

                            notificationManager.notify(1, mBuilder.build());

                        }
                        else{
                            countHeartrate--;
                            if(countHeartrate <=0){
                                countHeartrate=0;
                            }
                        }

                    }
                });
            }
        }
    };

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //CharSequence name = getString(R.string.channel_name);
            //String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("HeartRateNotification", "心率警告", importance);
            //channel.setDescription("and you?");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
