package jsnet.proj.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Login extends AppCompatActivity {
    private static final String TAG = "MAIN";
    private TextView tv;
    private EditText etId;
    private EditText etPw;
    private Button btnSend;
    private TextView btnReg;
    private RequestQueue queue;
    private  String get_success;
    private  String get_token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        tv = findViewById(R.id.tvMain);
        etId = findViewById(R.id.etId);
        etPw = findViewById(R.id.etPw);
        btnSend = findViewById(R.id.btnSend);
        btnReg = findViewById(R.id.textView3);

        queue = Volley.newRequestQueue(this);
        String url = "http://file.paranweb.co.kr/gay/login_app.php";

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
                return params;
            }
        };

        stringRequest.setTag(TAG);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queue.add(stringRequest);
            }
        });

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        getBaseContext(), // 현재화면의 제어권자
                        Reg.class); // 다음넘어갈 화면




                getBaseContext().startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));
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
