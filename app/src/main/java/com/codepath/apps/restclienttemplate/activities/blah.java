//package com.codepath.apps.restclienttemplate;
//
//import android.os.Bundle;
//import android.support.v4.view.ViewPager;
//import android.support.v7.app.AppCompatActivity;
//
//import com.astuetz.PagerSlidingTabStrip;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//
//public class MainActivity extends AppCompatActivity {
//
//    @BindView(R.id.viewpager) ViewPager viewPager;
//    @BindView(R.id.tabs) PagerSlidingTabStrip tabsStrip;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.blah);
//        ButterKnife.bind(this);
//
//        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager()));
//        tabsStrip.setViewPager(viewPager);
//    }
//
//}
