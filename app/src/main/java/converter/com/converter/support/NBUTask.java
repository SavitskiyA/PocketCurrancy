package converter.com.converter.support;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;
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
import converter.com.converter.activities.BaseFragment;

/**
 * Created by Andrey on 21.02.2017.
 */

public class NBUTask extends AsyncTask<String, String, String> {

    private BaseFragment baseFragment;
    private Context context;
    private DataBaseHelper dataBaseHelper;



    public NBUTask(BaseFragment baseFragment, Context context, DataBaseHelper dataBaseHelper) {
        this.baseFragment = baseFragment;
        this.context = context;
        this.dataBaseHelper = dataBaseHelper;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(context, "Getting current rates.", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected String doInBackground(String... params) {
        try {
            return baseFragment.getResponse(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
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
                baseFragment.setData(s, null);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
            Cursor cursor = dataBaseHelper.getlastCursFromNBU(DataBaseHelper.getCurrentDate());
            if (cursor.getCount() == 0) {
                Toast.makeText(context, "Error with exctracting data from database", Toast.LENGTH_SHORT).show();
            } else {
                baseFragment.fillView(cursor);
                baseFragment.setStatIconIntoView(null);
            }

        } else {
            baseFragment.showCustomDialog(DataBaseHelper.Strings.NO_DATA);
        }

    }


}
