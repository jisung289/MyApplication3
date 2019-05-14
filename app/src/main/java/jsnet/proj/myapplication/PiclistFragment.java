package jsnet.proj.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017-06-14.
 */

public class PiclistFragment extends Fragment
{
    private Intent intent;

    String myJSON;

    private String page="1";
    private int page_int=1;
    private int re_view=0;
    private int flag_frist=0;
    FloatingActionButton menu1,menu2,menu3 ;
    List<Item> items = new ArrayList<>();

    final int ITEM_SIZE = 6;
    ProgressDialog mProgressDialog;
    JSONObject jsonobject;
    JSONArray jsonarray;

    private ViewGroup container;
    private SwipeRefreshLayout swipeContainer;
    private ImageView coverimg;
    private Button setkeybtn;
    private GridLayoutManager layoutManager;


    private Handler mHandler = new Handler();

    private RecyclerView recyclerView;


    public static RecyclerView.LayoutManager mLayoutManager;



    RecyclerAdapter_piclist RecyclerAdapter = null;

    public PiclistFragment()
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
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_mynews, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recyclerview);



        // startActivity(new Intent(getActivity().getBaseContext(), Setkeyword.class));
        items.clear();


        swipeContainer = (SwipeRefreshLayout) layout.findViewById(R.id.swipe_layout);
        coverimg = (ImageView) layout.findViewById(R.id.imageView6);
        setkeybtn = (Button) layout.findViewById(R.id.button4);


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


        setkeybtn.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {




                    }
                }
        );


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

                    }
                }
        );


        return layout;

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
            SharedPreferences preferences = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
            String userkey=preferences.getString("token", "");

            jsonobject = JSONfunctions
                    .getJSONfromURL("http://file.paranweb.co.kr/gay/pic_app.php?uk="+userkey+"&p="+ String.valueOf(page_int));


            Log.d("json_url", "http://file.paranweb.co.kr/gay/board_app_my.php?p="+ String.valueOf(page_int));
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

                    RecyclerAdapter=new RecyclerAdapter_piclist(getActivity(), items, R.layout.fragment_first);


                    layoutManager = new GridLayoutManager(getActivity(), 6);
                    layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            int gridPosition = position % 5;
                            switch (gridPosition) {
                                case 0:
                                case 1:
                                case 2:
                                    return 2;
                                case 3:
                                case 4:
                                case 5:
                                    return 2;
                            }
                            return 0;
                        }
                    });
                    recyclerView.setLayoutManager(layoutManager);
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
                Toast.makeText(getActivity(), "인터넷 연결 상태가 좋지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        }

    }






}


