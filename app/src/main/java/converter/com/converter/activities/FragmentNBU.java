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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import converter.com.converter.R;
import converter.com.converter.support.CustomDialog;
import converter.com.converter.support.DataBaseHelper;
import converter.com.converter.support.NBUTask;
import converter.com.converter.support.PBTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentNBU extends BaseFragment {

    private DataBaseHelper dbh;
    private TextView txt_EUR_UAH, txt_USD_UAH, txt_RUB_UAH;
    private FragmentManager fm;
    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;
    private AsyncTask<String, String, String> nbuTask;
    private ImageView img_EUR_sale, img_USD_sale, img_RUR_sale;
    private Drawable triangleGreenSelector, triangleRedSelector, equallySelector;


    public FragmentNBU() {
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
//        handInsert();

    }

    private void handInsert() {
        dbh.insertIntoNBU("20170322", "EUR", "26.4");
        dbh.insertIntoNBU("20170322", "USD", "25.0");
        dbh.insertIntoNBU("20170322", "RUR", "0.55");

        dbh.insertIntoNBU("20170321", "EUR", "28.0");
        dbh.insertIntoNBU("20170321", "USD", "27.0");
        dbh.insertIntoNBU("20170321", "RUR", "0.5");

        dbh.insertIntoNBU("20170320", "EUR", "27.1");
        dbh.insertIntoNBU("20170320", "USD", "26.06");
        dbh.insertIntoNBU("20170320", "RUR", "0.4");

        dbh.insertIntoNBU("20170319", "EUR", "28.87");
        dbh.insertIntoNBU("20170319", "USD", "27.56");
        dbh.insertIntoNBU("20170319", "RUR", "0.7");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fragment_cur_exchange_nbu, container, false);
        txt_EUR_UAH = (TextView) v.findViewById(R.id.txt_EUR_UAH_sale);
        txt_USD_UAH = (TextView) v.findViewById(R.id.txt_USD_UAH_sale);
        txt_RUB_UAH = (TextView) v.findViewById(R.id.txt_RUB_UAH_sale);
        img_EUR_sale = (ImageView) v.findViewById(R.id.img_EUR_sale);
        img_USD_sale = (ImageView) v.findViewById(R.id.img_USD_sale);
        img_RUR_sale = (ImageView) v.findViewById(R.id.img_RUB_sale);
        return v;
    }


    public void startTask() {
        nbuTask = new NBUTask(this, this.getContext(), dbh).execute(DataBaseHelper.Strings.NBU_CUR_RATE);
    }

    public void createBroadcastReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (cm.getActiveNetworkInfo() != null) {
                    Cursor cursor = dbh.getlastCursFromNBU(DataBaseHelper.getCurrentDate());
                    int cursorCount = cursor.getCount();
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
        getData();
        createBroadcastReceiver();
        getActivity().registerReceiver(receiver, intentFilter);
        img_EUR_sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCurranciesChart chartDialog = new FragmentCurranciesChart();
                chartDialog.setArguments(createBundle(DataBaseHelper.Strings.NBU, DataBaseHelper.Strings.EUR));
                chartDialog.show(getFragmentManager(), "tag");
            }
        });
        img_USD_sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCurranciesChart chartDialog = new FragmentCurranciesChart();
                chartDialog.setArguments(createBundle(DataBaseHelper.Strings.NBU, DataBaseHelper.Strings.USD));
                chartDialog.show(getFragmentManager(), "tag");
            }
        });
        img_RUR_sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCurranciesChart chartDialog = new FragmentCurranciesChart();
                chartDialog.setArguments(createBundle(DataBaseHelper.Strings.NBU, DataBaseHelper.Strings.RUR));
                chartDialog.show(getFragmentManager(), "tag");
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }


    public void getData() {
        Cursor cursor = dbh.getlastCursFromNBU(DataBaseHelper.getCurrentDate());
        if (cursor.getCount() == 0) {
            startTask();
        } else {
            fillView(cursor);
            setStatIconIntoView(null);
        }

    }

    @Override
    public void fillView(Cursor cursor) {
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

    @Override
    public synchronized void setData(String s, String source) throws JSONException {
        JSONArray parentArray = new JSONArray(s);
        for (int i = 0; i < parentArray.length(); i++) {
            JSONObject object = parentArray.getJSONObject(i);
            String cc = object.getString("cc");
            String rate = object.getString("rate");
            if (cc.equals("USD") || cc.equals("EUR"))
                dbh.insertIntoNBU(DataBaseHelper.getCurrentDate(), cc, rate);
            if (cc.equals("RUB"))
                dbh.insertIntoNBU(DataBaseHelper.getCurrentDate(), "RUR", rate);
        }
    }

    @Override
    public void showCustomDialog(String message) {
        CustomDialog customDialog = new CustomDialog(message);
        customDialog.show(fm, "tag");
    }

    @Override
    public void setStatIconIntoView(String source) {
        setStatIconIntoView(img_EUR_sale, getDiff(DataBaseHelper.Strings.EUR));
        setStatIconIntoView(img_USD_sale, getDiff(DataBaseHelper.Strings.USD));
        setStatIconIntoView(img_RUR_sale, getDiff(DataBaseHelper.Strings.RUR));
    }

    private void setStatIconIntoView(View v, double diff) {
        if (diff > 0) {
            v.setBackgroundDrawable(triangleGreenSelector);
        } else if (diff < 0) {
            v.setBackgroundDrawable(triangleRedSelector);
        } else {
            v.setBackgroundDrawable(equallySelector);
        }
    }

    private Bundle createBundle(String bank, String mainCurrancy) {
        Bundle bundle = new Bundle();
        bundle.putString("BANK", bank);
        bundle.putString("MAIN_CURRANCY", mainCurrancy);
        return bundle;
    }

    private int getRowcount() {
        Cursor cursor = dbh.getRowCount(DataBaseHelper.NBUtable.NAME, null);
        return cursor.getCount();
    }


    private double getDiff(String cur) {
        if (getRowcount() <= 1) return 0;

        String curValue = null;
        String prevValue = null;
        Cursor cursorCur = dbh.getValueFromNBU(DataBaseHelper.getCurrentDate(), cur);
        while (cursorCur.moveToNext()) {
            curValue = cursorCur.getString(0);
        }
        boolean go = true;
        int i = 1;
        while (go) {
            Cursor cursorPrev = dbh.getValueFromNBU(DataBaseHelper.getPrevDate(i), cur);
            while (cursorPrev.moveToNext()) {
                prevValue = cursorPrev.getString(0);
                go = false;
            }
            i++;

        }
        return Double.parseDouble(curValue) - Double.parseDouble(prevValue);
    }
}
