package converter.com.converter.support;

import android.app.Activity;
import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

import converter.com.converter.R;
import converter.com.converter.activities.FragmentCurExchangePB;

/**
 * Created by Andrey on 24.02.2017.
 */

public class CustomDialog extends DialogFragment {
    LayoutInflater inflater;
    View v;
    Button b_wifi, b_settings;
    TextView txt_mes;
    String message;
    Object object;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;
    private ImageView img_wifi, img_mob_net, img_i;

    public CustomDialog(String message) {
        this.message = message;

    }

    public CustomDialog(String message, Object object) {
        this.message = message;
        this.object = object;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.custom_dialog_layout, null);
        img_wifi = (ImageView) v.findViewById(R.id.img_wifi);
        img_mob_net = (ImageView) v.findViewById(R.id.img_mob_net);
        img_i= (ImageView) v.findViewById(R.id.img_i);
        txt_mes = (TextView) v.findViewById(R.id.txt_mes);
        builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setCancelable(true);


        img_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableWifiConnection();
                alertDialog.dismiss();
                Toast.makeText(getContext(), "Wait please...", Toast.LENGTH_SHORT).show();
            }
        });


        img_mob_net.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettings();
            }
        });
        img_i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        txt_mes.setText(this.message);
        alertDialog = builder.create();
        return alertDialog;

    }



    private void enableWifiConnection() {
        WifiManager wifi = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(true);
    }

    public void showSettings() {
        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
    }

    public boolean isInternetWorking() {
        boolean success = false;
        try {
            URL url = new URL("https://google.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.connect();
            success = connection.getResponseCode() == 200;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }
}
