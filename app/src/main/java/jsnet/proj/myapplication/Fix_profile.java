package jsnet.proj.myapplication;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class Fix_profile extends AppCompatActivity {

    int img_up_flag=0;
    int rotate = 0;
    int img_up_cnt = 0;
    int img_up_i = 0;
    Button GetImageFromGalleryButton, UploadImageOnServerButton;
    private String userkey;
    ImageView ShowSelectedImage;

    EditText GetImageName;
    int mIndex = 1;
    Bitmap FixBitmap;
    Bitmap FixBitmapa[];
    String user_data = "" ;
    String ImageTag = "image_tag" ;
    String b_sno = "" ;

    String ImageName = "image_data" ;

    String ServerUploadPath ="http://file.paranweb.co.kr/gay/save_pro.php" ;
    String Frist_insert ="http://file.paranweb.co.kr/gay/save_pro_first.php" ;

    ProgressDialog progressDialog ;

    ByteArrayOutputStream byteArrayOutputStream ;
    ByteArrayOutputStream byteArrayOutputStreama[] ;

    byte[] byteArray ;

    String ConvertImage ;

    String GetImageNameFromEditText;

    HttpURLConnection httpURLConnection ;

    URL url;

    OutputStream outputStream;

    BufferedWriter bufferedWriter ;

    int RC ;

    BufferedReader bufferedReader ;

    StringBuilder stringBuilder;

    boolean check = true;
    private TextView text_length;
    private TextView text_in;
    private String fix_type;
    private String fix_data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wr_feed);


        Intent intent = getIntent(); // 값을 받아온다.
        fix_type=(String)intent.getExtras().get("fix_type");
        fix_data=(String)intent.getExtras().get("fix_data");





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


        ImageView btn = (ImageView) findViewById(R.id.my_news_list);
        text_length = (TextView) findViewById(R.id.text_length);

        btn.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        setResult(RESULT_OK);
                        finish();


                    }
                }
        );



        SharedPreferences preferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        userkey=preferences.getString("temp_key", "");
        user_data=userkey;


         GetImageFromGalleryButton = (Button)findViewById(R.id.albumBtn);

        UploadImageOnServerButton = (Button)findViewById(R.id.wr_btn_ok);

        ShowSelectedImage = (ImageView)findViewById(R.id.image);

        GetImageName = (EditText)findViewById(R.id.editText);

        if(fix_type.startsWith("knick")){
            GetImageName.setHint("변경할 닉네임을 입력하세요.");
            text_length.setText("닉네임은 15글자까지 가능합니다.");
        }else{
            GetImageName.setHint("변경할 소개말을 입력하세요.");
            text_length.setText("변경할 소개말은 3줄만 가능합니다.");
        }
        GetImageName.setText(fix_data);

        byteArrayOutputStream = new ByteArrayOutputStream();


        GetImageFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();

                intent.setType("image/*");

                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);

                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), 1);




            }
        });

        UploadImageOnServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GetImageNameFromEditText = GetImageName.getText().toString();
                if(GetImageNameFromEditText.equals("")) {
                    Toast.makeText(getBaseContext(), "변경할 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
               progressDialog = ProgressDialog.show(Fix_profile.this,"글을 저장중입니다.","잠시만 기다려주세요",false,false);

                UploadImageToServer();


            }
        });



        GetImageName.post(new Runnable() {
            @Override
            public void run() {
                GetImageName.setFocusableInTouchMode(true);
                GetImageName.requestFocus();

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

                imm.showSoftInput(GetImageName,0);

            }
        });



        GetImageName.addTextChangedListener(new TextWatcher()
        {
            String previousString = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                previousString= s.toString();
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if(fix_type.startsWith("knick")){

                            ((EditText)findViewById(R.id.editText)).setFilters(new InputFilter[] {new InputFilter.LengthFilter(15)});

                }else{
                    if (GetImageName.getLineCount() >= 4)
                    {
                        GetImageName.setText(previousString);
                        GetImageName.setSelection(GetImageName.length());

                        text_length.setTextColor(Color.parseColor("#da2f2f"));
                    }
                }

            }
        });
    }



    @Override
    protected void onActivityResult(int RC, int RQC, Intent I) {

        super.onActivityResult(RC, RQC, I);

        if (RC == 1 && RQC == RESULT_OK && I != null && I.getData() != null) {

            Uri uri = I.getData();


            try {
                ClipData clipData = I.getClipData();


                    FixBitmapa = new Bitmap[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        img_up_cnt=img_up_cnt+1;
                        FixBitmapa[i] = MediaStore.Images.Media.getBitmap(getContentResolver(), clipData.getItemAt(i).getUri());
                        LinearLayout mLinearLayout =  (LinearLayout)findViewById(R.id.img_table);

                        // ImageView 객체 생성
                        ImageView iv = new ImageView(this);
                        iv.setImageBitmap(FixBitmapa[i]);
                        iv.setId(mIndex);
                        iv.setAdjustViewBounds(true);
                        iv.setLayoutParams(new Gallery.LayoutParams(
                                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
                        iv.setScaleType(ImageView.ScaleType.FIT_XY); // 레이아웃 크기에 이미지를 맞춘다

                        mLinearLayout.addView(iv);
                        mIndex=mIndex+1;
                    }

            //씨발 이미지 회전!!!!!!!!!!!!

                img_up_flag=1;
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    private void up_all(){
        LinearLayout mLinearLayout =  (LinearLayout)findViewById(R.id.img_table);


        byteArrayOutputStream =new ByteArrayOutputStream();
        if(img_up_i<mLinearLayout.getChildCount()){

            FixBitmapa[img_up_i].compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);


            byteArray = byteArrayOutputStream.toByteArray();

            ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

            uploadimg(ConvertImage);

            img_up_i = img_up_i + 1;
        }else{
            progressDialog.dismiss();
            setResult(RESULT_OK);


            finish();
        }



    }


    private void uploadimg(String a){


        ConvertImage=a;


        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();


            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                up_all();
            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String, String> HashMapParams = new HashMap<String, String>();
                HashMapParams.put("sno", b_sno);

                if(img_up_flag==1) {

                    HashMapParams.put(ImageName, ConvertImage);
                }

                String FinalData = imageProcessClass.ImageHttpRequest(ServerUploadPath, HashMapParams);

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();


    }



    private String getRealPathFromURI(Uri contentUri) {
        int column_index=0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(column_index);
    }

    public void UploadImageToServer(){

        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                progressDialog = ProgressDialog.show(Fix_profile.this,"글을 저장중입니다.","잠시만 기다려주세요",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);
                b_sno=string1;
                up_all();
            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String, String> HashMapParams = new HashMap<String, String>();

                HashMapParams.put("user_data", user_data);
                HashMapParams.put("pro_data", GetImageNameFromEditText);
                HashMapParams.put("pro_type", fix_type);
                String FinalData = imageProcessClass.ImageHttpRequest(Frist_insert, HashMapParams);

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


    public int exifOrientationToDegrees(int exifOrientation)
    {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
        {
            return 90;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
        {
            return 180;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
        {
            return 270;
        }
        return 0;
    }

    /**
     * 이미지를 회전시킵니다.
     *
     * @param bitmap 비트맵 이미지
     * @param degrees 회전 각도
     * @return 회전된 이미지
     */
    public Bitmap rotate(Bitmap bitmap, int degrees)
    {
        if(degrees != 0 && bitmap != null)
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try
            {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted)
                {
                    bitmap.recycle();
                    bitmap = converted;
                }
            }
            catch(OutOfMemoryError ex)
            {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }


}
