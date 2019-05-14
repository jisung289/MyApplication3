package jsnet.proj.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


/**
 * Created by Administrator on 2017-06-14.
 */

public class Mainviewpage extends AppCompatActivity implements ActionBar.TabListener {
    public static ViewPager vp;
    FragmentStatePagerAdapter pagerAdapter;


    private Activity Mainviewpage = this;



    private String token;

    private Intent intent;
    private String temp_key;
    public static Activity AActivity;
    private Fragment targetFragment;
    final static String FRAGMENT_TAG = "FRAGMENTB_TAG";


    private ImageView btn1;
    private ImageView btn2;
    private ImageView btn3;
    private ImageView btn4;
    private ImageView btn5;
    private Context context;
    @Override


    protected void onCreate(Bundle savedInstanceState)
    {


        context = getApplicationContext();
        SharedPreferences preferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        temp_key=preferences.getString("temp_key", "");
        if(temp_key=="") {

            Intent intent = new Intent(
                    context, // 현재화면의 제어권자
                    Login.class); // 다음넘어갈 화면


            context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));
            finish();
        }else{
            Toast.makeText(context, temp_key, Toast.LENGTH_SHORT).show();
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager);





        ActionBar actionBar = getSupportActionBar();

        // Custom Actionbar를 사용하기 위해 CustomEnabled을 true 시키고 필요 없는 것은 false 시킨다
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.


        vp = (ViewPager)findViewById(R.id.vp);
        pagerAdapter=new pagerAdapter(getSupportFragmentManager());

        vp.setOffscreenPageLimit(3);
        //저장되는 최대한의 프래그먼트

        vp.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override //스와이프로 페이지 이동시 호출됨
            public void onPageSelected(int position) {
                //swipe - 손가락을 화면에 댄 후, 일직선으로 드래그했다가 손을 떼는 동작이다.
                //화면을 좌우로 스와이핑하여 섹션 사이를 이동할 때, 현재 선택된 탭의 위치이다.

                //액션바의 탭위치를 페이지 위치에 맞춘다.
                //   actionBar.setSelectedNavigationItem(position);


                btn1.setImageResource(R.drawable.ic_list_off);
                btn2.setImageResource(R.drawable.ic_img_off);
                btn3.setImageResource(R.drawable.ic_member_off);
                btn4.setImageResource(R.drawable.ic_msg_off);
                btn5.setImageResource(R.drawable.ic_dot_off);


                if(position==0){

                    btn1.setImageResource(R.drawable.ic_list_on);
                }

                if(position==1){
                    btn2.setImageResource(R.drawable.ic_img_on);
                }


                if(position==2){
                    btn3.setImageResource(R.drawable.ic_member_on);
                }


                if(position==3){
                    btn4.setImageResource(R.drawable.ic_msg_on);
                }


                if(position==4){
                    btn5.setImageResource(R.drawable.ic_dot_on);
                }


                /*
                ImageView btn_first = (ImageView)findViewById(R.id.btn1);
                ImageView btn_second = (ImageView)findViewById(R.id.btn2);
                ImageView btn_third = (ImageView)findViewById(R.id.btn3);



                if(position==0){
                    btn_first.setImageResource(R.drawable.heart_on);
                    btn_second.setImageResource(R.drawable.heart_off);
                    btn_third.setImageResource(R.drawable.heart_off);
                }

                if(position==1){
                    btn_first.setImageResource(R.drawable.heart_off);
                    btn_second.setImageResource(R.drawable.heart_on);
                    btn_third.setImageResource(R.drawable.heart_off);
                }

                if(position==2){
                    btn_first.setImageResource(R.drawable.heart_off);
                    btn_second.setImageResource(R.drawable.heart_off);
                    btn_third.setImageResource(R.drawable.heart_on);
                }

                  */

            }
        });

        vp.setAdapter(pagerAdapter);
        vp.setCurrentItem(0);



        btn1 = (ImageView)findViewById(R.id.btn1);
        btn2 = (ImageView)findViewById(R.id.btn2);
        btn3= (ImageView)findViewById(R.id.btn3);
        btn4 = (ImageView)findViewById(R.id.btn4);
        btn5= (ImageView)findViewById(R.id.btn5);

        btn1.setOnClickListener(movePageListener);
        btn1.setTag(0);
        btn2.setOnClickListener(movePageListener);
        btn2.setTag(1);
        btn3.setOnClickListener(movePageListener);
        btn3.setTag(2);
        btn4.setOnClickListener(movePageListener);
        btn4.setTag(3);
        btn5.setOnClickListener(movePageListener);
        btn5.setTag(4);


