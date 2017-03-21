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

import java.util.Date;

import converter.com.converter.R;
import converter.com.converter.support.CustomDialog;
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
    private ImageView img_EUR_buy, img_EUR_sale, img_USD_buy, img_USD_sale, img_RUR_buy, img_RUR_sale;
    private AsyncTask<String, String, String> pbTask;
    private Drawable triangleGreenSelector, triangleRedSelector, equallySelector;
    private double diff_EUR_BUY_BRANCH, diff_EUR_SALE_BRANCH, diff_USD_BUY_BRANCH, diff_USD_SALE_BRANCH, diff_RUR_BUY_BRANCH, diff_RUR_SALE_BRANCH, diff_EUR_BUY_CARDS, diff_EUR_SALE_CARDS, diff_USD_BUY_CARDS, diff_USD_SALE_CARDS, diff_RUR_BUY_CARDS, diff_RUR_SALE_CARDS;

    public FragmentCurExchangePB() {
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
        calculateDiffBranch();
        calculateDiffCards();
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
        img_EUR_buy = (ImageView) v.findViewById(R.id.img_EUR_buy);
        img_EUR_sale = (ImageView) v.findViewById(R.id.img_EUR_sale);
        img_USD_buy = (ImageView) v.findViewById(R.id.img_USD_buy);
        img_USD_sale = (ImageView) v.findViewById(R.id.img_USD_sale);
        img_RUR_buy = (ImageView) v.findViewById(R.id.img_RUB_buy);
        img_RUR_sale = (ImageView) v.findViewById(R.id.img_RUB_sale);

        setStatIconintoView(img_EUR_buy,diff_EUR_BUY_BRANCH);
        setStatIconintoView(img_EUR_sale,diff_EUR_SALE_BRANCH);
        setStatIconintoView(img_USD_buy,diff_USD_BUY_BRANCH);
        setStatIconintoView(img_USD_sale,diff_USD_SALE_BRANCH);
        setStatIconintoView(img_RUR_buy,diff_RUR_BUY_BRANCH);
        setStatIconintoView(img_RUR_sale,diff_RUR_SALE_BRANCH);

        return v;
    }



    @Override
    public void onResume() {
        super.onResume();
        rb_branch.setChecked(true);
        fillViews(DataBaseHelper.Strings.BRANCH);
        createBroadcastReceiver();
        radioSwitcher();
        getActivity().registerReceiver(receiver, intentFilter);
        img_EUR_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCurranciesChart chartDialog = new FragmentCurranciesChart();
                Bundle bundle = new Bundle();
                bundle.putString("BANK", DataBaseHelper.Strings.PB);
                bundle.putString("MAIN_CURRANCY", DataBaseHelper.Strings.EUR);
                bundle.putString("WHAT", DataBaseHelper.Strings.BUY);
                if (rb_cards.isChecked()) bundle.putString("TYPE", DataBaseHelper.Strings.CARDS);
                if (rb_branch.isChecked()) bundle.putString("TYPE", DataBaseHelper.Strings.BRANCH);
                chartDialog.setArguments(bundle);
                chartDialog.show(getFragmentManager(), "tag");
            }
        });

        img_EUR_sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCurranciesChart chartDialog = new FragmentCurranciesChart();
                Bundle bundle = new Bundle();
                bundle.putString("BANK", DataBaseHelper.Strings.PB);
                bundle.putString("MAIN_CURRANCY", DataBaseHelper.Strings.EUR);
                bundle.putString("WHAT", DataBaseHelper.Strings.SALE);
                if (rb_cards.isChecked()) bundle.putString("TYPE", DataBaseHelper.Strings.CARDS);
                if (rb_branch.isChecked()) bundle.putString("TYPE", DataBaseHelper.Strings.BRANCH);
                chartDialog.setArguments(bundle);
                chartDialog.show(getFragmentManager(), "tag");
            }
        });

        img_USD_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCurranciesChart chartDialog = new FragmentCurranciesChart();
                Bundle bundle = new Bundle();
                bundle.putString("BANK", DataBaseHelper.Strings.PB);
                bundle.putString("MAIN_CURRANCY", DataBaseHelper.Strings.USD);
                bundle.putString("WHAT", DataBaseHelper.Strings.BUY);
                if (rb_cards.isChecked()) bundle.putString("TYPE", DataBaseHelper.Strings.CARDS);
                if (rb_branch.isChecked()) bundle.putString("TYPE", DataBaseHelper.Strings.BRANCH);
                chartDialog.setArguments(bundle);
                chartDialog.show(getFragmentManager(), "tag");
            }
        });

        img_USD_sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCurranciesChart chartDialog = new FragmentCurranciesChart();
                Bundle bundle = new Bundle();
                bundle.putString("BANK", DataBaseHelper.Strings.PB);
                bundle.putString("MAIN_CURRANCY", DataBaseHelper.Strings.USD);
                bundle.putString("WHAT", DataBaseHelper.Strings.SALE);
                if (rb_cards.isChecked()) bundle.putString("TYPE", DataBaseHelper.Strings.CARDS);
                if (rb_branch.isChecked()) bundle.putString("TYPE", DataBaseHelper.Strings.BRANCH);
                chartDialog.setArguments(bundle);
                chartDialog.show(getFragmentManager(), "tag");
            }
        });

        img_RUR_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCurranciesChart chartDialog = new FragmentCurranciesChart();
                Bundle bundle = new Bundle();
                bundle.putString("BANK", DataBaseHelper.Strings.PB);
                bundle.putString("MAIN_CURRANCY", DataBaseHelper.Strings.RUR);
                bundle.putString("WHAT", DataBaseHelper.Strings.BUY);
                if (rb_cards.isChecked()) bundle.putString("TYPE", DataBaseHelper.Strings.CARDS);
                if (rb_branch.isChecked()) bundle.putString("TYPE", DataBaseHelper.Strings.BRANCH);
                chartDialog.setArguments(bundle);
                chartDialog.show(getFragmentManager(), "tag");
            }
        });
        img_RUR_sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCurranciesChart chartDialog = new FragmentCurranciesChart();
                Bundle bundle = new Bundle();
                bundle.putString("BANK", DataBaseHelper.Strings.PB);
                bundle.putString("MAIN_CURRANCY", DataBaseHelper.Strings.RUR);
                bundle.putString("WHAT", DataBaseHelper.Strings.SALE);
                if (rb_cards.isChecked()) bundle.putString("TYPE", DataBaseHelper.Strings.CARDS);
                if (rb_branch.isChecked()) bundle.putString("TYPE", DataBaseHelper.Strings.BRANCH);
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

    public void radioSwitcher() {
        rb_branch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_cards.setChecked(false);
                fillViews(DataBaseHelper.Strings.BRANCH);
                setStatIconintoView(img_EUR_buy,diff_EUR_BUY_BRANCH);
                setStatIconintoView(img_EUR_sale,diff_EUR_SALE_BRANCH);
                setStatIconintoView(img_USD_buy,diff_USD_BUY_BRANCH);
                setStatIconintoView(img_USD_sale,diff_USD_SALE_BRANCH);
                setStatIconintoView(img_RUR_buy,diff_RUR_BUY_BRANCH);
                setStatIconintoView(img_RUR_sale,diff_RUR_SALE_BRANCH);

            }
        });

        rb_cards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_branch.setChecked(false);
                fillViews(DataBaseHelper.Strings.CARDS);
                setStatIconintoView(img_EUR_buy,diff_EUR_BUY_CARDS);
                setStatIconintoView(img_EUR_sale,diff_EUR_SALE_CARDS);
                setStatIconintoView(img_USD_buy,diff_USD_BUY_CARDS);
                setStatIconintoView(img_USD_sale,diff_USD_SALE_CARDS);
                setStatIconintoView(img_RUR_buy,diff_RUR_BUY_CARDS);
                setStatIconintoView(img_RUR_sale,diff_RUR_SALE_CARDS);

            }
        });
    }


    public void fillViews(String type) {
        Cursor cursor = dbh.getlastCursFromPB(DataBaseHelper.getCurrentDate(), type);
        if (cursor.getCount() == 0) {
//            if (pbTask.getStatus() != PBTask.Status.RUNNING) {
            startTask();
//            }
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
        if (rb_branch.isChecked()) {
            pbTask = new PBTask(this.getContext(), DataBaseHelper.Strings.BRANCH, dbh, v, fm).execute(DataBaseHelper.Strings.PB_BRANCH_CUR_RATE);
            return;
        }
        if (rb_cards.isChecked()) {
            pbTask = new PBTask(this.getContext(), DataBaseHelper.Strings.CARDS, dbh, v, fm).execute(DataBaseHelper.Strings.PB_CARDS_CUR_RATE);
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


    public double getDiff(String cur, String what, String type) {
        String curValue = null;
        String prevValue = null;
        Cursor cursorCur = dbh.getValueFromPB(DataBaseHelper.getCurrentDate(), cur, what, type);
        if (cursorCur.getCount() != 0) {
            while (cursorCur.moveToNext()) {
                curValue = cursorCur.getString(0);
            }
        }
        boolean go = true;
        int i = 1;
        while (go) {
            Cursor cursorPrev = dbh.getValueFromPB(DataBaseHelper.getPrevDate(i), cur, what, type);
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


    private void calculateDiffCards() {
        diff_EUR_BUY_CARDS = getDiff(DataBaseHelper.Strings.EUR, DataBaseHelper.Strings.BUY, DataBaseHelper.Strings.CARDS);
        diff_EUR_SALE_CARDS = getDiff(DataBaseHelper.Strings.EUR, DataBaseHelper.Strings.SALE, DataBaseHelper.Strings.CARDS);
        diff_USD_BUY_CARDS = getDiff(DataBaseHelper.Strings.USD, DataBaseHelper.Strings.BUY, DataBaseHelper.Strings.CARDS);
        diff_USD_SALE_CARDS = getDiff(DataBaseHelper.Strings.USD, DataBaseHelper.Strings.SALE, DataBaseHelper.Strings.CARDS);
        diff_RUR_BUY_CARDS = getDiff(DataBaseHelper.Strings.EUR, DataBaseHelper.Strings.BUY, DataBaseHelper.Strings.CARDS);
        diff_RUR_SALE_CARDS = getDiff(DataBaseHelper.Strings.EUR, DataBaseHelper.Strings.SALE, DataBaseHelper.Strings.CARDS);
    }

    private void calculateDiffBranch() {
        diff_EUR_BUY_BRANCH = getDiff(DataBaseHelper.Strings.EUR, DataBaseHelper.Strings.BUY, DataBaseHelper.Strings.BRANCH);
        diff_EUR_SALE_BRANCH = getDiff(DataBaseHelper.Strings.EUR, DataBaseHelper.Strings.SALE, DataBaseHelper.Strings.BRANCH);
        diff_USD_BUY_BRANCH = getDiff(DataBaseHelper.Strings.USD, DataBaseHelper.Strings.BUY, DataBaseHelper.Strings.BRANCH);
        diff_USD_SALE_BRANCH = getDiff(DataBaseHelper.Strings.USD, DataBaseHelper.Strings.SALE, DataBaseHelper.Strings.BRANCH);
        diff_RUR_BUY_BRANCH = getDiff(DataBaseHelper.Strings.EUR, DataBaseHelper.Strings.BUY, DataBaseHelper.Strings.BRANCH);
        diff_RUR_SALE_BRANCH = getDiff(DataBaseHelper.Strings.EUR, DataBaseHelper.Strings.SALE, DataBaseHelper.Strings.BRANCH);
    }
}
