package converter.com.converter.support;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import converter.com.converter.activities.BaseFragment;

/**
 * Created by Andrey on 09.02.2017.
 */


public class PBTask extends AsyncTask<String, String, String> {
    private BaseFragment baseFragment;
    private Context context;
    private DataBaseHelper dataBaseHelper;
    private String source;

    public PBTask(BaseFragment baseFragment, Context context, DataBaseHelper dataBaseHelper, String source) {
        this.baseFragment = baseFragment;
        this.context = context;
        this.dataBaseHelper = dataBaseHelper;
        this.source = source;
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
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s != null) {
            try {
                baseFragment.setData(s, source);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
            Cursor cursor = dataBaseHelper.getlastCursFromPB(DataBaseHelper.getCurrentDate(), source);
            if (cursor.getCount() == 0) {
                Toast.makeText(context, "Error with exctracting data from database", Toast.LENGTH_SHORT).show();
            } else {
                baseFragment.fillView(cursor);
                baseFragment.setStatIconIntoView(source);
            }

        } else {
            baseFragment.showCustomDialog(DataBaseHelper.Strings.NO_DATA);
        }


    }

}




