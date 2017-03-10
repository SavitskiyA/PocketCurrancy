package converter.com.converter.activities;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v4.app.FragmentManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import converter.com.converter.R;
import converter.com.converter.support.DataBaseHelper;
import converter.com.converter.support.PBTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCurExchangePB extends Fragment {
    private DataBaseHelper dbh;
    private TextView txt_EUR_UAH_buy, txt_EUR_UAH_sale, txt_USD_UAH_buy, txt_USD_UAH_sale, txt_RUR_UAH_buy, txt_RUR_UAH_sale;
    private RadioButton rb_branch, rb_cards;
    private View v;
    private FragmentManager fm;
    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;

    public FragmentCurExchangePB() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbh = new DataBaseHelper(this.getContext());
        fm = getFragmentManager();
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        createBroadcastReceiver();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_fragment_cur_exchange_pb, container, false);
        txt_EUR_UAH_buy = (TextView) v.findViewById(R.id.txt_EUR_UAH_buy);
        txt_EUR_UAH_sale = (TextView) v.findViewById(R.id.txt_EUR_UAH_sale);
        txt_USD_UAH_buy = (TextView) v.findViewById(R.id.txt_USD_UAH_buy);
        txt_USD_UAH_sale = (TextView) v.findViewById(R.id.txt_USD_UAH_sale);
        txt_RUR_UAH_buy = (TextView) v.findViewById(R.id.txt_RUB_UAH_buy);
        txt_RUR_UAH_sale = (TextView) v.findViewById(R.id.txt_RUB_UAH_sale);
        rb_branch = (RadioButton) v.findViewById(R.id.rb_branch);
        rb_cards = (RadioButton) v.findViewById(R.id.rb_cards);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        rb_branch.setChecked(true);
        fillViews(DataBaseHelper.Strings.BRANCH);
        radioSwitcher();
        getActivity().registerReceiver(receiver,intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

    public void radioSwitcher() {
        rb_branch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fillViews(DataBaseHelper.Strings.BRANCH);
                rb_cards.setChecked(false);
            }
        });

        rb_cards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fillViews(DataBaseHelper.Strings.CARDS);
                rb_branch.setChecked(false);
            }
        });
    }


    public void fillViews(String type) {
        Cursor cursor = dbh.getlastCursFromPB(DataBaseHelper.getCurrentDate(), type);
        if (cursor.getCount() == 0) {
            startTask();
        } else {
            StringBuffer buffer = new StringBuffer();
            while (cursor.moveToNext()) {
                String cur = cursor.getString(2);
                switch (cur) {
                    case "1":
                        txt_EUR_UAH_buy.setText(cursor.getString(3));
                        txt_EUR_UAH_sale.setText(cursor.getString(4));
                        break;
                    case "2":
                        txt_USD_UAH_buy.setText(cursor.getString(3));
                        txt_USD_UAH_sale.setText(cursor.getString(4));
                        break;
                    case "3":
                        txt_RUR_UAH_buy.setText(cursor.getString(3));
                        txt_RUR_UAH_sale.setText(cursor.getString(4));
                        break;
                }
            }
        }
    }

    public void startTask() {
        if(rb_branch.isChecked())new PBTask(this.getContext(), DataBaseHelper.Strings.BRANCH, dbh, v, fm).execute(DataBaseHelper.Strings.PB_BRANCH_CUR_RATE);
        if(rb_cards.isChecked())new PBTask(this.getContext(), DataBaseHelper.Strings.CARDS, dbh, v, fm).execute(DataBaseHelper.Strings.PB_CARDS_CUR_RATE);
    }

    public void createBroadcastReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (cm.getActiveNetworkInfo() != null) {
                    Cursor cursor = dbh.getlastCursFromPB(DataBaseHelper.getCurrentDate(), DataBaseHelper.Strings.BUY);
                    if (cursor.getCount() == 0) startTask();
                }
            }
        };
    }
}
