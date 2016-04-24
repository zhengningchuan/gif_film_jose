package com.jose.gifmovie;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jose.adapter.CommentsListViewAdapter;
import com.jose.gifmovie.R;
import com.jose.model.CommentsModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.droidsonroids.gif.GifImageView;


public class MainActivity extends FragmentActivity implements ViewPager.OnPageChangeListener, SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    //两两弹幕之间的间隔时间
    public static final int DELAY_TIME = 500;

    /**
     * 标签：程序是否处于暂停状态
     * 15/11/01 测试按Home后一分钟以上回到程序会发生满屏线程阻塞
     */
    private static boolean isOnPause = false;
    private static boolean isCommentsOn = true;
    GifImageView gifView;
    MyViewPager viewPager;
    SeekBar seekBar;
    TextView currentPageTv;
    RelativeLayout bottom_layout;
    LinearLayout bottom_describe_layout;
    RelativeLayout top_bar_layout;
    ImageView comments_icon_iv;
    ImageView shoucang_icon_iv;
    ImageView share_icon_iv;
    ImageView tanmu_swicher_iv;
    ImageView back_iv;
    TextView movie_tittle_tv;
    TextView movie_info_tv;
    TextView comments_switcher_tv;
    ListView commentsListView;
    EditText commentsEditText;
    RelativeLayout commentsLayout;

    private Animation playAnimation;
    private Animation describeAnimation;
    private int totalNum = 2;
    private int seekPosition = 0;
    int startX = 0;
    private static Random random = new Random();
    private FragAdapter fragAdapter;
    private List<CommentsModel> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gifView = (GifImageView) this.findViewById(R.id.gifview);
        viewPager = (MyViewPager) this.findViewById(R.id.viewpager);
        seekBar = (SeekBar) this.findViewById(R.id.sb_detail_play_progress);
        currentPageTv = (TextView) this.findViewById(R.id.current_page_tv);
        bottom_layout = (RelativeLayout) this.findViewById(R.id.bottom_layout);
        top_bar_layout = (RelativeLayout) this.findViewById(R.id.top_bar_layout);
        bottom_describe_layout = (LinearLayout) this.findViewById(R.id.bottom_describe_layout);
        comments_icon_iv = (ImageView) this.findViewById(R.id.comments_icon_iv);
        shoucang_icon_iv = (ImageView) this.findViewById(R.id.shoucang_icon_iv);
        share_icon_iv = (ImageView) this.findViewById(R.id.share_icon_iv);
        tanmu_swicher_iv = (ImageView) this.findViewById(R.id.tanmu_swicher_iv);
        back_iv = (ImageView) this.findViewById(R.id.back_iv);
        movie_tittle_tv = (TextView) this.findViewById(R.id.movie_tittle_tv);
        movie_info_tv = (TextView) this.findViewById(R.id.movie_info_tv);
        commentsListView = (ListView) this.findViewById(R.id.comments_lv);
        comments_switcher_tv = (TextView) this.findViewById(R.id.comments_switcher_tv);
        commentsEditText = (EditText) this.findViewById(R.id.comments_edtitext);
        commentsLayout = (RelativeLayout) this.findViewById(R.id.comments_layout);
        comments_icon_iv.setOnClickListener(this);
        shoucang_icon_iv.setOnClickListener(this);
        comments_switcher_tv.setOnClickListener(this);
        share_icon_iv.setOnClickListener(this);
        tanmu_swicher_iv.setOnClickListener(this);
        back_iv.setOnClickListener(this);
        List<GifFragment> gifFragments = new ArrayList<GifFragment>();
        gifFragments.add(GifFragment.newInstance(R.drawable.anim_flag_england));
        gifFragments.add(GifFragment.newInstance(R.drawable.anim_flag_iceland));
        fragAdapter = new FragAdapter(getSupportFragmentManager(), gifFragments);
        dataList = new ArrayList<CommentsModel>();
        dataList.add(new CommentsModel());
        dataList.add(new CommentsModel());
        dataList.add(new CommentsModel());
        commentsListView.setAdapter(new CommentsListViewAdapter(this, dataList));
        viewPager.setAdapter(fragAdapter);
        seekBar.setOnSeekBarChangeListener(this);
        viewPager.addOnPageChangeListener(this);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int endX;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        endX = (int) event.getX();
                        if ((startX == endX && startX > 240) || (startX == endX && commentsLayout.getVisibility() != View.VISIBLE)) {
                            if (bottom_layout.getVisibility() == View.VISIBLE) {
                                playAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bottom_layout_disappear_anim);
                                playAnimation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                        if (bottom_layout.getVisibility() == View.VISIBLE) {
                                            top_bar_layout.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.top_layout_disappear_anim));
                                            top_bar_layout.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        if (bottom_layout.getVisibility() == View.VISIBLE) {
                                            bottom_layout.setVisibility(View.INVISIBLE);
                                            describeAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bottom_layout_appear_anim);
                                            bottom_describe_layout.startAnimation(describeAnimation);
                                            bottom_describe_layout.setVisibility(View.VISIBLE);
                                        } else {
                                            bottom_describe_layout.setVisibility(View.GONE);
                                            playAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bottom_layout_appear_anim);
                                            bottom_layout.startAnimation(playAnimation);
                                            bottom_layout.setVisibility(View.VISIBLE);
                                            top_bar_layout.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.top_layout_appear_anim));
                                            top_bar_layout.setVisibility(View.VISIBLE);
                                        }
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                bottom_layout.startAnimation(playAnimation);

                            } else {
                                describeAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bottom_layout_disappear_anim);
                                describeAnimation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                        if (bottom_layout.getVisibility() == View.VISIBLE) {
                                            top_bar_layout.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.top_layout_disappear_anim));
                                            top_bar_layout.setVisibility(View.GONE);
                                        }
                                        if(commentsLayout.getVisibility()==View.VISIBLE){
                                            commentsLayout.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.left_layout_disappear_anim));
                                            commentsLayout.setVisibility(View.INVISIBLE);
                                        }
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        if (bottom_layout.getVisibility() == View.VISIBLE) {
                                            bottom_layout.setVisibility(View.INVISIBLE);
                                            describeAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bottom_layout_appear_anim);
                                            bottom_describe_layout.startAnimation(describeAnimation);
                                            bottom_describe_layout.setVisibility(View.VISIBLE);
                                        } else {
                                            bottom_describe_layout.setVisibility(View.GONE);
                                            playAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bottom_layout_appear_anim);
                                            bottom_layout.startAnimation(playAnimation);
                                            bottom_layout.setVisibility(View.VISIBLE);
                                            top_bar_layout.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.top_layout_appear_anim));
                                            top_bar_layout.setVisibility(View.VISIBLE);
                                        }
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                bottom_describe_layout.startAnimation(describeAnimation);

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
        currentPageTv.setText(position + 1 + "");
        seekBar.setProgress((int) (((float) position / totalNum) * 100));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        seekPosition = (int) ((progress / 100.0) * 2);
        currentPageTv.setText(seekPosition + 1 + "");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        viewPager.setCurrentItem(seekPosition);
    }

    @Override
    public void onClick(View v) {
        if (v == comments_icon_iv) {
            Toast.makeText(this, "跳转评论区", Toast.LENGTH_SHORT).show();
        } else if (v == shoucang_icon_iv) {
            Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
        } else if (v == share_icon_iv) {
            Toast.makeText(this, "开始分享", Toast.LENGTH_SHORT).show();
        } else if (v == tanmu_swicher_iv) {
            isCommentsOn = !isCommentsOn;
            GifFragment fragment = (GifFragment) fragAdapter.getItem(viewPager.getCurrentItem());
            //fragment.removeBarrageView();
            Toast.makeText(this, "弹幕关闭", Toast.LENGTH_SHORT).show();
        } else if (v == back_iv) {
            Toast.makeText(this, "退出此页", Toast.LENGTH_SHORT).show();
        } else if (v == comments_switcher_tv) {
            Animation animation;
            if (commentsLayout.getVisibility() != View.VISIBLE) {
                animation = AnimationUtils.loadAnimation(this, R.anim.left_layout_appear_anim);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        commentsLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                commentsLayout.startAnimation(animation);
            }
        }
    }

    public static class GifFragment extends Fragment {
        int resId;
        GifImageView gifImageView;
        RelativeLayout rootlayout;
        Handler handler;
        Runnable createBarrageView;
        Context context;

        public GifFragment() {
            super();
        }

        public static final GifFragment newInstance(int resId) {
            GifFragment gifFragment = new GifFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("resId", resId);
            gifFragment.setArguments(bundle);
            return gifFragment;
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            this.context = context;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.gif_imageview_fragment_layout, container, false);
            gifImageView = (GifImageView) view.findViewById(R.id.gifview);
            gifImageView.setBackgroundResource(getArguments().getInt("resId"));
            gifImageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                }
            });
            rootlayout = (RelativeLayout) view.findViewById(R.id.rootlayout);
            //读取文字资源
            final String[] texts = getResources().getStringArray(R.array.default_text_array);

            //设置宽高全屏
            final ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            if (handler == null) {
                handler = new Handler();
            }
            createBarrageView = new Runnable() {
                @Override
                public void run() {
                    if (!isOnPause && isCommentsOn) {
                        Log.e("azzz", "发送弹幕");
                        //新建一条弹幕，并设置文字
                        final BarrageView barrageView = new BarrageView(context);
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

        public void removeBarrageView() {
            if (handler != null) {
                handler.removeCallbacks(createBarrageView);
            }
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();

        }
    }

    public class FragAdapter extends FragmentPagerAdapter {

        private List<GifFragment> mFragments;

        public FragAdapter(FragmentManager fm, List<GifFragment> fragments) {
            super(fm);
            // TODO Auto-generated constructor stub
            mFragments = fragments;
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
