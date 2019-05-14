package jsnet.proj.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.firebase.iid.FirebaseInstanceId;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


/**
 * Created by Administrator on 2017-09-24.
 */

public class test extends AppCompatActivity {

    public ImageLoader imageLoader = ImageLoader.getInstance();


    private int chat_scroll_flag=0;

    private RequestQueue queue;
    private EditText mInputMessageView;
    Context context;
    public static Context mContext;
    private final int DYNAMIC_VIEW_ID  = 0x8000;
private int page_int=0;
    private int last_play;
    private int last_play_p;
private  String[] list;
    JSONObject jsonobject;
    JSONArray jsonarray;
    private Socket mSocket;
    private LinearLayout inLayout;
    private LinearLayout pro_list;

    private String[]  user_name_l;
    private String[] pro_img_l ;
    private String[]  session_id_l ;

    private Handler mHandler;
    private Runnable mRunnable;
    private String userkey;
    private DisplayImageOptions options;
    MediaPlayer mPlayer = null;
    private String user_token;
    private String chat_sno;
    private String provide;


    ScrollView scrollView;
    EditText chat_content;
String chat_content_s;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // IO.socet()메서드와 mSocket.connect() 메서드사이에 일반적으로 작성하는 것 같습니다.

        imageLoader.init(ImageLoaderConfiguration.createDefault(getBaseContext()));
        chat_scroll_flag=0;
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.temp_img)
                .showImageOnFail(R.drawable.temp_img)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        System.gc();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);



        SharedPreferences preferences2 = getSharedPreferences("pref", Context.MODE_PRIVATE);
        userkey=preferences2.getString("temp_key", "");

        Intent intent = getIntent(); // 값을 받아온다.
        user_token=(String)intent.getExtras().get("user_token");

        if(user_token.isEmpty()){

            Toast.makeText(context, "채팅 참여자 정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }

        queue = Volley.newRequestQueue(this);
        String url = "http://file.paranweb.co.kr/gay/get_chatsno.php?uk="+userkey+"&at="+user_token;


        Log.d("json_url_pro", url);

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("json_url_pro", "res");
                try {
                    //Creating JsonObject from response String
                    jsonobject= new JSONObject(response.toString());
                    chat_sno=jsonobject.getString("user_name");
                    provide=jsonobject.getString("provide");
                    if(chat_sno.isEmpty()){
                        Toast.makeText(context, "채팅 참여자 정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    if(chat_sno.equals("nopoint")){
                        point_c();
                    }
                } catch (JSONException e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) ;



        stringRequest.setTag("MAIN");
        queue.add(stringRequest);





        context = getApplicationContext();
        mContext = this;
        ActionBar actionBar = getSupportActionBar();

        // Custom Actionbar를 사용하기 위해 CustomEnabled을 true 시키고 필요 없는 것은 false 시킨다
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.


        //layout을 가지고 와서 actionbar에 포팅을 시킵니다.
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.action_bar_inchat, null);

        actionBar.setCustomView(actionbar);


        //액션바 양쪽 공백 없애기
        // Toolbar parent = (Toolbar)actionbar.getParent();
        //   parent.setContentInsetsAbsolute(0,0);


        // 액션바에 백그라운드 색상을 아래처럼 입힐 수 있습니다.
        //actionBar.setBackgroundDrawable(new ColorDrawable(Color.argb(255,255,255,255)));


        // 액션바에 백그라운드 이미지를 아래처럼 입힐 수 있습니다. (drawable 폴더에 img_action_background.png 파일이 있어야 겠죠?)
        //actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.img_action_background));

        //    showNotiPannel = false;


        TextView chat_title_view = (TextView) findViewById(R.id.chat_title_tv);
        ImageView btn = (ImageView) findViewById(R.id.my_news_list);

        btn.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();


                    }
                }
        );


        ImageView view_profile_list = (ImageView) findViewById(R.id.view_profile_list_inchat);

        view_profile_list.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        View drawerView;
                        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.d_lay_wrap);

                        drawerView = (View) findViewById(R.id.d_layer);

                        if (drawerLayout.isDrawerOpen(drawerView)) {
                            drawerLayout.closeDrawer(drawerView);
                        } else {
                            drawerLayout.openDrawer(drawerView);

                        }




                    }
                }
        );










        SharedPreferences preferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        userkey=preferences.getString("temp_key", "");

        Toast.makeText(context, userkey, Toast.LENGTH_SHORT).show();
        mPlayer = MediaPlayer.create(this, R.raw.sample);
        mPlayer.setOnCompletionListener(mCompleteListener);
        deletePlayer();

        try {
            IO.Options opts = new IO.Options();
            opts.forceNew = false;
            opts.reconnection = true;

           // mSocket = IO.socket("http://175.126.82.167:3400",opts);
            mSocket = IO.socket("http://175.126.82.167:3400");

            mSocket.connect();
        } catch(URISyntaxException e) {
            e.printStackTrace();
        }

        final String useruuid;
       useruuid = GetDevicesUUID(getBaseContext());

        setContentView(R.layout.chat);



        TextInputLayout te_layout = (TextInputLayout) findViewById(R.id.te_layout);
       // te_layout.setHintEnabled(false);
        ImageView imageV = (ImageView) findViewById(R.id.imageView45);

        imageV.setBackground(new ShapeDrawable(new OvalShape()));
        imageV.setClipToOutline(true);
        inLayout = (LinearLayout) findViewById(R.id.inLayout);
        pro_list = (LinearLayout) findViewById(R.id.pro_list_sc);



        findViewById(R.id.btn_send).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        //여기에 이벤트를 적어주세요

                        chat_content = (EditText)findViewById(R.id.editText);
                        chat_content_s = chat_content.getText().toString();
                        chat_content.setText("");

                        send_chat(chat_content_s,chat_content_s);
                        Toast.makeText(context, "send_chat...", Toast.LENGTH_SHORT).show();



                    }
                }
        );

        findViewById(R.id.imgup).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        //여기에 이벤트를 적어주세요


                        Intent intent = new Intent(
                                context, // 현재화면의 제어권자
                                Chat_add_img.class); // 다음넘어갈 화면


                        context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));

                    }
                }
        );


        findViewById(R.id.audio).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        //여기에 이벤트를 적어주세요


                        Intent intent = new Intent(
                                context, // 현재화면의 제어권자
                                rec.class); // 다음넘어갈 화면



                        context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));


                    }
                }
        );





        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on("chat", onMessageReceived);
        mSocket.on("get_client_list",  profile_list);
        JSONObject data = new JSONObject();
        try {
            //data.put("j_content", user_name);


            data.put("uk", userkey);
        } catch(JSONException e) {
            e.printStackTrace();
        }


        mSocket.emit("systemIn", data);



        new DownloadJSON().execute();






    }

    private void point_c(){
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setMessage("포인트가 부족하여 상대에게 먼저 대화를 보낼수 없습니다.\n포인트를 충전하시겠습니까?").setCancelable(
                false).setPositiveButton("포인트 충전",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'Yes' Button

                        Toast.makeText(context, "포인트 충전으로 이동합니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).setNegativeButton("닫기",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'NO' Button
                        finish();
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alt_bld.create();
        // Title for AlertDialog
        alert.setTitle("포인트 부족");
        // Icon for AlertDialog
       // alert.setIcon(R.drawable.icon);
        alert.show();
    }

    // Socket서버에 connect 된 후, 서버로부터 전달받은 'Socket.EVENT_CONNECT' Event 처리.
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // your code...
        //      Toast.makeText(context, "연결됨...", Toast.LENGTH_SHORT).show();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {








                }
            });

        }
    };
    // 서버로부터 전달받은 'chat-message' Event 처리.
    private Emitter.Listener onMessageReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // 전달받은 데이터는 아래와 같이 추출할 수 있습니다.
            JSONObject receivedData = (JSONObject) args[0];
            String username;
            String message;

            final String data;
            final String data2;
            final String data3;
            final String data4;
            final String data5;
            try {
                 data = receivedData.getString("user_name");
               data2 = receivedData.getString("content");
                data3 = receivedData.getString("pro_img_1");
                data4 = receivedData.getString("tempid");
                data5 = receivedData.getString("re_tempid");
                Log.d("jisung_redate", data5);
            } catch (JSONException e) {
                return;
            }
            // add the message to view


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                //    Toast.makeText(context,"img1"+data3, Toast.LENGTH_SHORT).show();
                    if(data4.equals(user_token) || data4.equals(userkey)) {
                        add_view(data, data2, data3, data4,data5);
                    }
                }
            });

            // your code...
        }
    };



    // 서버로부터 전달받은 'chat-message' Event 처리.
    private Emitter.Listener profile_list = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // 전달받은 데이터는 아래와 같이 추출할 수 있습니다.
            JSONArray receivedData=(JSONArray)args[0];




           final String data = receivedData.toString();




            try{
                JSONArray jarray = new JSONArray(data);
                int a=jarray.length();
                Log.d("test1112", String.valueOf(a));

                user_name_l = new String[a];
                pro_img_l = new String[a];
                session_id_l = new String[a];


                for(int k=0; k < jarray.length(); k++){
                    Log.d("test1112", String.valueOf(k));



                    JSONObject jObject = jarray.getJSONObject(k);  // JSONObject 추출
                     user_name_l[k] = jObject.getString("nickname");
                     pro_img_l[k] = jObject.getString("pro_img");
                     session_id_l[k] = jObject.getString("session_id");

                }





            }
            catch (JSONException e){ ;}



            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pro_list.removeAllViews();
                    ref_pro_list(user_name_l,pro_img_l,session_id_l);
                }
            });

        }
    };


    @Override
    public void onBackPressed() {




        View drawerView;
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.d_lay_wrap);

        drawerView = (View) findViewById(R.id.d_layer);

        if (drawerLayout.isDrawerOpen(drawerView)) {
            drawerLayout.closeDrawer(drawerView);
        } else {
            super.onBackPressed();

        }
    }

    @Override
    public void onDestroy() {


        queue = Volley.newRequestQueue(this);
        String url = "http://file.paranweb.co.kr/gay/chat_ajax.php?p=&uk="+userkey+"&ta="+user_token;
        Log.d("json_url_pro", url);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("json_url_pro", "res");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) ;



        stringRequest.setTag("MAIN");
        queue.add(stringRequest);



        Toast.makeText(context,"chat_dis", Toast.LENGTH_SHORT).show();
        mSocket.disconnect();
        inLayout.removeAllViews();
        System.gc();
        super.onDestroy();

    }
    public void clearApplicationCache(java.io.File dir){
        if(dir==null) dir = getCacheDir();
        if(dir==null) return;
        java.io.File[] children = dir.listFiles();
        try{
            // 쿠키 삭제
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeSessionCookie();

            for(int i=0;i<children.length;i++)
                if(children[i].isDirectory())
                    clearApplicationCache(children[i]);
                else children[i].delete();
        }
        catch(Exception e){}
    }


    public void send_chat(String user_name, String content) {


        Log.d("chat_int", "a:"+provide);
        Log.d("chat_int", "b:"+chat_sno);
        // 서버로 이벤트를 전송하는 부분에 적절히 추가하시면 될 것 같아요.
        JSONObject data = new JSONObject();
        try {
            //data.put("j_content", user_name);

            data.put("content", content);
            data.put("uk", userkey);
            data.put("re_uk", user_token);
            data.put("chat_sno", chat_sno);
            data.put("provide", provide);
            mSocket.emit("chat", data);
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    public void ref_pro_list(String[] user_name, String[] pro_img_list, String[] uk) {

        Log.d("test1112", String.valueOf(user_name.length));
            // add the message to view

        for(int i=0; i < user_name.length; i++) {
            LayoutInflater inflater_n = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout pro_list_l = (LinearLayout) inflater_n.inflate(R.layout.chat_pro_list, null);

            ImageView pro_img_v = (ImageView) pro_list_l.findViewById(R.id.pro_img);
            TextView user_name_v = (TextView) pro_list_l.findViewById(R.id.user_name);
            pro_img_v.setBackground(new ShapeDrawable(new OvalShape()));

         //     imageLoader = new ImageLoader(getBaseContext());
         //   imageLoader.DisplayImage("http://file.paranweb.co.kr/gay/images/" + pro_img_list[i], pro_img_v);

            final String img_list=pro_img_list[i];

            pro_img_v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(
                            context, // 현재화면의 제어권자
                            img_view.class); // 다음넘어갈 화면
                    intent.putExtra("img", "http://file.paranweb.co.kr/gay/profile_img_big/" + img_list);
                    context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));
                }
            });


            ImageLoader.getInstance().displayImage("http://file.paranweb.co.kr/gay/profile_img_small/" + pro_img_list[i], pro_img_v, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    //     spinner.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    String message = null;
                    switch (failReason.getType()) {
                        case IO_ERROR:
                            message = "Input/Output error";
                            break;
                        case DECODING_ERROR:
                            message = "Image can't be decoded";
                            break;
                        case NETWORK_DENIED:
                            message = "Downloads are denied";
                            break;
                        case OUT_OF_MEMORY:
                            message = "Out Of Memory error";
                            break;
                        case UNKNOWN:
                            message = "Unknown error";
                            break;
                    }
                    Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();

                    //   spinner.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    //     spinner.setVisibility(View.GONE);
                }
            });


            user_name_v.setText(user_name[i]);

            pro_list.addView(pro_list_l);
        }
    }

    public void add_view(String user_name, String content, final String pro_img, String tempid,String re_tempid) {

        if(tempid.equals(userkey)) {

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout route_info_tab = (LinearLayout) inflater.inflate(R.layout.route_info_tab, null);

            ImageView im = (ImageView) route_info_tab.findViewById(R.id.imageView4);
            TextView iv = (TextView) route_info_tab.findViewById(R.id.textView);

            final ImageView im_audio = (ImageView) route_info_tab.findViewById(R.id.imageView8);


            if(content.startsWith("img:")){

                iv.setVisibility(View.GONE);
                im.setVisibility(View.VISIBLE);


                final String[] data = content.split("img:");

                img_load("http://file.paranweb.co.kr/gay/user_img_small/"+data[1], im);



                im.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        Intent intent = new Intent(
                                context, // 현재화면의 제어권자
                                img_view.class); // 다음넘어갈 화면

                        intent.putExtra("img", data[1]);


                        context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));

                    }
                });

            }else if(content.startsWith("audio:")){

                iv.setVisibility(View.GONE);
                im_audio.setVisibility(View.VISIBLE);


                final String[] data = content.split("audio:");
                final String[] data_a=data[1].split("/");
                final String data_b=data_a[data_a.length-1];
                final String[] data_c=data_b.split("\\.");


                last_play= Integer.parseInt(data_c[data_c.length-2].substring(18,22));

                im_audio.setId(DYNAMIC_VIEW_ID + last_play);
                // 오디오 파일을 로딩한다



                im_audio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        im_audio.getId();
                        deletePlayer();
                        last_play_p=im_audio.getId();
                        String url = data[1];

                        if (mPlayer != null) {
                            mPlayer.stop();
                            mPlayer.release();
                            mPlayer = null;

                            im_audio.setImageResource(R.drawable.h2);
                        }else {
                            LoadMedia(url);
                            mPlayer.start();
                            im_audio.setImageResource(R.drawable.h2_on);
                        }

                    }
                });

            }else {
                iv.setText(content);
            }
            // 추가할 녀석(route_info_tab 이라는 다른 xml 파일에 있다.ㅎ)
            inLayout.addView(route_info_tab);
        }else {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout route_info_tab = (LinearLayout) inflater.inflate(R.layout.chat_out, null);

            TextView iv = (TextView) route_info_tab.findViewById(R.id.textView);
            TextView iv2 = (TextView) route_info_tab.findViewById(R.id.user_name);
            ImageView im = (ImageView) route_info_tab.findViewById(R.id.imageView4);
            final ImageView im_audio = (ImageView) route_info_tab.findViewById(R.id.imageView8);



            if(content.startsWith("img:")){

                iv.setVisibility(View.GONE);
                im.setVisibility(View.VISIBLE);

                //imageLoader = new ImageLoader(getBaseContext());
               final String[] data = content.split("img:");


                img_load("http://file.paranweb.co.kr/gay/user_img_small/"+data[1], im);

                im.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        Intent intent = new Intent(
                                context, // 현재화면의 제어권자
                                img_view.class); // 다음넘어갈 화면

                        intent.putExtra("img", data[1]);


                        context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));

                    }
                });

                iv2.setText(user_name);
            }else if(content.startsWith("audio:")){

                iv.setVisibility(View.GONE);
                im_audio.setVisibility(View.VISIBLE);

                final String[] data = content.split("audio:");
                final String[] data_a=data[1].split("/");
                final String data_b=data_a[data_a.length-1];
                final String[] data_c=data_b.split("\\.");


                last_play= Integer.parseInt(data_c[data_c.length-2].substring(18,22));

                im_audio.setId(DYNAMIC_VIEW_ID + last_play);

                // 오디오 파일을 로딩한다




                im_audio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String url = data[1];
                        im_audio.getId();

                        deletePlayer();
                        last_play_p=im_audio.getId();

                        if (mPlayer != null) {
                            mPlayer.stop();
                            mPlayer.release();
                            mPlayer = null;

                            im_audio.setImageResource(R.drawable.h2);
                        }else {
                            deletePlayer();

                            LoadMedia(url);
                            mPlayer.start();

                            im_audio.setImageResource(R.drawable.h2_on);
                        }

                    }
                });

                iv2.setText(user_name);
            }else {

                iv.setText(content);
                iv2.setText(user_name);
            }


            ImageView im2 = (ImageView) route_info_tab.findViewById(R.id.imageView7);

            im2.setBackground(new ShapeDrawable(new OvalShape()));

                if(pro_img!="") {
               //     Toast.makeText(context,"img3"+pro_img, Toast.LENGTH_SHORT).show();
                //    imageLoader = new ImageLoader(getBaseContext());
                    final String[] data = content.split("img:");
                    img_load("http://file.paranweb.co.kr/gay/profile_img_small/" + pro_img, im2);

                  //

                }

            im2.setClipToOutline(true);

            im2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    Intent intent = new Intent(
                            context, // 현재화면의 제어권자
                            img_view.class); // 다음넘어갈 화면

                    intent.putExtra("img", "http://file.paranweb.co.kr/gay/profile_img_big/"+pro_img);


                    context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));

                }
            });


            // 추가할 녀석(route_info_tab 이라는 다른 xml 파일에 있다.ㅎ)
            inLayout.addView(route_info_tab);
        }
        mRunnable = new Runnable() {
            @Override
            public void run() {
                    if(chat_scroll_flag==0) {
                        scrollView = (ScrollView) findViewById(R.id.scrollView);
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
            }
        };

        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, 100);

        scrollView = (ScrollView) findViewById(R.id.scrollView);


        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {


                if(chat_scroll_flag==0) {
                    scrollView = (ScrollView) findViewById(R.id.scrollView);
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }


                int scrollY = scrollView.getScrollY(); // For ScrollView
                int scrollX = scrollView.getScrollX(); // For HorizontalScrollView
                // DO SOMETHING WITH THE SCROLL COORDINATES
                if(scrollView.getChildAt(0).getHeight()-scrollView.getHeight()==scrollView.getScrollY()){
                    chat_scroll_flag=0;
                }else{
                    chat_scroll_flag=1;
                }
            }
        });



    }


    private String GetDevicesUUID(Context mContext) {
        return user_token;
    }


    // 오디오 파일을 로딩한다
    public boolean LoadMedia(String filePath) {
        Toast.makeText(context, "음성파일을 재생합니다.", Toast.LENGTH_SHORT).show();
        try {
            mPlayer = new MediaPlayer();
            mPlayer.setDataSource(filePath);
             mPlayer.setOnCompletionListener(mCompleteListener);
        } catch (IOException e) {
            return false;
        }

        try {
            mPlayer.prepare();
        } catch (IOException e) {
            return false;
        }

        return true;
    }


    public void deletePlayer() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;

        }

    //    Toast.makeText(context,"deleteplayer", Toast.LENGTH_SHORT).show();
        if(last_play_p!=0) {




            ImageView im = (ImageView) findViewById(last_play_p);

            im.setImageResource(R.drawable.h2);
        }
    }

    MediaPlayer.OnCompletionListener mCompleteListener =
            new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer arg0) {
                    // mTextMessage.setText("Play Ended!");
                    deletePlayer();
                }
            };


    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first

        // Release the Camera because we don't need it when paused
        // and other activities might need to use it.
        deletePlayer();
    }



    // DownloadJSON AsyncTask
    private class DownloadJSON extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create an array


            jsonobject = JSONfunctions
                    .getJSONfromURL("http://file.paranweb.co.kr/gay/chat_ajax.php?p=&uk="+userkey+"&ta="+user_token);


            Log.d("json_url", "http://file.paranweb.co.kr/gay/chat_ajax.php?p=&uk="+userkey+"&ta="+user_token);
            if(jsonobject==null) {
                return null;
            }
            try {
                // Locate the array name in JSON
                jsonarray = jsonobject.getJSONArray("result");
                String[] array = new String[3];

               list = new String[jsonarray.length()];


                Log.d("json_url", String.valueOf(jsonarray.length()));
                for (int i = 0; i < jsonarray.length(); i++) {
                    jsonobject = jsonarray.getJSONObject(i);
                    // Retrive JSON Objects

                    list[i] = jsonobject.getString("user_name")+"|"+jsonobject.getString("content")+"|"+jsonobject.getString("pro_img")+"|"+jsonobject.getString("uk")+"|"+jsonobject.getString("re_uk");

                    //items.add(item[i]);
                }

            } catch (JSONException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if(jsonarray!=null) {

                //  RecyclerAdapter.additem(RecyclerAdapter.getItemCount()-jsonarray.length());
                // mProgressDialog.dismiss();
                String[] list_spit;
                String d;
                for (int i = 0; i < jsonarray.length()-1; i++) {
                    // Retrive JSON Objects
                    list_spit=null;
                    d=null;

                    d=list[i];

                    Log.d("chat_new", d);
                    list_spit =d.split("\\|");


                    add_view(list_spit[0],list_spit[1],list_spit[2],list_spit[3],list_spit[4]);

                }



            }else{
                Toast.makeText(context, "인터넷 연결 상태가 좋지 않습니다. 채팅서버 연결이 불안정합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private String GetDevicesUUID() {
        String token = FirebaseInstanceId.getInstance().getToken();
        return token;
    }

    private void img_load(String url, ImageView img_h)
    {


        ImageLoader.getInstance().displayImage(url, img_h, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                //     spinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                String message = null;
                switch (failReason.getType()) {
                    case IO_ERROR:
                        message = "Input/Output error";
                        break;
                    case DECODING_ERROR:
                        message = "Image can't be decoded";
                        break;
                    case NETWORK_DENIED:
                        message = "Downloads are denied";
                        break;
                    case OUT_OF_MEMORY:
                        message = "Out Of Memory error";
                        break;
                    case UNKNOWN:
                        message = "Unknown error";
                        break;
                }
                Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();

                //   spinner.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                //     spinner.setVisibility(View.GONE);
                if(chat_scroll_flag==0) {
                    scrollView = (ScrollView) findViewById(R.id.scrollView);
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            }
        });
    }


}
