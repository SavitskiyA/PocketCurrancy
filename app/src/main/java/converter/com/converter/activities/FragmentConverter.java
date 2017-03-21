package converter.com.converter.activities;


import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import converter.com.converter.R;
import converter.com.converter.support.CustomDialog;
import converter.com.converter.support.DataBaseHelper;
import converter.com.converter.support.SpinnerAdapter;
import converter.com.converter.support.SpinnerModel;

import static converter.com.converter.R.id.rb_curs_NBU;
import static converter.com.converter.R.id.rb_curs_PB;


public class FragmentConverter extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private Spinner sp_from, sp_to;
    private ArrayList<SpinnerModel> arrayList;
    private SpinnerAdapter spinnerAdapter;
    private RadioButton rb_bp, rb_nbu;
    private Button button;
    private DataBaseHelper dbh;
    private String selected_cur_from, selected_cur_to;
    private EditText edt_from, edt_to;

    public FragmentConverter() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arrayList = new ArrayList<>();
        dbh = new DataBaseHelper(this.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_converter, container, false);
        sp_from = (Spinner) v.findViewById(R.id.sp_cur_from);
        sp_to = (Spinner) v.findViewById(R.id.sp_cur_to);
        rb_bp = (RadioButton) v.findViewById(rb_curs_PB);
        rb_nbu = (RadioButton) v.findViewById(R.id.rb_curs_NBU);
        spinnerAdapter = new SpinnerAdapter(this.getActivity(), arrayList);
        sp_from.setAdapter(spinnerAdapter);
        sp_to.setAdapter(spinnerAdapter);
        sp_from.setOnItemSelectedListener(this);
        sp_to.setOnItemSelectedListener(this);
        rb_bp.setOnClickListener(this);
        rb_nbu.setOnClickListener(this);
        button = (Button) v.findViewById(R.id.button);
        button.setOnClickListener(this);
        setDataToModel();
        edt_from = (EditText) v.findViewById(R.id.edt_from);
        edt_to = (EditText) v.findViewById(R.id.edt_to);
        return v;
    }


    public void setDataToModel() {
        TypedArray img_array = getResources().obtainTypedArray(R.array.img_array);
        List<String> currancies = Arrays.asList(getResources().getStringArray(R.array.cur_array));
        List<String> curAbr = Arrays.asList(getResources().getStringArray(R.array.curAbr_array));
        for (int i = 0; i < currancies.size(); i++) {
            arrayList.add(new SpinnerModel(currancies.get(i), img_array.getResourceId(i, 0), curAbr.get(i)));
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.sp_cur_from)
            selected_cur_from = arrayList.get(position).getCurAbr();
        if (parent.getId() == R.id.sp_cur_to)
            selected_cur_to = arrayList.get(position).getCurAbr();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                String edt_from_value = edt_from.getText().toString();
                if (rb_bp.isChecked())
                    getCombinationRes(R.id.rb_curs_PB, selected_cur_from, selected_cur_to, edt_from_value);
                if (rb_nbu.isChecked())
                    getCombinationRes(R.id.rb_curs_NBU, selected_cur_from, selected_cur_to, edt_from_value);
                break;
            case R.id.rb_curs_PB:
                rb_bp.setChecked(true);
                rb_nbu.setChecked(false);
                break;
            case R.id.rb_curs_NBU:
                rb_nbu.setChecked(true);
                rb_bp.setChecked(false);
                break;
        }
    }


    public String getCurFromUAHPB(String sum_UAH, String cur) {
        String rate = dbh.getRatePB(cur, DataBaseHelper.Strings.SALE);
        if (rate != null) {
            double res = Double.parseDouble(sum_UAH) / Double.parseDouble(rate);
            return String.format("%.2f", res);
        } else {
            return null;
        }
    }

    public String getUAHFromCurPB(String sum_cur, String cur) {
        String rate = dbh.getRatePB(cur, DataBaseHelper.Strings.BUY);
        if (rate != null) {
            double res = Double.parseDouble(sum_cur) * Double.parseDouble(rate);
            return String.format("%.2f", res);
        } else return null;
    }

    public String getValFromValPB(String sum, String cur_from, String cur_to) {
        String rateCurFrom = dbh.getRatePB(cur_from, DataBaseHelper.Strings.BUY);
        String rateCurTo = dbh.getRatePB(cur_to, DataBaseHelper.Strings.SALE);
        if (rateCurFrom != null && rateCurTo != null) {
            double res = Double.parseDouble(sum) * ((Double.parseDouble(rateCurFrom) / Double.parseDouble(rateCurTo)));
            return String.format("%.2f", res);
        }
        return null;

    }

    public String getCurFromUAHNBU(String sum_UAH, String cur) {
        String rate = dbh.getRateNBU(cur);
        if (rate != null) {
            double res = Double.parseDouble(sum_UAH) / Double.parseDouble(rate);
            return String.format("%.2f", res);
        }
        return null;
    }

    public String getUAHFromCurNBU(String sum_cur, String cur) {
        String rate = dbh.getRateNBU(cur);
        if (rate != null) {
            double res = Double.parseDouble(sum_cur) * Double.parseDouble(dbh.getRateNBU(cur));
            return String.format("%.2f", res);
        }
        return null;
    }

    public String getValFromValNBU(String sum, String cur_from, String cur_to) {
        String rateCurFrom = dbh.getRateNBU(cur_from);
        String rateCurTo = dbh.getRateNBU(cur_to);
        if (rateCurFrom != null && rateCurTo != null) {
            double res = Double.parseDouble(sum) * ((Double.parseDouble(rateCurFrom) / Double.parseDouble(rateCurTo)));
            return String.format("%.2f", res);
        }
        return null;
    }


    public void getCombinationRes(int radioButtonId, String selected_sp_from, String selected_sp_to, String edt_from) {
        if (edt_from.matches("[0-9]+") && edt_from.length() > 1) {
            switch (radioButtonId) {
                case R.id.rb_curs_PB:
                    if (selected_sp_from.equals(selected_sp_to)) {
                        edt_to.setText(edt_from);
                    }

                    if (selected_sp_from.equals("UAH") && !selected_sp_to.equals("UAH")) {
                        String res = getCurFromUAHPB(edt_from, selected_sp_to);
                        if (res != null) edt_to.setText(res);
                        else showCustomDialog();
                    }

                    if (!selected_sp_from.equals("UAH") && selected_sp_to.equals("UAH")) {
                        String res = getUAHFromCurPB(edt_from, selected_sp_from);
                        if (res != null) edt_to.setText(res);
                        else showCustomDialog();
                    }
                    boolean b = (!selected_sp_from.equals("UAH")) && (!selected_sp_to.equals("UAH"));
                    if (b) {
                        String res = getValFromValPB(edt_from, selected_sp_from, selected_sp_to);
                        if (res != null) edt_to.setText(res);
                        else showCustomDialog();
                    }

                    break;
                case R.id.rb_curs_NBU:
                    if (selected_sp_from.equals(selected_sp_to)) {
                        edt_to.setText(edt_from);
                    }

                    if (selected_sp_from.equals("UAH") && !selected_sp_to.equals("UAH")) {
                        String res = getCurFromUAHNBU(edt_from, selected_sp_to);
                        if (res != null) edt_to.setText(res);
                        else showCustomDialog();
                    }

                    if (!selected_sp_from.equals("UAH") && selected_sp_to.equals("UAH")) {
                        String res = getUAHFromCurNBU(edt_from, selected_sp_from);
                        if (res != null) edt_to.setText(res);
                        else showCustomDialog();
                    }
                    if (!selected_sp_from.equals("UAH") && !selected_sp_to.equals("UAH")) {
                        String res = getValFromValNBU(edt_from, selected_sp_from, selected_sp_to);
                        if (res != null) edt_to.setText(res);
                        else showCustomDialog();
                    }
                    break;
            }
        } else Toast.makeText(this.getActivity(), "Enter only digits", Toast.LENGTH_SHORT).show();
    }

    public void showCustomDialog() {
        CustomDialog customDialog = new CustomDialog("There is no data in database. Try to enable internet connection to get data from sorce");
        customDialog.show(getFragmentManager(), "Error of getting data from database");
    }


}
