package jsnet.proj.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class Reg extends AppCompatActivity {
    private static final String TAG = "MAIN";
    private TextView tv;
    private EditText etId;
    private EditText etPw;
    private Button btnSend;
    private RequestQueue queue;
    private  String get_success;
    private  String get_token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg);
        tv = findViewById(R.id.tvMain);
        etId = findViewById(R.id.etId);
        etPw = findViewById(R.id.etPw);
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }
}
