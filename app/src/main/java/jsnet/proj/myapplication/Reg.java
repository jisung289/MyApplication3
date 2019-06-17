package jsnet.proj.myapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Reg extends AppCompatActivity {
    private static final String TAG = "MAIN";
    private TextView tv;
    private ImageView btnSend;
    private RequestQueue queue;
    private  String get_success;
    private  String get_token;

    ArrayAdapter<CharSequence> adspin;
    private Boolean spinner_flag;

    private Spinner spinner;



    private EditText etId;
    private EditText etPw;
    private EditText knick;
    private EditText etPwr;
    private EditText etph;


    private TextView txt_area;
    private RadioButton radioButton;
    private RadioButton radioButton2;
    private String sex;
    private Login Login_a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        Login_a = (Login)Login.LoginA;


        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg);
        tv = findViewById(R.id.tvMain);
        etId = findViewById(R.id.etId);
        etPw = findViewById(R.id.etPw);

        knick= findViewById(R.id.knick);
        etPwr= findViewById(R.id.etPwr);
        etph= findViewById(R.id.etph);
        txt_area= findViewById(R.id.txt_area);
        radioButton = findViewById(R.id.radioButton);
        radioButton2 = findViewById(R.id.radioButton2);


        btnSend = findViewById(R.id.btnSend);
        queue = Volley.newRequestQueue(this);
        String url = "http://file.paranweb.co.kr/gay/reg_app.php";

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //Creating JsonObject from response String
                    JSONObject jsonObject= new JSONObject(response.toString());
                    get_success = jsonObject.getString("success");
                    get_token = jsonObject.getString("token");

                    if(get_success=="false"){
                        tv.setText("로그인 정보가 올바르지 않습니다.");
                    }else{

                        SharedPreferences pref =getBaseContext().getSharedPreferences("pref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("temp_key", get_token);
                        editor.commit();
                        Toast.makeText(getBaseContext(), "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(
                                getBaseContext(), // 현재화면의 제어권자
                                Mainviewpage.class); // 다음넘어갈 화면


                        getBaseContext().startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));

                        Login_a.finish();
                        finish();
                    }

                } catch (JSONException e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", etId.getText().toString());
                params.put("user_pass", etPw.getText().toString());
                params.put("user_knick", knick.getText().toString());
                params.put("user_area", txt_area.getText().toString());
                params.put("user_ph", etph.getText().toString());
                params.put("user_sex",sex);
                return params;
            }
        };

        stringRequest.setTag(TAG);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etPw.getText().toString().equals(etPwr.getText().toString())) {
                   if(radioButton.isChecked()){
                       sex="male";
                       queue.add(stringRequest);
                   }else if(radioButton2.isChecked()){
                        sex="female";
                       queue.add(stringRequest);
                    }else{
                       tv.setText("성별을 선택해주세요.");
                   }
                }else{
                    tv.setText("비밀번호 확인이 일치하지 않습니다.");
                }
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
                    txt_area.setText(adspin.getItem(position));



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

        txt_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner_flag=true;
                spinner.performClick();



            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }

}
