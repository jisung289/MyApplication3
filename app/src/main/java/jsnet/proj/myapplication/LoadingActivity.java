package jsnet.proj.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2016-06-10.
 */
public class LoadingActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Context context = getBaseContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        initialize();

        LinearLayout view = (LinearLayout) findViewById(R.id.load_view);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.loading_anim);
        view.startAnimation(animation);
    }

    private void initialize() {
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                finish();
            }
        };

        handler.sendEmptyMessageDelayed(0, 3000);
        //handler.sendEmptyMessageDelayed(0, 3);
    }


}