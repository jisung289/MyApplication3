package jsnet.proj.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import uk.co.senab.photoview.PhotoViewAttacher;

public class img_view extends Activity {
	private static String TAG = "camera";
	private static final int REQUEST_IMAGE_CAPTURE = 1;
	private static final int REQUEST_IMAGE_ALBUM = 2;
	private static final int REQUEST_IMAGE_CROP = 3;
	private ImageView mImageView;
	private String mCurrentPhotoPath;
	private String mCurrentPhotoPath2;
	private String mCurrentPhotoPath3;
	private Uri contentUri;
	PhotoViewAttacher mAttacher;
	ImageLoader imageLoader;
	private Handler mHandler;
	private Runnable mRunnable;
	@Override
	protected void onCreate(Bundle savedInstanceState) {


		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_img);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

		Intent intent = getIntent(); // 값을 받아온다.
		String img_url=(String)intent.getExtras().get("img");

		ImageView img = (ImageView)findViewById(R.id.imgview);

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


		if(img_url.startsWith("http")){

			ImageLoader.getInstance().displayImage(img_url, img,options);
		}else{

			ImageLoader.getInstance().displayImage("http://file.paranweb.co.kr/gay/user_img_big/"+img_url, img,options);
		}

		Log.d("json_url", String.valueOf("http://file.paranweb.co.kr/gay/user_img_big/"+img_url));

		mRunnable = new Runnable() {
			@Override
			public void run() {

				mAttacher=new PhotoViewAttacher((ImageView)findViewById(R.id.imgview));
			}
		};

		mHandler = new Handler();
		mHandler.postDelayed(mRunnable, 500);

	}



}
