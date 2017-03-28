package converter.com.converter.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;


import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;


import converter.com.converter.R;
import converter.com.converter.support.SectionsPageAdapter;

public class MainActivity extends FragmentActivity  {

    private SectionsPageAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                    setContentView(R.layout.activity_main);
            mSectionsPagerAdapter = new SectionsPageAdapter(getSupportFragmentManager());
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);


    }


  }