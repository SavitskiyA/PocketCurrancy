package converter.com.converter.support;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import converter.com.converter.R;

/**
 * Created by Andrey on 20.02.2017.
 */

public class SpinnerModel {
    private int image;
    private String currancy;
    private String curAbr;

    public String getCurAbr() {
        return curAbr;
    }



    public SpinnerModel(String currancy, int image, String curAbr) {
        this.image = image;
        this.currancy = currancy;
        this.curAbr=curAbr;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getCurrancy() {
        return currancy;
    }

    public void setCurrancy(String currancy) {
        this.currancy = currancy;
    }

    public void setCurAbr(String curAbr) {
        this.curAbr = curAbr;
    }
}

