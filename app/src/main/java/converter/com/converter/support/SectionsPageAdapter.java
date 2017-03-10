package converter.com.converter.support;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import converter.com.converter.activities.FragmentConverter;
import converter.com.converter.activities.FragmentCurExchangeContainer;

/**
 * Created by Andrey on 08.02.2017.
 */

public class SectionsPageAdapter extends FragmentPagerAdapter {

    public SectionsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new FragmentCurExchangeContainer();
            case 1: return new FragmentConverter();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }



    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Обмен валют";
            case 1:
                return "Конвертер";
        }
        return null;
    }

}
