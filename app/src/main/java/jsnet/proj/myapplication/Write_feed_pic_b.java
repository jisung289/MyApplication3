package jsnet.proj.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

public class Write_feed_pic_b extends Activity {

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

    String ServerUploadPath ="http://file.paranweb.co.kr/gay/save_feed.php" ;
    String Frist_insert ="http://file.paranweb.co.kr/gay/save_feed_first.php" ;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wr_feed_pic );



        SharedPreferences preferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        userkey=preferences.getString("temp_key", "");
        user_data=userkey;


         GetImageFromGalleryButton = (Button)findViewById(R.id.albumBtn);

        UploadImageOnServerButton = (Button)findViewById(R.id.wr_btn_ok);

        ShowSelectedImage = (ImageView)findViewById(R.id.image);

        GetImageName = (EditText)findViewById(R.id.editText);

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
               progressDialog = ProgressDialog.show(Write_feed_pic_b.this,"글을 저장중입니다.","잠시만 기다려주세요",false,false);

                UploadImageToServer();


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

                progressDialog = ProgressDialog.show(Write_feed_pic_b.this,"글을 저장중입니다.","잠시만 기다려주세요",false,false);
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
                HashMapParams.put(ImageTag, GetImageNameFromEditText);
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
