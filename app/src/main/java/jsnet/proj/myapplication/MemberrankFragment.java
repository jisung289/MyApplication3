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

public class MemberrankFragment extends Fragment
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
    private ImageView profile_img;

    private String json_user_name1;
    private String json_user_img1;
    private String json_user_name2;
    private String json_user_img2;
    private String json_user_name3;
    private String json_user_img3;


    private String json_user_uk1;
    private String json_user_uk2;
    private String json_user_uk3;



    private TextView user_name1;
    private ImageView user_img1;
    private TextView user_name2;
    private ImageView user_img2;
    private TextView user_name3;
    private ImageView user_img3;


    final int ITEM_SIZE = 6;
    ProgressDialog mProgressDialog;
    JSONObject jsonobject;
    JSONArray jsonarray;

    private ViewGroup container;
    private SwipeRefreshLayout swipeContainer;
    private ImageView coverimg;
    private Button setkeybtn;
    private String userkey;
    private Handler mHandler = new Handler();

    private RecyclerView recyclerView;


    public static RecyclerView.LayoutManager mLayoutManager;



    RecyclerAdapter_single RecyclerAdapter = null;

    public MemberrankFragment()
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
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_member, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recyclerview);


        SharedPreferences preferences = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        userkey=preferences.getString("temp_key", "");

        token = FirebaseInstanceId.getInstance().getToken();
        getto();

        // startActivity(new Intent(getActivity().getBaseContext(), Setkeyword.class));
        items.clear();


        swipeContainer = (SwipeRefreshLayout) layout.findViewById(R.id.swipe_layout);
        coverimg = (ImageView) layout.findViewById(R.id.imageView6);


        user_name1=(TextView) layout.findViewById(R.id.pro_name_rank1);
        user_name2=(TextView) layout.findViewById(R.id.pro_name_rank2);
        user_name3=(TextView) layout.findViewById(R.id.pro_name_rank3);



        user_img1=(ImageView) layout.findViewById(R.id.pro_img_rank1);
        user_img2=(ImageView) layout.findViewById(R.id.pro_img_rank2);
        user_img3=(ImageView) layout.findViewById(R.id.pro_img_rank3);



        swipeContainer.setRefreshing(true);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                // fetchTimelineAsync(0);

                items.clear();
                //  recyclerView.removeAllViews();
                page_int=1;
                re_view=0;

                new DownloadJSON().execute();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                        swipeContainer.setRefreshing(false);
                    }
                }, 1000);

            }
        });


        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);





        queue = Volley.newRequestQueue(getActivity());
        String url = "http://file.paranweb.co.kr/gay/get_member.php?uk="+userkey;


        Log.d("json_url_pro", url);

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("json_url_pro", "res");
                try {





                    //Creating JsonObject from response String
                    jsonobject= new JSONObject(response.toString());
                    json_user_name1=jsonobject.getString("json_user_name1");
                    json_user_img1=jsonobject.getString("json_user_img1");
                    json_user_name2=jsonobject.getString("json_user_name2");
                    json_user_img2=jsonobject.getString("json_user_img2");
                    json_user_name3=jsonobject.getString("json_user_name3");
                    json_user_img3=jsonobject.getString("json_user_img3");


                    json_user_uk1=jsonobject.getString("json_user_uk1");
                    json_user_uk2=jsonobject.getString("json_user_uk2");
                    json_user_uk3=jsonobject.getString("json_user_uk3");



                    user_name1.setText(json_user_name1);
                    user_name2.setText(json_user_name2);
                    user_name3.setText(json_user_name3);

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



                    ImageLoader.getInstance().displayImage("http://file.paranweb.co.kr/gay/profile_img_small/"+json_user_img1, user_img1,options);
                    //     profile_img.setBackground(new ShapeDrawable(new OvalShape()));
                    user_img1.setClipToOutline(true);


                    ImageLoader.getInstance().displayImage("http://file.paranweb.co.kr/gay/profile_img_small/"+json_user_img2, user_img2,options);
                    //     profile_img.setBackground(new ShapeDrawable(new OvalShape()));
                    user_img2.setClipToOutline(true);


                    ImageLoader.getInstance().displayImage("http://file.paranweb.co.kr/gay/profile_img_small/"+json_user_img3, user_img3,options);
                    //     profile_img.setBackground(new ShapeDrawable(new OvalShape()));
                    user_img3.setClipToOutline(true);





                } catch (JSONException e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) ;

        stringRequest.setTag("MAIN3");
        queue.add(stringRequest);


        user_img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        getActivity(), // 현재화면의 제어권자
                        Profile.class); // 다음넘어갈 화면
                intent.putExtra("user_token", json_user_uk1);
                getActivity().startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));

            }
        });

        user_img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        getActivity(), // 현재화면의 제어권자
                        Profile.class); // 다음넘어갈 화면
                intent.putExtra("user_token", json_user_uk2);
                getActivity().startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));

            }
        });

        user_img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        getActivity(), // 현재화면의 제어권자
                        Profile.class); // 다음넘어갈 화면
                intent.putExtra("user_token", json_user_uk3);
                getActivity().startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));

            }
        });



        user_img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        getActivity(), // 현재화면의 제어권자
                        Profile.class); // 다음넘어갈 화면
                intent.putExtra("user_token", json_user_uk3);
                getActivity().startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        //메모장 백업해놓음


        //  recyclerView.setAdapter(new RecyclerAdapter(getApplicationContext(), items, R.layout.activity_main));



        page_int=1;
        re_view=0;
        //    getData("http://car2.paranweb.co.kr/car.php?p="+String.valueOf(page_int));
        //    getData("http://car2.paranweb.co.kr/car.php?p="+String.valueOf(page_int));

        //   Item[] item = new Item[1];
        //    item[0] = new Item( "http://car.paranweb.co.kr/car_img/3151.png", "자동차", "1");

        //   items.add(item[0]);


        new DownloadJSON().execute();


