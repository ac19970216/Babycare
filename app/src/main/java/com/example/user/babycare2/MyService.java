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
    int countmun =1;

    class Data{
        babywarning[] babywarning;
        class babywarning{
            String Time;
            String warning;
        }
    }
    
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
        }, 500, 1500);


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

                Request requset = new Request.Builder().url("http://192.168.100.7/get_warning.php/").build();

                Call call = mOkHttpClient.newCall(requset);

                call.enqueue(new com.squareup.okhttp.Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {

                    }
                    @TargetApi(Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(Response response) throws IOException {
                        Gson gson = new Gson();
                        Data data = gson.fromJson(response.body().string(), Data.class);

                        String[] list_item = new String[data.babywarning.length];
                        for (int i = 0; i < data.babywarning.length; i++) {
                            list_item[i] = new String();
                            list_item[i] += "\n時間:" + data.babywarning[i].Time;
                            list_item[i] += "\n警告:" + data.babywarning[i].warning;
                        }
                        int[] Warning = new int[data.babywarning.length];
                        for(int i =0 ; i<data.babywarning.length;i++)
                        {
                            Warning[i]=Integer.parseInt(data.babywarning[i].warning);
                            //System.out.println(HRrate[i]);
                        }
                        if( countmun ==1){
                            repeat = Warning[(data.babywarning.length)-1];
                            countmun++;
                        }

                        if(Warning[(data.babywarning.length)-1] == 1 && Warning[(data.babywarning.length)-1] != repeat){
                            repeat = Warning[(data.babywarning.length)-1];
                            Intent intent = new Intent(MyService.this, HeartRateActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this,0, intent, 0);

                            Notification.Builder mBuilder = new Notification.Builder(MyService.this,"HeartRateNotification")
                                    .setSmallIcon(R.drawable.ic_report_problem_black_24dp)
                                    .setContentTitle("警告")
                                    .setContentText("偵測到時間"+data.babywarning[(data.babywarning.length)-1].Time+"寶寶的心率過低!")
                                    //.setPriority(Notification.PRIORITY_DEFAULT)
                                    // Set the intent that will fire when the user taps the notification
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true);
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MyService.this);
// notificationId is a unique int for each notification that you must define
                            createNotificationChannel();   //創建Notification Channel
                            notificationManager.notify(1, mBuilder.build());
                        }
                        else if(Warning[(data.babywarning.length)-1] == 2 && Warning[(data.babywarning.length)-1] != repeat){
                            repeat = Warning[(data.babywarning.length)-1];
                            Intent intent = new Intent(MyService.this, HeartRateActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this,0, intent, 0);

                            Notification.Builder mBuilder = new Notification.Builder(MyService.this,"HeartRateNotification")
                                    .setSmallIcon(R.drawable.ic_report_problem_black_24dp)
                                    .setContentTitle("警告")
                                    .setContentText("偵測到時間"+data.babywarning[(data.babywarning.length)-1].Time+"寶寶的心率過高!")
                                    //.setPriority(Notification.PRIORITY_DEFAULT)
                                    // Set the intent that will fire when the user taps the notification
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true);
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MyService.this);
// notificationId is a unique int for each notification that you must define
                            createNotificationChannel();   //創建Notification Channel
                            notificationManager.notify(1, mBuilder.build());
                        }
                        else if(Warning[(data.babywarning.length)-1] == 3 && Warning[(data.babywarning.length)-1] != repeat){
                            repeat = Warning[(data.babywarning.length)-1];
                            Intent intent = new Intent(MyService.this, TemperatureActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this,0, intent, 0);

                            Notification.Builder mBuilder = new Notification.Builder(MyService.this,"HeartRateNotification")
                                    .setSmallIcon(R.drawable.ic_report_problem_black_24dp)
                                    .setContentTitle("警告")
                                    .setContentText("偵測到時間"+data.babywarning[(data.babywarning.length)-1].Time+"寶寶的體溫過低!")
                                    //.setPriority(Notification.PRIORITY_DEFAULT)
                                    // Set the intent that will fire when the user taps the notification
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true);
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MyService.this);
// notificationId is a unique int for each notification that you must define
                            createNotificationChannel();   //創建Notification Channel
                            notificationManager.notify(1, mBuilder.build());
                        }
                        else if(Warning[(data.babywarning.length)-1] == 4 && Warning[(data.babywarning.length)-1] != repeat){
                            repeat = Warning[(data.babywarning.length)-1];
                            Intent intent = new Intent(MyService.this, TemperatureActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this,0, intent, 0);

                            Notification.Builder mBuilder = new Notification.Builder(MyService.this,"HeartRateNotification")
                                    .setSmallIcon(R.drawable.ic_report_problem_black_24dp)
                                    .setContentTitle("警告")
                                    .setContentText("偵測到時間"+data.babywarning[(data.babywarning.length)-1].Time+"寶寶可能發燒!")
                                    //.setPriority(Notification.PRIORITY_DEFAULT)
                                    // Set the intent that will fire when the user taps the notification
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true);
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MyService.this);
// notificationId is a unique int for each notification that you must define
                            createNotificationChannel();   //創建Notification Channel
                            notificationManager.notify(1, mBuilder.build());
                        }
                        else if(Warning[(data.babywarning.length)-1] == 5 && Warning[(data.babywarning.length)-1] != repeat){
                            repeat = Warning[(data.babywarning.length)-1];
                            Intent intent = new Intent(MyService.this, HeartRateActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this,0, intent, 0);

                            Notification.Builder mBuilder = new Notification.Builder(MyService.this,"HeartRateNotification")
                                    .setSmallIcon(R.drawable.ic_report_problem_black_24dp)
                                    .setContentTitle("警告")
                                    .setContentText("偵測到時間"+data.babywarning[(data.babywarning.length)-1].Time+"寶寶可能在哭鬧中，已啟用安撫功能但仍然無效，請檢察寶寶狀況!")
                                    //.setPriority(Notification.PRIORITY_DEFAULT)
                                    // Set the intent that will fire when the user taps the notification
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true);
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MyService.this);
// notificationId is a unique int for each notification that you must define
                            createNotificationChannel();   //創建Notification Channel
                            notificationManager.notify(1, mBuilder.build());

                        }
                        else if(Warning[(data.babywarning.length)-1] == 0)
                        {
                            repeat = Warning[(data.babywarning.length)-1];
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
            NotificationChannel channel = new NotificationChannel("HeartRateNotification", "警告", importance);
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
