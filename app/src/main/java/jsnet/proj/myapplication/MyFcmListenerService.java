package jsnet.proj.myapplication;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2016-06-10.
 */
public class MyFcmListenerService extends FirebaseMessagingService {
    Intent intent;;
    public void onCreate() {
        unregisterRestartAlarm();                                                 //이미 등록된 알람이 있으면 제거
        super.onCreate();
    }


    private boolean serviceRunning = false;
    private NotificationManager mNotificationManager = null;
    @Override
    public void onMessageReceived(RemoteMessage message){
        String from = message.getFrom();
        Map<String, String> data = message.getData();
        String title = data.get("title");
        String msg = data.get("message");
        String url = data.get("url");
        String grade = data.get("grade");

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);

        SharedPreferences.Editor editor_new = pref.edit();
        editor_new.putString("new_url", url);
        editor_new.putString("new_grade", grade);
        editor_new.commit();
        //    Toast.makeTex

            onBtnNotification(title, msg, url,grade);

            Intent intent = new Intent("new_event");
            intent.putExtra("url", url);
            intent.putExtra("grade", grade);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            SharedPreferences bpref = getSharedPreferences("bpref", MODE_PRIVATE);
            String b_count = bpref.getString("b_count", "");


            if (b_count == "") {
                b_count = "0";
            }
            int numInt = Integer.parseInt(b_count);
            numInt = numInt + 1;
            String b_count2 = String.valueOf(numInt);

            SharedPreferences.Editor editor = bpref.edit();
            editor.putString("b_count", b_count2);
            editor.commit();

            updateIconBadgeCount(this, numInt);
        // 전달 받은 정보로 뭔가를 하면 된다.
    }
    public void onBtnNotification(String title, String msg, String url, String grade) {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String pause=pref.getString("pause", "");
        String push=pref.getString("push_flag", "");
        if(push.equals("N")){
            return;
        }


        Intent aint = new Intent(getApplicationContext(), Mainviewpage.class);
        aint.putExtra("url", url);
        aint.putExtra("grade", grade);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, aint, FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder builder = new Notification.Builder(this, "news");
            // 작은 아이콘 이미지.


// 작은 아이콘 이미지.
            builder.setSmallIcon(R.drawable.icon);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon));

// 알림이 출력될 때 상단에 나오는 문구.
            builder.setTicker(title);

// 알림 출력 시간.
            builder.setWhen(System.currentTimeMillis());

// 알림 제목.
            builder.setContentTitle(title);


// 알림 내용.
            builder.setContentText(msg);

// 알림시 사운드, 진동, 불빛을 설정 가능.
            builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);


// 알림 터치시 반응.
            builder.setContentIntent(pendingIntent);

// 알림 터치시 반응 후 알림 삭제 여부.
            builder.setAutoCancel(true);

// 우선순위.


// 고유ID로 알림을 생성.
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(123456);
            nm.notify(123456, builder.build());




        }else{


            Notification.Builder builder = new Notification.Builder(this);

// 작은 아이콘 이미지.
            builder.setSmallIcon(R.drawable.icon);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon));

// 알림이 출력될 때 상단에 나오는 문구.
            builder.setTicker(title);

// 알림 출력 시간.
            builder.setWhen(System.currentTimeMillis());

// 알림 제목.
            builder.setContentTitle(title);


// 알림 내용.
            builder.setContentText(msg);

// 알림시 사운드, 진동, 불빛을 설정 가능.
            builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

// 알림 터치시 반응.
            builder.setContentIntent(pendingIntent);

// 알림 터치시 반응 후 알림 삭제 여부.
            builder.setAutoCancel(true);

// 우선순위.


// 고유ID로 알림을 생성.
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(123456);
            nm.notify(123456, builder.build());




        }
    }

    @Override
    public void onDestroy() {
        registerRestartAlarm();                                                   // 서비스가 죽을때 알람을 등록
        super.onDestroy();
    }

    // support persistent of Service
    public void registerRestartAlarm() {

        Random rnd = new Random();

        int p = rnd.nextInt(1000);




        Intent intent = new Intent(MyFcmListenerService.this, BootReceiver.class);
        intent.setAction("ACTION.RESTART.MyFcmListenerService");
        PendingIntent sender = PendingIntent.getBroadcast(MyFcmListenerService.this, p, intent, FLAG_UPDATE_CURRENT);
        long firstTime = SystemClock.elapsedRealtime();
        firstTime += 10*1000;                                               // 10초 후에 알람이벤트 발생
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 10*1000, sender);
    }
    public void unregisterRestartAlarm() {

        Random rnd = new Random();

        int p = rnd.nextInt(1000);


        Intent intent = new Intent(MyFcmListenerService.this, BootReceiver.class);
        intent.setAction("ACTION.RESTART.MyFcmListenerService");
        PendingIntent sender = PendingIntent.getBroadcast(MyFcmListenerService.this, p, intent, FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.cancel(sender);
    }


    public void updateIconBadgeCount(Context context, int count) {

        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");

        // Component를 정의
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", getLauncherClassName(context));

        // 카운트를 넣어준다.
        intent.putExtra("badge_count", count);

        // Version이 3.1이상일 경우에는 Flags를 설정하여 준다.
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            intent.setFlags(0x00000020);
        }

        // send
        sendBroadcast(intent);
    }

    private String getLauncherClassName(Context context) {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(getPackageName());

        List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(intent, 0);
        if(resolveInfoList != null && resolveInfoList.size() > 0) {
            Log.d("test", "success");
            return resolveInfoList.get(0).activityInfo.name;

        }
        Log.d("test", "Fail");
        return "";
    }


}