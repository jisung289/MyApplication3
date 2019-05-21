package jsnet.proj.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Administrator on 2016-06-10.
 */
public class Freepoint extends AppCompatActivity implements RewardedVideoAdListener {

    private RequestQueue queue;
    private Button view_ad;
    private RewardedVideoAd mRewardedVideoAd;
//


    JSONObject jsonobject;
    JSONArray jsonarray;
    private String json_user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Context context = getBaseContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.freepoint);

        view_ad = (Button) findViewById(R.id.view_ad);
        // Use an activity context to get the rewarded video instance.
        MobileAds.initialize(this, "ca-app-pub-3604815056468599~6438037497");


        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();

        view_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mRewardedVideoAd.isLoaded()) {
                    mRewardedVideoAd.show();
                }else{
                    Toast.makeText(getBaseContext(), "현재 수령 가능한 보상이 없습니다. 잠시후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });



    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }

    @Override
    public void onRewarded(RewardItem reward) {
        Toast.makeText(getBaseContext(), "광고포인트가 적립되었습니다.", Toast.LENGTH_SHORT).show();



        SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
        String userkey=preferences.getString("temp_key", "");

        queue = Volley.newRequestQueue(getBaseContext());
        String url = "http://file.paranweb.co.kr/gay/ad_point.php?uk="+userkey;


        Log.d("json_url_pro", url);

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("json_url_pro", "res");


                try {
                    //Creating JsonObject from response String
                    jsonobject= new JSONObject(response.toString());
                    json_user_name = jsonobject.getString("su");
                    if(json_user_name.equals("0")){
                        Toast.makeText(getBaseContext(), "오늘은 이미 좋아요를 하셨습니다.", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getBaseContext(), "좋아요!", Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) ;


        stringRequest.setTag("POINT");
        queue.add(stringRequest);

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Toast.makeText(this, "onRewardedVideoAdLeftApplication",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        Log.d("ads_test", "err:"+errorCode);
        Toast.makeText(this, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoStarted() {
        Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoCompleted() {
        Toast.makeText(this, "onRewardedVideoCompleted", Toast.LENGTH_SHORT).show();
    }
}
