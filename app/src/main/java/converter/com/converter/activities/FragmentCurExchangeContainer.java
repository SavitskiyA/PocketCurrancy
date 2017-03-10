package converter.com.converter.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import converter.com.converter.R;


public class FragmentCurExchangeContainer extends Fragment {
    RadioButton rb_pb, rb_nbu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_container_cur_exchange, container, false);
        rb_pb = (RadioButton) v.findViewById(R.id.rb_PB);
        rb_nbu = (RadioButton) v.findViewById(R.id.rb_NBU);

        radioSwitcher();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        rb_nbu.setChecked(false);
        FragmentCurExchangePB f1 = new FragmentCurExchangePB();
        ft.replace(R.id.fragment_container, f1);
        ft.commit();

        return v;
    }

    public void radioSwitcher() {
        rb_pb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                rb_nbu.setChecked(false);
                FragmentCurExchangePB f1 = new FragmentCurExchangePB();
                ft.replace(R.id.fragment_container, f1);
                ft.commit();
            }
        });

        rb_nbu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                rb_pb.setChecked(false);
                FragmentCurExchangeNBU f2 = new FragmentCurExchangeNBU();
                ft.replace(R.id.fragment_container, f2);
                ft.commit();
            }
        });
    }

}






