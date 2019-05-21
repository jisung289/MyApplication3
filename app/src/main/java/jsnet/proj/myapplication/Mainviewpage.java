package jsnet.proj.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


/**
 * Created by Administrator on 2017-06-14.
 */

public class Mainviewpage extends AppCompatActivity implements ActionBar.TabListener {
    public static ViewPager vp;
    FragmentStatePagerAdapter pagerAdapter;


    private Activity Mainviewpage = this;



    private String token;

    private Intent intent;
    private String temp_key;
    public static Activity AActivity;
    private Fragment targetFragment;
    final static String FRAGMENT_TAG = "FRAGMENTB_TAG";

    private ImageView badge_new_chat;
    private TextView title_bar;
    private ImageView btn1;
    private ImageView btn2;
    private ImageView btn3;
    private ImageView btn4;
    private ImageView btn5;
    private Context context;
    final static int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 100;
    final static int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 200;

    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    @Override


    protected void onCreate(Bundle savedInstanceState)
    {


        context = getApplicationContext();
        SharedPreferences preferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        temp_key=preferences.getString("temp_key", "");
        if(temp_key=="") {

            Intent intent = new Intent(
                    context, // 현재화면의 제어권자
                    Login.class); // 다음넘어갈 화면


            context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));

            Intent intent2 = new Intent(
                    context, // 현재화면의 제어권자
                    LoadingActivity.class); // 다음넘어갈 화면


            context.startActivity(intent2.addFlags(FLAG_ACTIVITY_NEW_TASK));
            finish();
        }else{
            Toast.makeText(context, temp_key, Toast.LENGTH_SHORT).show();


            Intent intent = new Intent(
                    context, // 현재화면의 제어권자
                    LoadingActivity.class); // 다음넘어갈 화면


            context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));


        }



        token = FirebaseInstanceId.getInstance().getToken();
        getto();


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel("news", "news", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("채팅 및 이벤트 알림");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(notificationChannel);

        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager);



        btn1 = (ImageView)findViewById(R.id.btn1);
        btn2 = (ImageView)findViewById(R.id.btn2);
        btn3= (ImageView)findViewById(R.id.btn3);
        btn4 = (ImageView)findViewById(R.id.btn4);
        btn5= (ImageView)findViewById(R.id.btn5);
        badge_new_chat= (ImageView)findViewById(R.id.badge_new);


        ActionBar actionBar = getSupportActionBar();

        // Custom Actionbar를 사용하기 위해 CustomEnabled을 true 시키고 필요 없는 것은 false 시킨다
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.



        //layout을 가지고 와서 actionbar에 포팅을 시킵니다.
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.action_bar, null);


        actionBar.setCustomView(actionbar);
        Toolbar parent = (Toolbar)actionbar.getParent();
        parent.setContentInsetsAbsolute(0,0);



        vp = (ViewPager)findViewById(R.id.vp);
        pagerAdapter=new pagerAdapter(getSupportFragmentManager());

        title_bar = (TextView) findViewById(R.id.title_tv);
        vp.setOffscreenPageLimit(5);
        //저장되는 최대한의 프래그먼트

        vp.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override //스와이프로 페이지 이동시 호출됨
            public void onPageSelected(int position) {
                //swipe - 손가락을 화면에 댄 후, 일직선으로 드래그했다가 손을 떼는 동작이다.
                //화면을 좌우로 스와이핑하여 섹션 사이를 이동할 때, 현재 선택된 탭의 위치이다.

                //액션바의 탭위치를 페이지 위치에 맞춘다.
                //   actionBar.setSelectedNavigationItem(position);


                btn1.setImageResource(R.drawable.ic_crown_off);
                btn2.setImageResource(R.drawable.ic_member_off);
                btn3.setImageResource(R.drawable.ic_list_off);
                btn4.setImageResource(R.drawable.ic_msg_off);
                btn5.setImageResource(R.drawable.ic_dot_off);


                if(position==0){

                    btn1.setImageResource(R.drawable.ic_crown_on);
                    title_bar.setText("인기멤버");
                }

                if(position==1){
                    btn2.setImageResource(R.drawable.ic_member_on);
                    title_bar.setText("전체멤버");
                }


                if(position==2){
                    btn3.setImageResource(R.drawable.ic_list_on);
                    title_bar.setText("사진 이야기");
                }


                if(position==3){
                    btn4.setImageResource(R.drawable.ic_msg_on);
                    title_bar.setText("대화함");

                    badge_new_chat.setVisibility(View.GONE);


                    SharedPreferences preferences_in = getSharedPreferences("pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor_new_in = preferences_in.edit();
                    editor_new_in.putString("new_url", "");
                    editor_new_in.putString("new_grade", "");
                    editor_new_in.commit();


                }


                if(position==4){
                    btn5.setImageResource(R.drawable.ic_dot_on);
                    title_bar.setText("더보기");
                }


                /*
                ImageView btn_first = (ImageView)findViewById(R.id.btn1);
                ImageView btn_second = (ImageView)findViewById(R.id.btn2);
                ImageView btn_third = (ImageView)findViewById(R.id.btn3);



                if(position==0){
                    btn_first.setImageResource(R.drawable.heart_on);
                    btn_second.setImageResource(R.drawable.heart_off);
                    btn_third.setImageResource(R.drawable.heart_off);
                }

                if(position==1){
                    btn_first.setImageResource(R.drawable.heart_off);
                    btn_second.setImageResource(R.drawable.heart_on);
                    btn_third.setImageResource(R.drawable.heart_off);
                }

                if(position==2){
                    btn_first.setImageResource(R.drawable.heart_off);
                    btn_second.setImageResource(R.drawable.heart_off);
                    btn_third.setImageResource(R.drawable.heart_on);
                }

                  */

            }
        });

        vp.setAdapter(pagerAdapter);
        vp.setCurrentItem(0);




        btn1.setOnClickListener(movePageListener);
        btn1.setTag(0);
        btn2.setOnClickListener(movePageListener);
        btn2.setTag(1);
        btn3.setOnClickListener(movePageListener);
        btn3.setTag(2);
        btn4.setOnClickListener(movePageListener);
        btn4.setTag(3);
        btn5.setOnClickListener(movePageListener);
        btn5.setTag(4);


