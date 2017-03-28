package converter.com.converter.activities;

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import converter.com.converter.R;
import converter.com.converter.support.DataBaseHelper;

/**
 * Created by Andrey on 13.03.2017.
 */

public class FragmentCurranciesChart extends DialogFragment {
    LayoutInflater inflater;
    View v;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ArrayList<FragmentCurranciesChart.DBArray> dbArraysEUR = new ArrayList<>();
        ArrayList<FragmentCurranciesChart.DBArray> dbArraysUSD = new ArrayList<>();
        ArrayList<FragmentCurranciesChart.DBArray> dbArraysRUR = new ArrayList<>();

        inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.fragment_currancies_chart, null);

        GraphView graphView = (GraphView) v.findViewById(R.id.cur_chart);
        DataBaseHelper db = new DataBaseHelper(getContext());

        Bundle bundle = this.getArguments();
        String bank = bundle.getString("BANK");
        String mainCurrancy = bundle.getString("MAIN_CURRANCY");

        Cursor cursorEUR = null;
        Cursor cursorUSD = null;
        Cursor cursorRUR = null;

        if (bank.equals(DataBaseHelper.Strings.PB)) {
            String what = bundle.getString("TYPE");
            String type = bundle.getString("SOURCE");
            cursorEUR = db.getDataFromPB(DataBaseHelper.Strings.EUR, what, type, "5");
            cursorUSD = db.getDataFromPB(DataBaseHelper.Strings.USD, what, type, "5");
            cursorRUR = db.getDataFromPB(DataBaseHelper.Strings.RUR, what, type, "5");

        }
        if (bank.equals(DataBaseHelper.Strings.NBU)) {
            cursorEUR = db.getDataFromNBU(DataBaseHelper.Strings.EUR, "5");
            cursorUSD = db.getDataFromNBU(DataBaseHelper.Strings.USD, "5");
            cursorRUR = db.getDataFromNBU(DataBaseHelper.Strings.RUR, "5");
        }
        int cursorCount = cursorEUR.getCount();

        if (cursorCount != 0) {
            while (cursorEUR.moveToNext()) {
                dbArraysEUR.add(new FragmentCurranciesChart.DBArray(cursorEUR.getString(1), cursorEUR.getString(0)));
                cursorUSD.moveToNext();
                dbArraysUSD.add(new FragmentCurranciesChart.DBArray(cursorUSD.getString(1), cursorUSD.getString(0)));
                cursorRUR.moveToNext();
                dbArraysRUR.add(new FragmentCurranciesChart.DBArray(cursorRUR.getString(1), cursorRUR.getString(0)));
            }
        }

        for (int i = 0; i < dbArraysEUR.size() / 2; i++) {
            DBArray tempDBArray = dbArraysEUR.get(i);
            dbArraysEUR.set(i, dbArraysEUR.get(dbArraysEUR.size() - 1 - i));
            dbArraysEUR.set(dbArraysEUR.size() - 1 - i, tempDBArray);

            tempDBArray = dbArraysUSD.get(i);
            dbArraysUSD.set(i, dbArraysUSD.get(dbArraysUSD.size() - 1 - i));
            dbArraysUSD.set(dbArraysUSD.size() - 1 - i, tempDBArray);

            tempDBArray = dbArraysRUR.get(i);
            dbArraysRUR.set(i, dbArraysRUR.get(dbArraysRUR.size() - 1 - i));
            dbArraysRUR.set(dbArraysRUR.size() - 1 - i, tempDBArray);
        }

        DataPoint[] dataPointsEUR = new DataPoint[dbArraysEUR.size()];
        DataPoint[] dataPointsUSD = new DataPoint[dbArraysUSD.size()];
        DataPoint[] dataPointsRUR = new DataPoint[dbArraysRUR.size()];

        for (int i = 0; i < dataPointsEUR.length; i++) {
            dataPointsEUR[i] = new DataPoint(dbArraysEUR.get(i).getDate(), dbArraysEUR.get(i).getValue());
            dataPointsUSD[i] = new DataPoint(dbArraysUSD.get(i).getDate(), dbArraysUSD.get(i).getValue());
            dataPointsRUR[i] = new DataPoint(dbArraysRUR.get(i).getDate(), dbArraysRUR.get(i).getValue());
        }
        LineGraphSeries<DataPoint> lineGraphSeriesEUR = new LineGraphSeries<>(dataPointsEUR);
        LineGraphSeries<DataPoint> lineGraphSeriesUSD = new LineGraphSeries<>(dataPointsUSD);
        LineGraphSeries<DataPoint> lineGraphSeriesRUR = new LineGraphSeries<>(dataPointsRUR);
        lineGraphSeriesEUR.setDrawDataPoints(true);
        lineGraphSeriesEUR.setColor(Color.BLUE);
        lineGraphSeriesEUR.setTitle(DataBaseHelper.Strings.EUR);
        lineGraphSeriesUSD.setDrawDataPoints(true);
        lineGraphSeriesUSD.setColor(Color.GREEN);
        lineGraphSeriesUSD.setTitle(DataBaseHelper.Strings.USD);
        lineGraphSeriesRUR.setDrawDataPoints(true);
        lineGraphSeriesRUR.setColor(Color.RED);
        lineGraphSeriesRUR.setTitle(DataBaseHelper.Strings.RUR);


        switch (mainCurrancy) {
            case DataBaseHelper.Strings.EUR:
                lineGraphSeriesEUR.setThickness(15);

                break;
            case DataBaseHelper.Strings.USD:
                lineGraphSeriesUSD.setThickness(15);

                break;
            case DataBaseHelper.Strings.RUR:
                lineGraphSeriesRUR.setThickness(15);


                break;
        }

        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScalable(true);

        graphView.addSeries(lineGraphSeriesEUR);
        graphView.addSeries(lineGraphSeriesUSD);
        graphView.addSeries(lineGraphSeriesRUR);

        graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(graphView.getContext()));
        graphView.getGridLabelRenderer().setNumHorizontalLabels(dbArraysEUR.size());

        graphView.getViewport().setMinX(dbArraysEUR.get(0).getDate().getTime());
        graphView.getViewport().setMaxX(dbArraysEUR.get(dbArraysEUR.size() - 1).getDate().getTime());

        //
        graphView.getViewport().setMinY(dbArraysEUR.get(0).getValue());
        graphView.getViewport().setMaxY((dbArraysEUR.get(dbArraysEUR.size() - 1).getValue()) * 1.5);
        //
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getGridLabelRenderer().setHumanRounding(true);

        graphView.getLegendRenderer().setVisible(true);
        graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);




        graphView.setTitle("UAH/" + mainCurrancy);
        graphView.getGridLabelRenderer().setVerticalAxisTitle("UAH");
        graphView.getGridLabelRenderer().setHorizontalAxisTitle("DAYS");

        graphView.getGridLabelRenderer().setVerticalLabelsAlign(Paint.Align.LEFT);
        graphView.getGridLabelRenderer().setTextSize(25);
        graphView.getGridLabelRenderer().setHorizontalLabelsAngle(120);


        builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setCancelable(true);
        alertDialog = builder.create();
        return alertDialog;
    }

    class DBArray {

        private Date date;
        private double value;
        private Calendar calendar;
        private Date d;

        public DBArray(String date, String value) {
            this.date = formatDate(date);
            this.value = Float.valueOf(value);
        }

        public Date getDate() {
            return date;
        }

        public double getValue() {
            return value;
        }

        private Date formatDate(String dateInOldFormat) {
            Calendar c = Calendar.getInstance();
            c.set(Integer.parseInt(dateInOldFormat.substring(0, 4)), Integer.parseInt(dateInOldFormat.substring(4, 6)) - 1, Integer.parseInt(dateInOldFormat.substring(6, 8)));
            Date date = c.getTime();
            return date;
        }

    }


}
