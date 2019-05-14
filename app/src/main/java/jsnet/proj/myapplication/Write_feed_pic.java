package jsnet.proj.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;

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
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class Write_feed_pic extends Activity {

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


    File file;
    Uri uri;
    Uri uri_d;


    Intent CamIntent, GalIntent, CropIntent ;

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

                GetImageFromGallery();


            }
        });

        UploadImageOnServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GetImageNameFromEditText = GetImageName.getText().toString();
                progressDialog = ProgressDialog.show(Write_feed_pic.this,"글을 저장중입니다.","잠시만 기다려주세요",false,false);

                UploadImageToServer();


            }
        });

    }



    public void ClickImageFromCamera() {

        CamIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        file = new File(Environment.getExternalStorageDirectory(),
                "file" + String.valueOf(System.currentTimeMillis()) + ".jpg");


        uri = FileProvider.getUriForFile(getBaseContext(), "jsnet.proj.myapplication.provider", file);


        Log.d("json_url_pro", uri.toString());
        CamIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);

        CamIntent.putExtra("return-data", true);

        startActivityForResult(CamIntent, 0);

    }

    public void GetImageFromGallery(){

        GalIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(GalIntent, "사진첩에서 이미지를 가져옵니다."), 2);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("json_url_pro", "okcrop00");
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {

            Log.d("json_url_pro", "okcrop");
            final Uri resultUri = UCrop.getOutput(data);
            Log.d("json_url_pro", "okcrop2");
            Bundle bundle = data.getExtras();
            Bitmap bitmap = bundle.getParcelable("data");
            ImageView a_img=(ImageView)findViewById(R.id.imageView);
            a_img.setImageURI(uri_d);

            if(Build.VERSION.SDK_INT >= 21) {
                GradientDrawable drawable=
                        (GradientDrawable) getBaseContext().getDrawable(R.drawable.wr_feed_img);
                a_img.setBackground(drawable);
                a_img.setClipToOutline(true);
            }


            img_up_flag=1;
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
                "file" + String.valueOf(System.currentTimeMillis()));


        uri_d=Uri.fromFile(file);

        UCrop.of(uri, uri_d)
                .withMaxResultSize(1200, 1200)
                .start(this);
    }










    private void up_all(){
        LinearLayout mLinearLayout =  (LinearLayout)findViewById(R.id.img_table);


        byteArrayOutputStream =new ByteArrayOutputStream();
        if(img_up_i<1){


            Log.d("json_url_pro", "upload1");

//마지막으로 bitmap으로 전환한다 ~~!

            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), uri_d);

                bm.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);


                byteArray = byteArrayOutputStream.toByteArray();

                ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

                uploadimg(ConvertImage);

                img_up_i = img_up_i + 1;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.d("json_url_pro", "upload2");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("json_url_pro", "upload3");
            }


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

                Log.d("json_url_pro", ServerUploadPath);
                Log.d("json_url_pro", b_sno);

                String fileChk = uri_d.getPath();

                File file2 = new File(fileChk);

                if(file2.exists()==true) {

                    boolean deleted = file.delete();
                }


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

                progressDialog = ProgressDialog.show(Write_feed_pic.this,"글을 저장중입니다.","잠시만 기다려주세요",false,false);
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

    public static String getURLDecode(String content){
        try {
//          return URLDecoder.decode(content, "utf-8");   // UTF-8
            return URLDecoder.decode(content, "euc-kr");  // EUC-KR
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
