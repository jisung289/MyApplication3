package jsnet.proj.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Profile extends AppCompatActivity {


    private Intent intent;

    String myJSON;

    String ConvertImage ;

    byte[] byteArray ;

    HttpURLConnection httpURLConnection ;

    URL url;

    OutputStream outputStream;

    BufferedWriter bufferedWriter ;

    int RC ;

    BufferedReader bufferedReader ;

    StringBuilder stringBuilder;

    boolean check = true;
    private String page="1";
    private int page_int=1;
    private int re_view=0;
    private int flag_frist=0;
    List<Item> items = new ArrayList<>();

    final int ITEM_SIZE = 6;
    ProgressDialog mProgressDialog;
    JSONObject jsonobject;
    JSONArray jsonarray;

    private ViewGroup container;
    private SwipeRefreshLayout swipeContainer;
    private ImageView coverimg;
    private static final String TAG = "MAIN";

    private TextView profile_name;
    private TextView profile_text;
    private TextView profile_area;

    ArrayAdapter<CharSequence> adspin;

    private LinearLayout btn_like;
    private LinearLayout btn_chat;
    private LinearLayout btn_list;



    private ImageView mypro_img;
    private ImageView mypro_text;
    private ImageView mypro_knick;
    private ImageView mypro_area;

    private TextView profile_btn_like;
    private TextView profile_btn_list;


    private String user_token;

    private RequestQueue queue;
    private ImageView profile_img;

    private String json_user_name;
    private String json_user_img;
    private String json_user_text;
    private String json_user_carea;
    private String json_user_sex;

    ByteArrayOutputStream byteArrayOutputStream ;
    ByteArrayOutputStream byteArrayOutputStreama[] ;

    Intent CamIntent, GalIntent, CropIntent ;
    private String jsno_btn_hit;
    private String jsno_btn_list;


    private Boolean spinner_flag;

    private Spinner spinner;
    private String userkey;

    private Handler mHandler = new Handler();


    String user_data = "" ;
    String ImageTag = "image_tag" ;
    String b_sno = "" ;

    String ImageName = "image_data" ;

    String ServerUploadPath ="http://file.paranweb.co.kr/gay/save_proimg.php" ;


    ProgressDialog progressDialog ;
    File file;
    Uri uri;
    Uri uri_d;


    int img_up_flag=0;
    int rotate = 0;
    int img_up_cnt = 0;
    int img_up_i = 0;


    private RecyclerView recyclerView;


    public static RecyclerView.LayoutManager mLayoutManager;



    RecyclerAdapter_profile RecyclerAdapter = null;

    String imageUri = "http://jutam.co.kr/images/login_logo.png"; // from Web


    private DisplayImageOptions options;
    ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);


        Intent intent = getIntent(); // 값을 받아온다.
        user_token=(String)intent.getExtras().get("user_token");



        SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
         userkey=preferences.getString("temp_key", "");


        profile_name = (TextView) findViewById(R.id.profile_name);
        profile_text = (TextView) findViewById(R.id.profile_text);
        profile_btn_like = (TextView) findViewById(R.id.likecnt);
        profile_btn_list = (TextView) findViewById(R.id.c_list);

        profile_area= (TextView) findViewById(R.id.profile_area);

        byteArrayOutputStream = new ByteArrayOutputStream();


        mypro_img = (ImageView) findViewById(R.id.mypro_img);
        mypro_knick = (ImageView) findViewById(R.id.mypro_knick);
        mypro_text = (ImageView) findViewById(R.id.mypro_text);
        mypro_area = (ImageView) findViewById(R.id.mypro_area);

        if(user_token.equals(userkey)) {


            mypro_img.setBackground(new ShapeDrawable(new OvalShape()));
            mypro_knick.setBackground(new ShapeDrawable(new OvalShape()));
            mypro_text.setBackground(new ShapeDrawable(new OvalShape()));
            mypro_area.setBackground(new ShapeDrawable(new OvalShape()));

            mypro_img.setVisibility(View.VISIBLE);
            mypro_knick.setVisibility(View.VISIBLE);
            mypro_text.setVisibility(View.VISIBLE);
            mypro_area.setVisibility(View.VISIBLE);

        }

        mypro_knick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(
                        getBaseContext(), // 현재화면의 제어권자
                        Fix_profile.class); // 다음넘어갈 화면
                intent.putExtra("fix_type", "knick");
                intent.putExtra("fix_data", profile_name.getText());
                startActivityForResult(intent,1);

            }
        });

        spinner_flag=false;
        spinner = (Spinner) findViewById(R.id.spinner_area);
        spinner.setPrompt("시/도 를 선택하세요.");

        adspin = ArrayAdapter.createFromResource(this, R.array.area,    android.R.layout.simple_spinner_item);

        adspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adspin);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?>  parent, View view, int position, long id) {
                if(spinner_flag==true) {
                    profile_area.setText(adspin.getItem(position));



                    SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                    String userkey=preferences.getString("temp_key", "");

                    queue = Volley.newRequestQueue(getBaseContext());
                    String url = "http://file.paranweb.co.kr/gay/save_pro_area.php?uk="+userkey+"&at="+adspin.getItem(position);


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


                    stringRequest.setTag(TAG);
                    queue.add(stringRequest);


                }


                spinner_flag=false;


            }
            public void onNothingSelected(AdapterView<?>  parent) {
            }
        });


        mypro_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner_flag=true;
                spinner.performClick();



            }
        });



        mypro_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetImageFromGallery();

            }
        });



        mypro_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(
                        getBaseContext(), // 현재화면의 제어권자
                        Fix_profile.class); // 다음넘어갈 화면
                intent.putExtra("fix_type", "text");;
                intent.putExtra("fix_data", profile_text.getText());
                startActivityForResult(intent,1);


            }
        });

        btn_like = (LinearLayout) findViewById(R.id.btn_like);
        btn_chat = (LinearLayout) findViewById(R.id.btn_chat);
        btn_list = (LinearLayout) findViewById(R.id.btn_list);



        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        getBaseContext(), // 현재화면의 제어권자
                        chat.class); // 다음넘어갈 화면
                intent.putExtra("user_token", user_token);
                getBaseContext().startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));

            }
        });



        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
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



        profile_img = (ImageView)findViewById(R.id.profile_img);

        ImageLoader.getInstance().displayImage(imageUri, profile_img,options);



        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        getBaseContext(), // 현재화면의 제어권자
                        img_view.class); // 다음넘어갈 화면
                intent.putExtra("img", "http://file.paranweb.co.kr/gay/profile_img_big/"+json_user_img);
                getBaseContext().startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));

            }
        });

        flag_frist=1;
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);



        // startActivity(new Intent(getActivity().getBaseContext(), Setkeyword.class));
        items.clear();


        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        coverimg = (ImageView) findViewById(R.id.imageView6);


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






        queue = Volley.newRequestQueue(this);
        String url = "http://file.paranweb.co.kr/gay/get_profile.php?uk="+userkey+"&at="+user_token;


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

                    jsno_btn_hit = jsonobject.getString("hit");
                    jsno_btn_list = jsonobject.getString("list_cnt");


                    profile_name.setText(json_user_name);
                    profile_text.setText(json_user_text);


                    profile_area.setText(json_user_carea);


                    profile_btn_like.setText(jsno_btn_hit);
                    profile_btn_list.setText(jsno_btn_list);

                    ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getBaseContext())
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

        stringRequest.setTag(TAG);
        queue.add(stringRequest);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
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


     //   new DownloadJSON().execute();

