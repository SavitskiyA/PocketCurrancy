package converter.com.converter.support;

import android.app.Activity;
import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.MainThread;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import converter.com.converter.R;
import converter.com.converter.activities.FragmentCurExchangeNBU;
import converter.com.converter.activities.MainActivity;

/**
 * Created by Andrey on 09.02.2017.
 */


public class PBTask extends AsyncTask<String, String, String> {

    private final DataBaseHelper mdb;
    private Context context;
    private String type;
    private TextView txt_EUR_UAH_buy, txt_EUR_UAH_sale, txt_USD_UAH_buy, txt_USD_UAH_sale, txt_RUR_UAH_buy, txt_RUR_UAH_sale;
    private CustomDialog customDialog;
    private FragmentManager fragmentManager;

    public PBTask(Context context, String type, DataBaseHelper mdb, View v, FragmentManager fragmentManager) {
        this.context = context;
        this.type = type;
        this.mdb = mdb;
        this.fragmentManager = fragmentManager;
        txt_EUR_UAH_buy = (TextView) v.findViewById(R.id.txt_EUR_UAH_buy);
        txt_EUR_UAH_sale = (TextView) v.findViewById(R.id.txt_EUR_UAH_sale);
        txt_USD_UAH_buy = (TextView) v.findViewById(R.id.txt_USD_UAH_buy);
        txt_USD_UAH_sale = (TextView) v.findViewById(R.id.txt_USD_UAH_sale);
        txt_RUR_UAH_buy = (TextView) v.findViewById(R.id.txt_RUB_UAH_buy);
        txt_RUR_UAH_sale = (TextView) v.findViewById(R.id.txt_RUB_UAH_sale);

    }




    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(context, "Getting current rates...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream inputstream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputstream));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            return buffer.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) connection.disconnect();
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }


    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s != null) {
            try {
                JSONArray parentArray = new JSONArray(s);
                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject object = parentArray.getJSONObject(i);
                    String cur = object.getString("ccy");
                    String buy = object.getString("buy");
                    String sale = object.getString("sale");
                    mdb.insertIntoPB(DataBaseHelper.getCurrentDate(), cur, buy, sale, type);
                }
                Cursor cursor = mdb.getlastCursFromPB(DataBaseHelper.getCurrentDate(), type);
                if (cursor.getCount() == 0) {
                    Toast.makeText(context, "Error with exctracting data from database", Toast.LENGTH_SHORT).show();
                } else {
                    StringBuffer buffer = new StringBuffer();
                    while (cursor.moveToNext()) {
                        String cur_id = cursor.getString(2);
                        switch (cur_id) {
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
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }

        } else {
            customDialog = new CustomDialog(DataBaseHelper.Strings.NO_DATA);
            customDialog.show(fragmentManager, "tag");
        }


    }
}




