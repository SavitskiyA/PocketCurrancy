package converter.com.converter.activities;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.series.DataPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import converter.com.converter.R;
import converter.com.converter.support.CustomDialog;
import converter.com.converter.support.DataBaseHelper;
import converter.com.converter.support.PBTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPB extends BaseFragment {
    private static String curSource = DataBaseHelper.Strings.BRANCH;
    private DataBaseHelper dbh;
    private TextView txt_EUR_UAH_buy, txt_EUR_UAH_sale, txt_USD_UAH_buy, txt_USD_UAH_sale, txt_RUR_UAH_buy, txt_RUR_UAH_sale;
    private RadioButton rb_branch, rb_cards;
    private FragmentManager fm;
    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;
    private ImageView img_EUR_buy, img_EUR_sale, img_USD_buy, img_USD_sale, img_RUR_buy, img_RUR_sale;
    private AsyncTask<String, String, String> pbTask;
    private Drawable triangleGreenSelector, triangleRedSelector, equallySelector;


    public FragmentPB() {
    }

    private void handInsert() {
        dbh.insertIntoPB("20170322", "EUR", "29.4", "29.7", DataBaseHelper.Strings.BRANCH);
        dbh.insertIntoPB("20170322", "USD", "26.0", "26.45", DataBaseHelper.Strings.BRANCH);
        dbh.insertIntoPB("20170322", "RUR", "0.55", "0.63", DataBaseHelper.Strings.BRANCH);

        dbh.insertIntoPB("20170321", "EUR", "28.0", "28.03", DataBaseHelper.Strings.BRANCH);
        dbh.insertIntoPB("20170321", "USD", "27.0", "27.45", DataBaseHelper.Strings.BRANCH);
        dbh.insertIntoPB("20170321", "RUR", "0.5", "0.6", DataBaseHelper.Strings.BRANCH);

        dbh.insertIntoPB("20170320", "EUR", "27.1", "27.5", DataBaseHelper.Strings.BRANCH);
        dbh.insertIntoPB("20170320", "USD", "26.06", "26.55", DataBaseHelper.Strings.BRANCH);
        dbh.insertIntoPB("20170320", "RUR", "0.4", "0.65", DataBaseHelper.Strings.BRANCH);

        dbh.insertIntoPB("20170319", "EUR", "28.87", "29.08", DataBaseHelper.Strings.BRANCH);
        dbh.insertIntoPB("20170319", "USD", "27.56", "28.65", DataBaseHelper.Strings.BRANCH);
        dbh.insertIntoPB("20170319", "RUR", "0.7", "0.8", DataBaseHelper.Strings.BRANCH);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbh = new DataBaseHelper(this.getActivity());

        fm = getFragmentManager();
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        triangleGreenSelector = getContext().getResources().getDrawable(R.drawable.trianglegreenselector);
        triangleRedSelector = getContext().getResources().getDrawable(R.drawable.triangleredselector);
        equallySelector = getContext().getResources().getDrawable(R.drawable.equallyselector);

        //
       // handInsert();
        //


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fragment_cur_exchange_pb, container, false);
        txt_EUR_UAH_buy = (TextView) v.findViewById(R.id.txt_EUR_UAH_buy);
        txt_EUR_UAH_sale = (TextView) v.findViewById(R.id.txt_EUR_UAH_sale);
        txt_USD_UAH_buy = (TextView) v.findViewById(R.id.txt_USD_UAH_buy);
        txt_USD_UAH_sale = (TextView) v.findViewById(R.id.txt_USD_UAH_sale);
        txt_RUR_UAH_buy = (TextView) v.findViewById(R.id.txt_RUB_UAH_buy);
        txt_RUR_UAH_sale = (TextView) v.findViewById(R.id.txt_RUB_UAH_sale);
        rb_branch = (RadioButton) v.findViewById(R.id.rb_branch);
        rb_cards = (RadioButton) v.findViewById(R.id.rb_cards);
        img_EUR_buy = (ImageView) v.findViewById(R.id.img_EUR_buy);
        img_EUR_sale = (ImageView) v.findViewById(R.id.img_EUR_sale);
        img_USD_buy = (ImageView) v.findViewById(R.id.img_USD_buy);
        img_USD_sale = (ImageView) v.findViewById(R.id.img_USD_sale);
        img_RUR_buy = (ImageView) v.findViewById(R.id.img_RUB_buy);
        img_RUR_sale = (ImageView) v.findViewById(R.id.img_RUB_sale);


        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        rb_branch.setChecked(true);
        getData(DataBaseHelper.Strings.BRANCH);
        createBroadcastReceiver();
        radioSwitcher();
        getActivity().registerReceiver(receiver, intentFilter);
        img_EUR_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCurranciesChart chartDialog = new FragmentCurranciesChart();
                chartDialog.setArguments(createBundle(DataBaseHelper.Strings.PB, DataBaseHelper.Strings.EUR, DataBaseHelper.Strings.BUY, curSource));
                chartDialog.show(fm, "tag");
            }
        });

        img_EUR_sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCurranciesChart chartDialog = new FragmentCurranciesChart();
                chartDialog.setArguments(createBundle(DataBaseHelper.Strings.PB, DataBaseHelper.Strings.EUR, DataBaseHelper.Strings.SALE, curSource));
                chartDialog.show(fm, "tag");
            }
        });

        img_USD_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCurranciesChart chartDialog = new FragmentCurranciesChart();
                chartDialog.setArguments(createBundle(DataBaseHelper.Strings.PB, DataBaseHelper.Strings.USD, DataBaseHelper.Strings.BUY, curSource));
                chartDialog.show(fm, "tag");
            }
        });

        img_USD_sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCurranciesChart chartDialog = new FragmentCurranciesChart();
                chartDialog.setArguments(createBundle(DataBaseHelper.Strings.PB, DataBaseHelper.Strings.USD, DataBaseHelper.Strings.SALE, curSource));
                chartDialog.show(fm, "tag");
            }
        });

        img_RUR_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCurranciesChart chartDialog = new FragmentCurranciesChart();
                chartDialog.setArguments(createBundle(DataBaseHelper.Strings.PB, DataBaseHelper.Strings.RUR, DataBaseHelper.Strings.BUY, curSource));
                chartDialog.show(fm, "tag");
            }
        });
        img_RUR_sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCurranciesChart chartDialog = new FragmentCurranciesChart();
                chartDialog.setArguments(createBundle(DataBaseHelper.Strings.PB, DataBaseHelper.Strings.RUR, DataBaseHelper.Strings.SALE, curSource));
                chartDialog.show(fm, "tag");
            }
        });

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
                curSource = DataBaseHelper.Strings.BRANCH;
                rb_cards.setChecked(false);
                getData(curSource);

            }
        });

        rb_cards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curSource = DataBaseHelper.Strings.CARDS;
                rb_branch.setChecked(false);
                getData(curSource);


            }
        });
    }


    public void getData(String source) {
        Cursor cursor = dbh.getlastCursFromPB(DataBaseHelper.getCurrentDate(), source);
        if (cursor.getCount() == 0) {
            startTask();
        } else {
            fillView(cursor);
            setStatIconIntoView(source);
        }

    }


    public void fillView(Cursor cursor) {
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


    public void startTask() {
        if (rb_branch.isChecked()) {
            pbTask = new PBTask(this, this.getContext(), dbh, curSource).execute(DataBaseHelper.Strings.PB_BRANCH_CUR_RATE);
            return;
        }
        if (rb_cards.isChecked()) {
            pbTask = new PBTask(this, this.getContext(), dbh, curSource).execute(DataBaseHelper.Strings.PB_CARDS_CUR_RATE);
            return;
        }
    }

    public void createBroadcastReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (cm.getActiveNetworkInfo() != null) {
                    Cursor cursor = dbh.getlastCursFromPB(DataBaseHelper.getCurrentDate(), DataBaseHelper.Strings.BRANCH);
                    int cursorCount = cursor.getCount();
                    if (cursorCount == 0) {
                        if (pbTask != null) {
                            if (pbTask.getStatus() != PBTask.Status.RUNNING) {
                                startTask();
                            }
                        }
                    }
                }
            }
        };
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

    private double getDiff(String cur, String what, String type) {
        int rowCount = getRowcount();
        if (rowCount <= 1) return 0;

        String curValue = null;
        String prevValue = null;
        Cursor cursorCur = dbh.getValueFromPB(DataBaseHelper.getCurrentDate(), cur, what, type);
        while (cursorCur.moveToNext()) {
            curValue = cursorCur.getString(0);
        }
        boolean go = true;
        int i = 1;
        while (go) {
            Cursor cursorPrev = dbh.getValueFromPB(DataBaseHelper.getPrevDate(i), cur, what, type);
            while (cursorPrev.moveToNext()) {
                prevValue = cursorPrev.getString(0);
                go = false;
            }
            i++;
        }
        return Double.parseDouble(curValue) - Double.parseDouble(prevValue);
    }

    //check amount of rows in table
    private int getRowcount() {
        Cursor cursor = dbh.getRowCount(DataBaseHelper.PBTable.NAME, curSource);
        int cursorCount = cursor.getCount();
        return cursorCount;
    }


    public void setStatIconIntoView(String curSource) {
        if (curSource.equals(DataBaseHelper.Strings.BRANCH)) {
            setStatIconIntoView(img_EUR_buy, getDiff(DataBaseHelper.Strings.EUR, DataBaseHelper.Strings.BUY, DataBaseHelper.Strings.BRANCH));
            setStatIconIntoView(img_EUR_sale, getDiff(DataBaseHelper.Strings.EUR, DataBaseHelper.Strings.SALE, DataBaseHelper.Strings.BRANCH));
            setStatIconIntoView(img_USD_buy, getDiff(DataBaseHelper.Strings.USD, DataBaseHelper.Strings.BUY, DataBaseHelper.Strings.BRANCH));
            setStatIconIntoView(img_USD_sale, getDiff(DataBaseHelper.Strings.USD, DataBaseHelper.Strings.SALE, DataBaseHelper.Strings.BRANCH));
            setStatIconIntoView(img_RUR_buy, getDiff(DataBaseHelper.Strings.RUR, DataBaseHelper.Strings.BUY, DataBaseHelper.Strings.BRANCH));
            setStatIconIntoView(img_RUR_sale, getDiff(DataBaseHelper.Strings.RUR, DataBaseHelper.Strings.SALE, DataBaseHelper.Strings.BRANCH));
        } else if (curSource.equals(DataBaseHelper.Strings.CARDS)) {
            setStatIconIntoView(img_EUR_buy, getDiff(DataBaseHelper.Strings.EUR, DataBaseHelper.Strings.BUY, DataBaseHelper.Strings.CARDS));
            setStatIconIntoView(img_EUR_sale, getDiff(DataBaseHelper.Strings.EUR, DataBaseHelper.Strings.SALE, DataBaseHelper.Strings.CARDS));
            setStatIconIntoView(img_USD_buy, getDiff(DataBaseHelper.Strings.USD, DataBaseHelper.Strings.BUY, DataBaseHelper.Strings.CARDS));
            setStatIconIntoView(img_USD_sale, getDiff(DataBaseHelper.Strings.USD, DataBaseHelper.Strings.SALE, DataBaseHelper.Strings.CARDS));
            setStatIconIntoView(img_RUR_buy, getDiff(DataBaseHelper.Strings.RUR, DataBaseHelper.Strings.BUY, DataBaseHelper.Strings.CARDS));
            setStatIconIntoView(img_RUR_sale, getDiff(DataBaseHelper.Strings.RUR, DataBaseHelper.Strings.SALE, DataBaseHelper.Strings.CARDS));

        }
    }


    @Override
    public synchronized void setData(String s, String source) throws JSONException {
        JSONArray parentArray = new JSONArray(s);
        for (int i = 0; i < parentArray.length(); i++) {
            JSONObject object = parentArray.getJSONObject(i);
            String cur = object.getString("ccy");
            String buy = object.getString("buy");
            String sale = object.getString("sale");
            dbh.insertIntoPB(DataBaseHelper.getCurrentDate(), cur, buy, sale, source);
        }
    }

    @Override
    public void showCustomDialog(String message) {
        CustomDialog customDialog = new CustomDialog(message);
        customDialog.show(fm, "tag");
    }


    private Bundle createBundle(String bank, String mainCurrancy, String type, String source) {
        Bundle bundle = new Bundle();
        bundle.putString("BANK", bank);
        bundle.putString("MAIN_CURRANCY", mainCurrancy);
        bundle.putString("TYPE", type);
        bundle.putString("SOURCE", source);
        return bundle;
    }


}
