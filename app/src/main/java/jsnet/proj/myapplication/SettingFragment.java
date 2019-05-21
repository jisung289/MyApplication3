package jsnet.proj.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


/**
 * Created by Administrator on 2017-06-14.
 */

public class SettingFragment extends Fragment
{
    private Intent intent;

    String myJSON;

    private String token;
    private String page="1";
    private int page_int=1;
    private int re_view=0;
    private int flag_frist=0;
    FloatingActionButton menu1,menu2,menu3 ;
    List<Item> items = new ArrayList<>();



    private RequestQueue queue;

    private String json_user_name;
    private String json_user_img;
    private String json_user_text;
    private String json_user_carea;
    private String json_user_sex;
    private String json_user_point;
    private String admin_code;


    private TextView point_view;
    private TextView profile_name;
    private TextView profile_text;
    private TextView profile_area;
    private ImageView profile_img;



    final int ITEM_SIZE = 6;
    ProgressDialog mProgressDialog;
    JSONObject jsonobject;
    JSONArray jsonarray;

    private ViewGroup container;
    private SwipeRefreshLayout swipeContainer;
    private ImageView btn2;
    private ImageView get_free_point;




    private Button setkeybtn;
    private String userkey;
    private Handler mHandler = new Handler();

    private RecyclerView recyclerView;


    public static RecyclerView.LayoutManager mLayoutManager;



    RecyclerAdapter_single RecyclerAdapter = null;

    public SettingFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        flag_frist=1;
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.setting_fragment, container, false);
        SharedPreferences preferences = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        userkey=preferences.getString("temp_key", "");

        token = FirebaseInstanceId.getInstance().getToken();
        getto();

        // startActivity(new Intent(getActivity().getBaseContext(), Setkeyword.class));
        items.clear();
        profile_name = (TextView) layout.findViewById(R.id.profile_name);
        profile_text = (TextView) layout.findViewById(R.id.profile_text);
        profile_area= (TextView) layout.findViewById(R.id.profile_area);

        profile_img = (ImageView)layout.findViewById(R.id.profile_img);
        btn2 = (ImageView)layout.findViewById(R.id.btn2);

        point_view= (TextView) layout.findViewById(R.id.point_view);


        get_free_point = (ImageView) layout.findViewById(R.id.get_free_point);


        get_free_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        getActivity().getBaseContext(), // 현재화면의 제어권자
                        Freepoint.class); // 다음넘어갈 화면
                getActivity().startActivityForResult(intent,4);
            }
        });



        queue = Volley.newRequestQueue(getActivity());
        String url = "http://file.paranweb.co.kr/gay/get_profile_set.php?uk="+userkey+"&at="+userkey;


        Log.d("json_url_pro", url);

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("json_url_pro", "res");
                try {
                    //Creating JsonObject from response String
                    jsonobject= new JSONObject(response.toString());
                    json_user_name=jsonobject.getString("user_name");
                    json_user_img = jsonobject.getString("user_proimg");
                    json_user_text = jsonobject.getString("user_text");

                    json_user_sex = jsonobject.getString("sex");
                    json_user_carea = jsonobject.getString("carea");
                    json_user_point= jsonobject.getString("point");
                    admin_code= jsonobject.getString("admin_code");


                    point_view.setText(json_user_point);
                    profile_name.setText(json_user_name);
                    profile_text.setText(json_user_text);


                    profile_area.setText(json_user_carea);



                    ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity().getBaseContext())
                            .threadPriority(Thread.NORM_PRIORITY - 2)
                            .denyCacheImageMultipleSizesInMemory()
                            .discCacheFileNameGenerator(new Md5FileNameGenerator())
                            .tasksProcessingOrder(QueueProcessingType.LIFO)
                            .writeDebugLogs() // Remove for release app
                            .build();
                    ImageLoader.getInstance().init(config);




                    DisplayImageOptions options = new DisplayImageOptions.Builder()
                            .showImageOnLoading(R.drawable.temp_img)
                            .showImageForEmptyUri(R.drawable.temp_img)
                            .showImageOnFail(R.drawable.temp_img)
                            .cacheInMemory(true)
                            .cacheOnDisk(true)
                            .considerExifParams(true)
                            .build();



                    ImageLoader.getInstance().displayImage("http://file.paranweb.co.kr/gay/profile_img_small/"+json_user_img, profile_img,options);
                    //     profile_img.setBackground(new ShapeDrawable(new OvalShape()));
                    profile_img.setClipToOutline(true);

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


        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        getActivity(), // 현재화면의 제어권자
                        Profile.class); // 다음넘어갈 화면
                intent.putExtra("user_token", userkey);
                getActivity().startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));

            }
        });

        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        getActivity(), // 현재화면의 제어권자
                        Profile.class); // 다음넘어갈 화면
                intent.putExtra("user_token", userkey);
                getActivity().startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));

            }
        });


        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        getActivity(), // 현재화면의 제어권자
                        chat.class); // 다음넘어갈 화면
                intent.putExtra("user_token", admin_code);
                getActivity().startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));

            }
        });




        return layout;

    }


    private void initialize() {
        Handler handler = new Handler() {
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
            FirebaseMessaging.getInstance().subscribeToTopic("news");
            FirebaseMessaging.getInstance().subscribeToTopic("news_new");
            SharedPreferences pref = getActivity().getBaseContext().getSharedPreferences("pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("token", token);
            editor.commit();
            //    Toast.makeText(this, "어플리케이션이 정상적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }


}