/*
        ImageView btn_first = (ImageView)findViewById(R.id.btn1);
        ImageView btn_second = (ImageView)findViewById(R.id.btn2);
        ImageView btn_third = (ImageView)findViewById(R.id.btn3);

        btn_first.setOnClickListener(movePageListener);
        btn_first.setTag(0);
        btn_second.setOnClickListener(movePageListener);
        btn_second.setTag(1);
        btn_third.setOnClickListener(movePageListener);
        btn_third.setTag(2);



       btn_third.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        //여기에 이벤트를 적어주세요

                  //      SharedPreferences pref =  getSharedPreferences("pref", MODE_PRIVATE);
                 //       SharedPreferences.Editor editor = pref.edit();
                 //       editor.putString("token", "");
                //        editor.commit();
                //        startActivity(new Intent(context, Loginactivity.class));
                //        AActivity.finish();

                    }
                }
        );*/



        View.OnClickListener movePageListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int tag = (int) v.getTag();
                vp.setCurrentItem(tag);
            }
        };


        //각각의 섹션을 위한 탭을 액션바에 추가한다.
        for (int i = 0; i <pagerAdapter.getCount(); i++) {
        /*    actionBar.addTab(
                    actionBar.newTab()
                            //어댑터에서 정의한 페이지 제목을 탭에 보이는 문자열로 사용한다.
                       //     .setText(pagerAdapter.getPageTitle(i))
                            //TabListener 인터페이스를 구현할 액티비티 오브젝트도 지정한다.
                            .setTabListener(this)
                            .setIcon(R.drawable.tab_01)
            );*/
        }









    }


    @Override //액션바의 탭 선택시 호출됨
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
        //액션바에서 선택된 탭에 대응되는 페이지를 뷰페이지에서 현재 보여지는 페이지로 변경한다.
        vp.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    View.OnClickListener movePageListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            int tag = (int) v.getTag();
            vp.setCurrentItem(tag);
        }
    };

    public Fragment findFragmentByPosition(int position) {
        return getSupportFragmentManager().findFragmentByTag(
                "android:switcher:" + vp.getId() + ":"
                        + ((FragmentPagerAdapter) vp.getAdapter()).getItemId(position));
    }




    private class pagerAdapter extends FragmentStatePagerAdapter
    {
        public pagerAdapter(android.support.v4.app.FragmentManager fm)
        {
            super(fm);
        }
        @Override
        public android.support.v4.app.Fragment getItem(int position)
        {

            switch(position)
            {
                case 0:
                    return new SinglelineFragment();
                case 1:
                    return new MainFragment();
                case 2:
                    return new ChatlistFragment();
                case 3:
                    return new MemberlistFragment();
                case 4:
                    return new SettingFragment();
                default:
                    return null;
            }
        }
        @Override
        public int getCount()
        {
            return 5;
        }

        public int getItemPosition(Object object){
            return POSITION_NONE;
        }
        //탭의 제목으로 사용되는 문자열 생성
        @Override
        public CharSequence getPageTitle(int position) {
            return "Section " + (position + 1);
        }



    }

    public Fragment findFragmentByPosition_new(int position) {
        return getSupportFragmentManager().findFragmentByTag(
                "android:switcher:" + vp.getId() + ":"
                        + ((FragmentPagerAdapter) vp.getAdapter()).getItemId(position));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {

            Log.d("json_url_pro", "called");
            vp.setAdapter(pagerAdapter);
            vp.setCurrentItem(0);

        }
        if (requestCode == 2) {

            Log.d("json_url_pro", "called");
            vp.setAdapter(pagerAdapter);
            vp.setCurrentItem(1);

        }
    }


}
