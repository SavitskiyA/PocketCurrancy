package converter.com.converter.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private AsyncTask<String, String, String> nbuTask;
    private ImageView img_EUR_sale, img_USD_sale, img_RUR_sale;
    private Drawable triangleGreenSelector, triangleRedSelector, equallySelector;
    private double diff_EUR, diff_USD, diff_RUR;



    public FragmentCurExchangeNBU() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbh = new DataBaseHelper(this.getContext());
        fm = getFragmentManager();
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

        triangleGreenSelector = getContext().getResources().getDrawable(R.drawable.trianglegreenselector);
        triangleRedSelector = getContext().getResources().getDrawable(R.drawable.triangleredselector);
        equallySelector = getContext().getResources().getDrawable(R.drawable.equallyselector);

        calculateDiff();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_fragment_cur_exchange_nbu, container, false);
        txt_EUR_UAH = (TextView) v.findViewById(R.id.txt_EUR_UAH_sale);
        txt_USD_UAH = (TextView) v.findViewById(R.id.txt_USD_UAH_sale);
        txt_RUB_UAH = (TextView) v.findViewById(R.id.txt_RUB_UAH_sale);
        img_EUR_sale = (ImageView) v.findViewById(R.id.img_EUR_sale);
        img_USD_sale = (ImageView) v.findViewById(R.id.img_USD_sale);
        img_RUR_sale = (ImageView) v.findViewById(R.id.img_RUB_sale);

        setStatIconintoView(img_EUR_sale,diff_EUR);
        setStatIconintoView(img_USD_sale,diff_USD);
        setStatIconintoView(img_RUR_sale,diff_RUR);

        return v;
    }

    private void calculateDiff() {
        diff_EUR = getDiff(DataBaseHelper.Strings.EUR);
        diff_USD = getDiff(DataBaseHelper.Strings.USD);
        diff_RUR = getDiff(DataBaseHelper.Strings.EUR);
    }

    private double getDiff(String cur) {
        String curValue = null;
        String prevValue = null;
        Cursor cursorCur = dbh.getValueFromNBU(DataBaseHelper.getCurrentDate(), cur);
        if (cursorCur.getCount() != 0) {
            while (cursorCur.moveToNext()) {
                curValue = cursorCur.getString(0);
            }
        }
        boolean go = true;
        int i = 1;
        while (go) {
            Cursor cursorPrev = dbh.getValueFromNBU(DataBaseHelper.getPrevDate(i), cur);
            if (cursorPrev.getCount() != 0) {
                while (cursorPrev.moveToNext()) {
                    prevValue = cursorPrev.getString(0);
                    go = false;
                }
            } else {
                i++;
            }
        }

        return Double.parseDouble(curValue) - Double.parseDouble(prevValue);

    }

    public void setStatIconintoView(View v, double diff) {
        if (diff > 0) {
            v.setBackgroundDrawable(triangleGreenSelector);
        } else if (diff < 0) {
            v.setBackgroundDrawable(triangleRedSelector);
        } else {
            v.setBackgroundDrawable(equallySelector);
        }
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
        nbuTask=new NBUTask(this.getContext(), dbh, v, fm).execute(DataBaseHelper.Strings.NBU_CUR_RATE);
    }

    public void createBroadcastReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (cm.getActiveNetworkInfo() != null) {
                    Cursor cursor = dbh.getlastCursFromNBU(DataBaseHelper.getCurrentDate());
                    int cursorCount=cursor.getCount();
                    if (cursorCount == 0) {
                        if (nbuTask != null) {
                            if (nbuTask.getStatus() != PBTask.Status.RUNNING) {
                                startTask();
                            }
                        }
                    }
                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        fillViews();
        createBroadcastReceiver();
        getActivity().registerReceiver(receiver,intentFilter);
        img_EUR_sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCurranciesChart chartDialog = new FragmentCurranciesChart();
                Bundle bundle = new Bundle();
                bundle.putString("BANK", DataBaseHelper.Strings.NBU);
                bundle.putString("MAIN_CURRANCY", DataBaseHelper.Strings.EUR);
                chartDialog.setArguments(bundle);
                chartDialog.show(getFragmentManager(), "tag");
            }
        });
        img_USD_sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCurranciesChart chartDialog = new FragmentCurranciesChart();
                Bundle bundle = new Bundle();
                bundle.putString("BANK", DataBaseHelper.Strings.NBU);
                bundle.putString("MAIN_CURRANCY", DataBaseHelper.Strings.USD);
                chartDialog.setArguments(bundle);
                chartDialog.show(getFragmentManager(), "tag");
            }
        });
        img_RUR_sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCurranciesChart chartDialog = new FragmentCurranciesChart();
                Bundle bundle = new Bundle();
                bundle.putString("BANK", DataBaseHelper.Strings.NBU);
                bundle.putString("MAIN_CURRANCY", DataBaseHelper.Strings.RUR);
                chartDialog.setArguments(bundle);
                chartDialog.show(getFragmentManager(), "tag");
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }
}