//호출부분







        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
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





        final RecyclerView  scrollView = (RecyclerView) findViewById(R.id.recyclerview);



        new DownloadJSON().execute();





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
            SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
            String userkey=preferences.getString("temp_key", "");

            jsonobject = JSONfunctions
                    .getJSONfromURL("http://file.paranweb.co.kr/gay/board_app_profile.php?uk="+userkey+"&at="+user_token+"&p="+ String.valueOf(page_int));


            Log.d("json_url", "http://file.paranweb.co.kr/gay/board_app_profile.php?uk="+userkey+"&at="+user_token+"&p="+ String.valueOf(page_int));
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

                    RecyclerAdapter=new RecyclerAdapter_profile(getBaseContext(), items, R.layout.fragment_first);

                    recyclerView.setAdapter(RecyclerAdapter);
                    re_view=1;

                }else {

                    RecyclerAdapter.additem(RecyclerAdapter.getItemCount()-jsonarray.length());
                }

                if(jsonarray.length()>0) {
                    swipeContainer.setVisibility(View.VISIBLE);
                    coverimg.setVisibility(View.GONE);

                }


            }else {
                Toast.makeText(getBaseContext(), "인터넷 연결 상태가 좋지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        }

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {

            SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
            String userkey=preferences.getString("temp_key", "");

            queue = Volley.newRequestQueue(this);
            String url = "http://file.paranweb.co.kr/gay/get_profile.php?uk="+userkey+"&at="+user_token;


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


                        jsno_btn_hit = jsonobject.getString("hit");
                        jsno_btn_list = jsonobject.getString("list_cnt");


                        profile_name.setText(json_user_name);
                        profile_text.setText(json_user_text);
                        profile_btn_like.setText(jsno_btn_hit);
                        profile_btn_list.setText(jsno_btn_list);

                        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getBaseContext())
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


                    } catch (JSONException e) {

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) ;


            stringRequest.setTag(TAG);
            queue.add(stringRequest);
        }

        Log.d("json_url_pro", "okcrop00");
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {

            Log.d("json_url_pro", "okcrop");
            final Uri resultUri = UCrop.getOutput(data);
            Log.d("json_url_pro", "okcrop2");
            Bundle bundle = data.getExtras();
            Bitmap bitmap = bundle.getParcelable("data");

            profile_img.setImageURI(uri_d);


            img_up_flag=1;

            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), uri_d);

                bm.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);


                byteArray = byteArrayOutputStream.toByteArray();

                ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

                img_up_i = img_up_i + 1;
                img_up_flag=1;
                uploadimg(ConvertImage);
                Log.d("json_url_pro", "upload23");

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("json_url_pro", "upload3");
            }

        } else if (resultCode == UCrop.RESULT_ERROR) {
            Log.d("json_url_pro", "okcrop333");
            final Throwable cropError = UCrop.getError(data);
            Log.d("json_url_pro", cropError.toString());
        }else if (requestCode == 2) {

            if (data != null) {
                uri = data.getData();
                Log.d("json_url_pro", "okintent");
                ImageCropFunction();
            }
        }
    }

    public void ImageCropFunction() {

        File path = new File (Environment.getExternalStorageDirectory()+"/crop");
        if (!path.exists()) {
            path.mkdirs();
        }


        file = new File(Environment.getExternalStorageDirectory()+"/crop",
                "/file" + String.valueOf(System.currentTimeMillis()));


        uri_d=Uri.fromFile(file);

        UCrop.of(uri, uri_d)
                .withAspectRatio(1, 1)
                .withMaxResultSize(1200, 1200)
                .start(this);
    }

    private String GetDevicesUUID() {
        String token = FirebaseInstanceId.getInstance().getToken();
        return token;
    }


    public void GetImageFromGallery(){

        GalIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(GalIntent, "사진첩에서 이미지를 가져옵니다."), 2);

    }


    private void uploadimg(String a){

        progressDialog = ProgressDialog.show(Profile.this,"글을 저장중입니다.","잠시만 기다려주세요",false,false);

        ConvertImage=a;


        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();


            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);



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


                String fileChk = uri_d.getPath();

                File file2 = new File(fileChk);

                if(file2.exists()==true) {

                    boolean deleted = file.delete();
                }
                progressDialog.dismiss();
            }

            @Override
            protected String doInBackground(Void... params) {

                Profile.ImageProcessClass imageProcessClass = new Profile.ImageProcessClass();

                HashMap<String, String> HashMapParams = new HashMap<String, String>();
                HashMapParams.put("sno", userkey);

                HashMapParams.put(ImageName, ConvertImage);
                Log.d("json_url_pro", "userkey:"+userkey);

                String FinalData = imageProcessClass.ImageHttpRequest(ServerUploadPath, HashMapParams);

                Log.d("json_url_pro", ServerUploadPath);
                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();


    }


    public class ImageProcessClass{

        public String ImageHttpRequest(String requestURL, HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {
                url = new URL(requestURL);

                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(20000);

                httpURLConnection.setConnectTimeout(20000);

                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoInput(true);

                httpURLConnection.setDoOutput(true);

                outputStream = httpURLConnection.getOutputStream();

                bufferedWriter = new BufferedWriter(

                        new OutputStreamWriter(outputStream, "UTF-8"));

                bufferedWriter.write(bufferedWriterDataFN(PData));

                bufferedWriter.flush();

                bufferedWriter.close();

                outputStream.close();

                RC = httpURLConnection.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReader.readLine()) != null){

                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            stringBuilder = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
                if (check)
                    check = false;
                else
                    stringBuilder.append("&");

                stringBuilder.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilder.append("=");

                stringBuilder.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilder.toString();
        }

    }

}
