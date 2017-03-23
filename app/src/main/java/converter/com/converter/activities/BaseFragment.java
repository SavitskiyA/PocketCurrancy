package converter.com.converter.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Andrey on 22.03.2017.
 */

public abstract class BaseFragment extends Fragment{
    public abstract void fillView(Cursor cursor);
    public abstract void setData(String s, String source) throws JSONException;
    public abstract void showCustomDialog(String message);
    public abstract void setStatIconIntoView(String source);
    public String getResponse(String URL) throws IOException {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            java.net.URL url = new URL(URL);
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
        }finally {
            if (connection != null) connection.disconnect();
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

}