//호출부분







        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {

                page_int=page_int+1;
                new DownloadJSON().execute();
            }
        });


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                swipeContainer.setRefreshing(false);
            }
        }, 1000);





        final RecyclerView scrollView = (RecyclerView) layout.findViewById(R.id.recyclerview);




        layout.findViewById(R.id.gototop).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {



                        LinearLayoutManager layoutManager = (LinearLayoutManager) scrollView
                                .getLayoutManager();
                        layoutManager.scrollToPositionWithOffset(0, 0);


                    }
                }
        );

        layout.findViewById(R.id.sha).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {


                        Intent intent = new Intent(
                                getActivity(), // 현재화면의 제어권자
                                Profile.class); // 다음넘어갈 화면
                        intent.putExtra("user_token", "ran");
                        getActivity().startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));


                    }
                }
        );


        return layout;

    }
    public void ref() {


        swipeContainer.setRefreshing(true);
        items.clear();
        //  recyclerView.removeAllViews();
        page_int=1;
        re_view=0;

        new DownloadJSON().execute();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                swipeContainer.setRefreshing(false);
            }
        }, 1000);
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

            // Retrieve JSON Objects from the given URL address

            jsonobject = JSONfunctions
                    .getJSONfromURL("http://file.paranweb.co.kr/gay/get_member_list.php?uk="+userkey+"&p="+ String.valueOf(page_int));


            Log.d("json_url", "http://file.paranweb.co.kr/gay/get_member_list.php?uk="+userkey+"&p="+ String.valueOf(page_int));
            if(jsonobject==null) {
                return null;
            }
            try {
                // Locate the array name in JSON
                jsonarray = jsonobject.getJSONArray("result");

                Item[] item = new Item[jsonarray.length()];

                if(page_int==1) {
                    items.clear();
                }
                Log.d("json_url", String.valueOf(jsonarray.length()));
                for (int i = 0; i < jsonarray.length(); i++) {
                    jsonobject = jsonarray.getJSONObject(i);
                    // Retrive JSON Objects

                    item[i] = new Item(jsonobject.getString("car_img"), jsonobject.getString("car_name"), jsonobject.getString("sno"), jsonobject.getString("p_content"), jsonobject.getString("reg_date"), jsonobject.getString("skin_num"), jsonobject.getString("pro_img"), jsonobject.getString("able_java"), jsonobject.getString("news_code"));
                    items.add(item[i]);
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


                if(re_view==0) {

                    RecyclerAdapter=new RecyclerAdapter_single(getActivity(), items, R.layout.fragment_first);

                    recyclerView.setAdapter(RecyclerAdapter);

                    if(jsonarray.length()<1){
                        coverimg.setImageResource(R.drawable.nosearch);
                    }


                    re_view=1;

                }else {

                    RecyclerAdapter.additem(RecyclerAdapter.getItemCount()-jsonarray.length());
                }

                if(jsonarray.length()>0) {
                    swipeContainer.setVisibility(View.VISIBLE);
                    coverimg.setVisibility(View.GONE);

                    swipeContainer.setRefreshing(false);
                }


            }else {
                coverimg.setImageResource(R.drawable.intro2);
                Toast.makeText(getActivity(), "인터넷 연결 상태가 좋지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        }

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