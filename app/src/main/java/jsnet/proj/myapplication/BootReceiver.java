package jsnet.proj.myapplication;

/**
 * Created by Administrator on 2016-08-08.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Administrator on 14. 3. 10.
 */
public class BootReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent){
        //  Intent i=new Intent(context, main.class);
        //  i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        //  context.startService(new Intent(context, MyFcmListenerService.class));

        /* 서비스 죽일때 알람으로 다시 서비스 등록 */
        if(intent.getAction().equals("ACTION.RESTART.MyFcmListenerService")){
            Log.d("RestartService", "ACTION_RESTART_PERSISTENTSERVICE");
            Intent ia = new Intent(context,MyFcmListenerService.class);
            context.startService(ia);
        }
        /* 폰 재부팅할때 서비스 등록 */
        if (intent.getAction().equals(intent.ACTION_BOOT_COMPLETED)) {
            Log.d("RestartService", "ACTION_BOOT_COMPLETED");
            Intent ib = new Intent(context,MyFcmListenerService.class);
            context.startService(ib);
        }


    }
}