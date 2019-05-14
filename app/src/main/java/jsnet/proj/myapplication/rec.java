package jsnet.proj.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by Administrator on 2017-09-26.
 */

public class rec extends Activity {
    Button uploadButton;
    int serverResponseCode = 0;
    final static int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 100;
    final static int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 200;

    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;


    Context context;
    public static Context mContext;
    ProgressDialog dialog = null;
    ProgressBar progressBar = null;
    int count= 1;
    String upLoadServerUri = null;



    private Handler mHandler;
    private Runnable mRunnable;


    /**********  File Path *************/
    final String uploadFilePath = "/sdcard/";//경로를 모르겠으면, 갤러리 어플리케이션 가서 메뉴->상세 정보
    final String uploadFileName = "recorded.mp4"; //전송하고자하는 파일 이름

    final String RECORDED_FILE = "/sdcard/recorded.mp4";

    MediaPlayer player;
    MediaRecorder recorder;
    CountDownTimer timer;
    int playbackPosition = 0;

    final int rec_time=10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rec);
        context = getApplicationContext();
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        upLoadServerUri = "http://car2.paranweb.co.kr/save_rec.php";//서버컴퓨터의 ip주소



        try {
            File file = new File(RECORDED_FILE);
            file.delete();
        }
        catch (Exception ex){
            Log.e("SampleAudioRecorder", "Exception : ", ex);
        }

        checkPermission();

        Button recordBtn = (Button) findViewById(R.id.recordBtn);
        Button recordStopBtn = (Button) findViewById(R.id.recordStopBtn);
        Button playBtn = (Button) findViewById(R.id.playBtn);
        Button playStopBtn = (Button) findViewById(R.id.playStopBtn);
        Button sendbtn = (Button) findViewById(R.id.send);


        final CountDownTimer timer = new CountDownTimer(rec_time*1000, 1) {
            @Override
            public void onFinish() {
                ImageView im2 = (ImageView)  findViewById(R.id.imageView8);
                im2.setImageResource(R.drawable.h2);

                this.cancel();
                recode_end();
            }

            @Override
            public void onTick(long millisUntilFinished) {

                TextView buttonName = (TextView)findViewById(R.id.time_txt);
                String sec= Long.toString(millisUntilFinished/1000);
                buttonName.setText( sec+"초 남음");
                count++;

                progressBar.setProgress((int)(100 * rec_time*10 - millisUntilFinished)/(rec_time*10));
            }
        };

        //타이머 시작



        final ImageView im2 = (ImageView)  findViewById(R.id.imageView8);
        im2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // 버튼을 눌렀을 때
                    im2.setImageResource(R.drawable.h2_on);
                    timer.start();
                    recode_start();
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    // 버튼을 누른 상태로 움직이고 있을 때
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // 버튼에서 손을 떼었을 때
                    im2.setImageResource(R.drawable.h2);

                    timer.cancel();
                    recode_end();
                }
                // 이벤트 처리를 여기서 완료했을 때
                // 다른곳에 이벤트를 넘기지 않도록
                // 리턴값을 true 로 준다
                // 리턴값이 있음
                return true;
            }
        });





        recordBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
        recordStopBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });




        playBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                try{
                    playAudio(RECORDED_FILE);

                    Toast.makeText(getApplicationContext(), "음악파일 재생 시작됨.", LENGTH_SHORT).show();
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        playStopBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                if(player != null){
                    playbackPosition = player.getCurrentPosition();
                    player.pause();
                    Toast.makeText(getApplicationContext(), "음악 파일 재생 중지됨.",LENGTH_SHORT).show();
                }
            }
        });


        sendbtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                dialog = ProgressDialog.show(rec.this, "", "Uploading file...", true);

                new Thread(new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                            }
                        });

                        uploadFile(RECORDED_FILE);

                    }
                }).start();

            }
        });

    }
    private void playAudio(String url) throws Exception {
        killMediaPlayer();

        player = new MediaPlayer();
        player.setDataSource(url);
        player.prepare();
        player.start();
    }

    protected void onDestroy() {
        super.onDestroy();
        killMediaPlayer();
    }

    private void killMediaPlayer() {
        if(player != null){
            try {
                player.release();
            } catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    protected void onPause(){
        if(recorder != null){
            recorder.release();
            recorder = null;
        }
        if (player != null){
            player.release();
            player = null;
        }

        super.onPause();

    }



    public int uploadFile(String sourceFileUri) {

        String fileName = sourceFileUri;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

            dialog.dismiss();

            Log.e("uploadFile", "Source File not exist :"
                    +uploadFilePath + "" + uploadFileName);

            runOnUiThread(new Runnable() {
                public void run() {

                    //파일없을때

                    Toast.makeText(rec.this, "업로드할 파일을 찾을수 없습니다.",
                            Toast.LENGTH_SHORT).show();

                }
            });

            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                final String serverResponseMessage = conn.getResponseMessage();
                final String restext;

                // 응답 내용(BODY) 구하기
                try (InputStream in = conn.getInputStream();
                     ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                    byte[] buf = new byte[1024 * 8];
                    int length = 0;
                    while ((length = in.read(buf)) != -1) {
                        out.write(buf, 0, length);
                    }
                    restext=new String(out.toByteArray(), "UTF-8");
                }



                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {

                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                    +uploadFileName;

                            if(restext.startsWith("ok:")) {
                                final String[] data = restext.split("ok:");
                                ((chat) chat.mContext).send_chat("김지성", "audio:" + data[1]);
                                finish();
                            }else {
                                Toast.makeText(rec.this, "알수없는 에러가 발생하였습니다. 다시 시도해주세요. err:"+restext,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }



                try {

                    File file = new File(RECORDED_FILE);
                    file.delete();
                }
                catch (Exception ex){
                    Log.e("SampleAudioRecorder", "Exception : ", ex);
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(rec.this, "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(rec.this, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            dialog.dismiss();
            return serverResponseCode;

        } // End else block
    }


    public void m_onClick() {


        new CountDownTimer(60* 1000, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
                Button buttonName = (Button)findViewById(R.id.recordBtn);
                buttonName.setText(count+":"+millisUntilFinished);
                count++;

                progressBar.setProgress((int)(100 * 600 - millisUntilFinished)/ 600);
            }

            @Override
            public void onFinish() {
            }
        }.start();
    }

    public void recode_start(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }



        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }
        try{
            if( recorder == null ) {
                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

                recorder.setOutputFile(RECORDED_FILE);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                Toast.makeText(getApplicationContext(),
                        "녹음을 시작합니다.", Toast.LENGTH_LONG).show();
                recorder.prepare();
                recorder.start();
            }//if
        }catch (Exception ex){
            Log.e("SampleAudioRecorder", "Exception : ", ex);
        }
    }



    public void checkPermission(){

        int permissionCheck_RECORD = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int permissionCheck_WRITE = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
//(ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)

            if ( (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.RECORD_AUDIO)) ||
                    (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))){

// 다이어로그같은것을 띄워서 사용자에게 해당 권한이 필요한 이유에 대해 설명합니다
                // 해당 설명이 끝난뒤 requestPermissions()함수를 호출하여 권한허가를 요청해야 합니다

            } else {

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

// 필요한 권한과 요청 코드를 넣어서 권한허가요청에 대한 결과를 받아야 합니다

            }
        }
    }


    public void recode_end(){
        try{

            if(recorder == null) {
                return;
            }

            recorder.stop();
            recorder.release();
            recorder = null;

            Toast.makeText(getApplicationContext(),
                    "녹음이 중지되었습니다.", Toast.LENGTH_LONG).show();
            // TODO Auto-generated method stub
        } catch(Exception e){
            e.printStackTrace();
        }
    }


}
