package converter.com.converter.support;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import converter.com.converter.R;


/**
 * Created by Andrey on 22.02.2017.
 */

public class SpinnerAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<String> cur_array;
    private TypedArray img_array;
    private ImageView img_cur;
    private TextView txt_cur;
    private ArrayList<SpinnerModel> spinnarArray;


    public SpinnerAdapter(Activity activity, ArrayList<SpinnerModel> spinnarArray) {
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.spinnarArray=spinnarArray;
        img_array = activity.getResources().obtainTypedArray(R.array.img_array);
        cur_array =  Arrays.asList(activity.getResources().getStringArray(R.array.cur_array));
    }

    @Override
    public int getCount() {
        return cur_array.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

public String getCurFromView(int position){
    return cur_array.get(position);
}


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = inflater.inflate(R.layout.spinner_model, null);
        img_cur = (ImageView) v.findViewById(R.id.img_cur);
        txt_cur = (TextView) v.findViewById(R.id.txt_cur);

        txt_cur.setText(spinnarArray.get(position).getCurrancy());
        img_cur.setBackgroundResource(spinnarArray.get(position).getImage());
//        txt_cur.setText(cur_array.get(position));
//        img_cur.setBackgroundResource(img_array.getResourceId(position, 0));
        return v;
    }
}
