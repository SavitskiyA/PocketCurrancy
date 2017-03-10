package converter.com.converter.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import converter.com.converter.R;
import converter.com.converter.support.DataBaseHelper;
import converter.com.converter.support.NBUTask;
import converter.com.converter.support.PBTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCurExchangeNBU extends Fragment {

    private DataBaseHelper dbh;
    private TextView txt_EUR_UAH, txt_USD_UAH, txt_RUB_UAH;
    private View v;
    private FragmentManager fm;
    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;

    public FragmentCurExchangeNBU() {
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
        v = inflater.inflate(R.layout.fragment_fragment_cur_exchange_nbu, container, false);
        txt_EUR_UAH = (TextView) v.findViewById(R.id.txt_EUR_UAH_sale);
        txt_USD_UAH = (TextView) v.findViewById(R.id.txt_USD_UAH_sale);
        txt_RUB_UAH = (TextView) v.findViewById(R.id.txt_RUB_UAH_sale);
        return v;
    }


    public void fillViews() {
        Cursor cursor = dbh.getlastCursFromNBU(DataBaseHelper.getCurrentDate());
        if (cursor.getCount() == 0) {
            startTask();
        } else {
            StringBuffer buffer = new StringBuffer();
            while (cursor.moveToNext()) {
                String cur = cursor.getString(2);
                switch (cur) {
                    case "1":
                        txt_EUR_UAH.setText(cursor.getString(3));
                        break;
                    case "2":
                        txt_USD_UAH.setText(cursor.getString(3));
                        break;
                    case "3":
                        txt_RUB_UAH.setText(cursor.getString(3));
                        break;

                }
            }
        }
    }

    public void startTask() {
        new NBUTask(this.getContext(), dbh, v, fm).execute(DataBaseHelper.Strings.NBU_CUR_RATE);
    }

    public void createBroadcastReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (cm.getActiveNetworkInfo() != null) {
                    Cursor cursor = dbh.getlastCursFromNBU(DataBaseHelper.getCurrentDate());
                    if (cursor.getCount() == 0) startTask();
                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        fillViews();
        getActivity().registerReceiver(receiver,intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }
}
