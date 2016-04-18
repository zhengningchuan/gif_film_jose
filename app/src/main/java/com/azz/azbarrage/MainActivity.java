package com.azz.azbarrage;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.droidsonroids.gif.GifImageView;


public class MainActivity extends FragmentActivity implements ViewPager.OnPageChangeListener,SeekBar.OnSeekBarChangeListener {
    //两两弹幕之间的间隔时间
    public static final int DELAY_TIME = 500;

    /**
     * 标签：程序是否处于暂停状态
     * 15/11/01 测试按Home后一分钟以上回到程序会发生满屏线程阻塞
     */
    private boolean isOnPause = false;
    GifImageView gifView;
    MyViewPager viewPager;
    SeekBar seekBar;
    TextView currentPageTv;
    RelativeLayout bottom_layout;
    private int totalNum = 2;
    private int seekPosition=0;
    int startX = 0;
    private Random random = new Random();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gifView = (GifImageView) this.findViewById(R.id.gifview);
        viewPager = (MyViewPager) this.findViewById(R.id.viewpager);
        seekBar = (SeekBar) this.findViewById(R.id.sb_detail_play_progress);
        currentPageTv = (TextView) this.findViewById(R.id.current_page_tv);
        bottom_layout = (RelativeLayout) this.findViewById(R.id.bottom_layout);
        List<GifFragment> gifFragments = new ArrayList<GifFragment>();
        gifFragments.add(new GifFragment(R.drawable.anim_flag_england));
        gifFragments.add(new GifFragment(R.drawable.anim_flag_iceland));
        viewPager.setAdapter(new FragAdapter(getSupportFragmentManager(),gifFragments));
        seekBar.setOnSeekBarChangeListener(this);
        viewPager.addOnPageChangeListener(this);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int endX;
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX =(int) event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        endX = (int) event.getX();
                        if(startX==endX){
                            if(bottom_layout.getVisibility() == View.VISIBLE){
                                bottom_layout.setVisibility(View.INVISIBLE);
                            }else {
                                bottom_layout.setVisibility(View.VISIBLE);
                            }
                        }
                        startX = 0;
                        break;
                }
                return false;
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        isOnPause = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isOnPause = false;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPageTv.setText(position+1+"");
        seekBar.setProgress((int)(((float)position/totalNum)*100));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        seekPosition =(int) ((progress/100.0)*2);
        currentPageTv.setText(seekPosition+1+"");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        viewPager.setCurrentItem(seekPosition);
    }

    public class GifFragment extends Fragment{
        int resId;
        GifImageView gifImageView;
        RelativeLayout rootlayout;
        public GifFragment(){
            super();
        }

        public GifFragment(int resId) {
            this.resId = resId;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view= inflater.inflate(R.layout.gif_imageview_fragment_layout, container, false);
            gifImageView = (GifImageView) view.findViewById(R.id.gifview);
            gifImageView.setBackgroundResource(resId);
            gifImageView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                }
            });
            rootlayout = (RelativeLayout) view.findViewById(R.id.rootlayout);
            //读取文字资源
            final String[] texts = getResources().getStringArray(R.array.default_text_array);

            //设置宽高全屏
            final ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            final Handler handler = new Handler();
            Runnable createBarrageView = new Runnable() {
                @Override
                public void run() {
                    if (!isOnPause) {
                        Log.e("azzz", "发送弹幕");
                        //新建一条弹幕，并设置文字
                        final BarrageView barrageView = new BarrageView(MainActivity.this);
                        barrageView.setText(texts[random.nextInt(texts.length)]); //随机设置文字
                        rootlayout.addView(barrageView, lp);
                    }
                    //发送下一条消息
                    handler.postDelayed(this, DELAY_TIME);
                }
            };
            handler.post(createBarrageView);
            return view;
        }
    }

    public class FragAdapter extends FragmentPagerAdapter {

        private List<GifFragment> mFragments;

        public FragAdapter(FragmentManager fm, List<GifFragment> fragments) {
            super(fm);
            // TODO Auto-generated constructor stub
            mFragments=fragments;
        }

        @Override
        public Fragment getItem(int arg0) {
            // TODO Auto-generated method stub
            return mFragments.get(arg0);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mFragments.size();
        }

    }
}
