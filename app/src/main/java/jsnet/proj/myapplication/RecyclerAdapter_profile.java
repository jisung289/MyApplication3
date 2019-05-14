package jsnet.proj.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class RecyclerAdapter_profile extends RecyclerView.Adapter<RecyclerAdapter_profile.ViewHolder> {
    Context context;
    List<Item> items;
    int item_layout;
    private String A;
    private String skinnum;
    private DisplayImageOptions options;
    // public ImageLoader imageLoader = ImageLoader.getInstance();

    private Intent intent;
    public RecyclerAdapter_profile(Context context, List<Item> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;


        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        ImageLoader.getInstance().init(config);




        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.temp_img)
                .showImageForEmptyUri(R.drawable.temp_img)
                .showImageOnFail(R.drawable.temp_img)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

    }






    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Item item = items.get(position);
        final ViewHolder holder_f=holder;
        skinnum=item.getskin_num();

     //   imageLoader = new ImageLoader(context);
        String img=item.getImage();


        if(img.equals("noimg")) {
            holder.image.setVisibility(View.GONE);
        }else {


            holder.image.setVisibility(View.VISIBLE);


            ImageLoader.getInstance().displayImage("http://file.paranweb.co.kr/gay/board_img_small/" + img, holder_f.image,options);


        }


        String pro_img=item.getpro_img();




        ImageLoader.getInstance().displayImage("http://file.paranweb.co.kr/gay/profile_img_small/" + item.getpro_img(), holder_f.image2,options);


        holder.image2.setBackground(new ShapeDrawable(new OvalShape()));
        holder.image2.setClipToOutline(true);

        holder.title.setText(item.getTitle());

        holder.tv_writer.setText(item.getregdate());

        holder.tv_content.setText(item.getcontent());

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        context, // 현재화면의 제어권자
                        img_view.class); // 다음넘어갈 화면
                intent.putExtra("img", "http://file.paranweb.co.kr/gay/board_img_big/" + item.getImage());
                context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));

            }
        });

        holder.tv_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




            }
        });

        if(position==getItemCount()){
            holder.progressbar.setVisibility(ProgressBar.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        ImageView image2;
        TextView title;
        TextView tv_content;
        TextView tv_writer;
        CardView cardview;
        ProgressBar progressbar;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            image2 = (ImageView) itemView.findViewById(R.id.new_chat_proimg);
            title = (TextView) itemView.findViewById(R.id.title);
            cardview = (CardView) itemView.findViewById(R.id.cardview);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            tv_writer = (TextView) itemView.findViewById(R.id.tv_date);
        }
    }


    public void additem(int i) {
        //notifyDataSetChanged();

        notifyItemInserted(i);
    }




    public class MommooAsyncTask extends AsyncTask<String, Void, String> {

        public String result;

        private String img;
        private ImageView mHolder;

        public MommooAsyncTask(String position, ImageView holder) {
            img = position;
            mHolder = holder;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            try {

            //    imageLoader.DisplayImage(img, mHolder);

            } catch(Exception e) {
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }



}