/*
        ImageView btn_first = (ImageView)findViewById(R.id.btn1);
        ImageView btn_second = (ImageView)findViewById(R.id.btn2);
        ImageView btn_third = (ImageView)findViewById(R.id.btn3);

        btn_first.setOnClickListener(movePageListener);
        btn_first.setTag(0);
        btn_second.setOnClickListener(movePageListener);
        btn_second.setTag(1);
        btn_third.setOnClickListener(movePageListener);
        btn_third.setTag(2);



       btn_new.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        //여기에 이벤트를 적어주세요

                  //      SharedPreferences pref =  getSharedPreferences("pref", MODE_PRIVATE);
                 //       SharedPreferences.Editor editor = pref.edit();
                 //       editor.putString("token", "");
                //        editor.commit();
                //        startActivity(new Intent(context, Loginactivity.class));
                //        AActivity.finish();

                    }
                }
        );*/



        View.OnClickListener movePageListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int tag = (int) v.getTag();
                vp.setCurrentItem(tag);
            }
        };


        //각각의 섹션을 위한 탭을 액션바에 추가한다.
        for (int i = 0; i <pagerAdapter.getCount(); i++) {
        /*    actionBar.addTab(
                    actionBar.newTab()
                            //어댑터에서 정의한 페이지 제목을 탭에 보이는 문자열로 사용한다.
                       //     .setText(pagerAdapter.getPageTitle(i))
                            //TabListener 인터페이스를 구현할 액티비티 오브젝트도 지정한다.
                            .setTabListener(this)
                            .setIcon(R.drawable.tab_01)
            );*/
        }






        Intent intent = getIntent(); // 푸쉬 실행시

        String s = intent.getStringExtra("url");
        String a = intent.getStringExtra("grade");
        if(s!=null) {
            get_new_intent(s,a);
        }

        updateIconBadgeCount(this,0);

    }


    @Override //액션바의 탭 선택시 호출됨
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
        //액션바에서 선택된 탭에 대응되는 페이지를 뷰페이지에서 현재 보여지는 페이지로 변경한다.
        vp.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    View.OnClickListener movePageListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            int tag = (int) v.getTag();
            vp.setCurrentItem(tag);
        }
    };

    public Fragment findFragmentByPosition(int position) {
        return getSupportFragmentManager().findFragmentByTag(
                "android:switcher:" + vp.getId() + ":"
                        + ((FragmentPagerAdapter) vp.getAdapter()).getItemId(position));
    }




    private class pagerAdapter extends FragmentStatePagerAdapter
    {
        public pagerAdapter(android.support.v4.app.FragmentManager fm)
        {
            super(fm);
        }
        @Override
        public android.support.v4.app.Fragment getItem(int position)
        {

            switch(position)
            {
                case 0:
                    return new MemberrankFragment();
                case 1:
                    return new MemberlistFragment();
                case 2:
                    return new MainFragment();
                case 3:
                    return new ChatlistFragment();
                case 4:
                    return new SettingFragment();
                default:
                    return null;
            }
        }
        @Override
        public int getCount()
        {
            return 5;
        }

        public int getItemPosition(Object object){
            return POSITION_NONE;
        }
        //탭의 제목으로 사용되는 문자열 생성
        @Override
        public CharSequence getPageTitle(int position) {
            return "Section " + (position + 1);
        }



    }

    public Fragment findFragmentByPosition_new(int position) {
        return getSupportFragmentManager().findFragmentByTag(
                "android:switcher:" + vp.getId() + ":"
                        + ((FragmentPagerAdapter) vp.getAdapter()).getItemId(position));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {

            Log.d("json_url_pro", "called");
            vp.setAdapter(pagerAdapter);
            vp.setCurrentItem(0);

        }
        if (requestCode == 2) {

            Log.d("json_url_pro", "called");
            vp.setAdapter(pagerAdapter);
            vp.setCurrentItem(2);

        }


        if (requestCode == 4) {

            Log.d("json_url_pro", "called");
            vp.setAdapter(pagerAdapter);
            vp.setCurrentItem(4);
        }
    }


    @Override
    protected void  onResume() {
        super.onResume();


        SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
        String new_url=preferences.getString("new_url", "");
        String new_grade=preferences.getString("new_grade", "");

        set_new_badge(new_grade);
        updateIconBadgeCount(this,0);
        LocalBroadcastManager.getInstance(this).registerReceiver( mMessageReceiver, new IntentFilter("new_event"));

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver( mMessageReceiver);

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override

        public void onReceive(Context context, Intent intent) {


            String url = intent.getStringExtra("url");
            String grade = intent.getStringExtra("grade");

            set_new_badge(grade);
        }
    };

    private void initialize() {
        android.os.Handler handler = new android.os.Handler() {
            @Override
            public void handleMessage(Message msg) {
                getto();
            }
        };

        handler.sendEmptyMessageDelayed(0, 3000);
        //handler.sendEmptyMessageDelayed(0, 3);
    }

    private void getto() {
        String token = FirebaseInstanceId.getInstance().getToken();
        if(token=="" || token==null){
            initialize();
            //   Toast.makeText(this, "어플리케이션등록에 실패하였습니다.  잠시후 다시 시도합니다.", Toast.LENGTH_SHORT).show();
        }else{
            FirebaseMessaging.getInstance().subscribeToTopic("mannam");
            FirebaseMessaging.getInstance().subscribeToTopic("mannam_test");
            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("token", token);
            editor.commit();
            login();
        }
    }



    public void updateIconBadgeCount(Context context, int count) {
        SharedPreferences bpref = getSharedPreferences("bpref", MODE_PRIVATE);
        String b_count = bpref.getString("b_count", "");
        SharedPreferences.Editor editor = bpref.edit();
        editor.putString("b_count", "0");
        editor.commit();


        Log.d("test", "updateIconBadgeCount_main");
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");

        // Component를 정의
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", Mainviewpage.class.getName());

        // 카운트를 넣어준다.
        intent.putExtra("badge_count", 0);

        // Version이 3.1이상일 경우에는 Flags를 설정하여 준다.
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            intent.setFlags(0x00000020);
        }

        // send
        sendBroadcast(intent);
    }
    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
        String url = intent.getStringExtra("url");
        String grade = intent.getStringExtra("grade");
        if(url!=null) {

            Log.d("json_url_pro", url);
            Log.d("json_url_pro", grade);
            Log.d("json_url_pro", "onnewintent");
            get_new_intent(url,grade);

        }
        return;

    }

    private void get_new_intent(String url,String grade){
        Log.d("json_url_pro", "onnewintent_fun");
        if(grade.equals("chat")){
            vp.setAdapter(pagerAdapter);
            vp.setCurrentItem(3);
        }
    }


    private void set_new_badge(String grade){
        if(grade.equals("chat")){
            badge_new_chat.setVisibility(View.VISIBLE);
        }
    }

    private void login(){
        token = FirebaseInstanceId.getInstance().getToken();
        SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
        String userkey=preferences.getString("temp_key", "");
        RequestQueue queue;
        queue = Volley.newRequestQueue(getBaseContext());
        String url = "http://file.paranweb.co.kr/gay/login_chk.php?uk="+userkey+"&fcm_token="+token;



        JSONArray jsonarray;

        Log.d("json_url_pro", url);

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("json_url_pro", "res");


                try {
                    //Creating JsonObject from response String
                    JSONObject  jsonobject= new JSONObject(response.toString());
                    String su = jsonobject.getString("success");
                    if(su.equals("0")){
                        SharedPreferences pref =getBaseContext().getSharedPreferences("pref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("temp_key", "");
                        editor.commit();
                        finish();
                    }

                } catch (JSONException e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) ;


        stringRequest.setTag("Login");
        queue.add(stringRequest);

    }

}
