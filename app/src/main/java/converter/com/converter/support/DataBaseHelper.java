package converter.com.converter.support;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.Settings;

import java.io.StringBufferInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.R.attr.type;

/**
 * Created by Andrey on 09.02.2017.
 */

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String DATABASENAME = "currancies.db";

    public static final class Strings {
        public static final String PB = "PB";
        public static final String NBU = "NBU";
        public static final String BUY = "buy";
        public static final String SALE = "sale";
        public static final String BRANCH = "branch";
        public static final String CARDS = "cards";
        public static final String USD = "USD";
        public static final String EUR = "EUR";
        public static final String RUR = "RUR";
        public static final String PB_BRANCH_CUR_RATE = "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5";
        public static final String PB_CARDS_CUR_RATE = "https://api.privatbank.ua/p24api/pubinfo?exchange&json&coursid=11";
        public static final String NBU_CUR_RATE = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";
        public static final String NO_DATA = "There is no currancies rates by today in database. Enable internet, please to update it. Thank you";

    }

    public static final class CatalogueTable {
        public static final String NAME = "catalogue";
        public static final String COl_1 = "cur_id";
        public static final String COl_2 = "cur_name";
    }

    public static final class PBTable {
        public static final String NAME = "pb";
        public static final String COl_1 = "id";
        public static final String COl_2 = "date";
        public static final String COl_3 = "cur_id";
        public static final String COl_4 = "buy";
        public static final String COl_5 = "sale";
        public static final String COl_6 = "type";
    }


    public static final class NBUtable {
        public static final String NAME = "nbu";
        public static final String COl_1 = "id";
        public static final String COl_2 = "date";
        public static final String COl_3 = "cur_id";
        public static final String COl_4 = "value";

    }

    //SQLiteDataBAse
    private SQLiteDatabase db;

    public static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        return dateFormat.format(date).toString();
    }

    private static Date yesterday(int days) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -days);
        return cal.getTime();
    }

    public static String getPrevDate(int days) {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(yesterday(days));
    }


    public DataBaseHelper(Context context) {
        super(context, DATABASENAME, null, 1);
        db = this.getWritableDatabase();


    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table catalogue (cur_id integer primary key autoincrement, cur_name text)");
        db.execSQL("create table pb (id integer primary key autoincrement, date text not null, cur_id integer not null, buy real not null, sale real not null, type text not null, foreign key(cur_id) references catalogue (cur_id))");
        db.execSQL("create table nbu (id integer primary key autoincrement, date text not null, cur_id integer not null, value real not null, foreign key(cur_id) references catalogue (cur_id))");
        db.execSQL("insert into catalogue values(null, 'EUR')");
        db.execSQL("insert into catalogue values(null, 'USD')");
        db.execSQL("insert into catalogue values(null, 'RUR')");
//        insertDataIntoCatalogue();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS catalogue");
        db.execSQL("DROP TABLE IF EXISTS pb");
        db.execSQL("DROP TABLE IF EXISTS nbu");
        onCreate(db);

    }



    public void insertIntoPB(String date, String cur_name, String buy, String sale, String type) {
        db = this.getWritableDatabase();

        db.execSQL("insert into pb values(null, '" + date + "', (select cur_id from catalogue where cur_name='" + cur_name + "'), '" + buy + "', '" + sale + "', '" + type + "')");
    }


    public void insertIntoNBU(String date, String cur_name, String value) {
        db = this.getWritableDatabase();
        db.execSQL("insert into nbu values(null, '" + date + "', (select cur_id from catalogue where cur_name='" + cur_name + "'), '" + value + "')");
    }


    public Cursor getValueFromPB(String date, String currancy, String type) {
        db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select buy, sale from pb where (cur_id = (select cur_id from catalogue where cur_name = '" + currancy + "')) and date = '" + date + "' and type = '" + type + "'", null);
        return res;
    }

    public Cursor getValueFromPB(String date, String currancy, String what, String type) {
        db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select "+ what +" from pb where (cur_id = (select cur_id from catalogue where cur_name = '" + currancy + "')) and date = '" + date + "' and type = '" + type + "'", null);
        return res;
    }

    public Cursor getValueFromNBU(String date, String currancy) {
        db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select value from nbu where (cur_id = (select cur_id from catalogue where cur_name = '" + currancy + "')) and date = '" + date + "'", null);
        return res;
    }

    public Cursor getLastRow(String table) {
        db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select date from " + table + " order by id desc limit 1", null);
        return res;
    }

    public String getLastRowDate(Cursor res) {
        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            buffer.append(res.getString(0));
        }
        System.out.println(buffer.toString());
        return buffer.toString();
    }


    public Cursor getlastCursFromPB(String lastDate, String type) {
        db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from pb where date='" + lastDate + "' and type='" + type + "'", null);
        return res;
    }


    public Cursor getlastCursFromNBU(String lastDate) {
        db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from nbu where date='" + lastDate + "'", null);
        return res;
    }


    public String getRatePB(String cur_name, String type) {
        db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select " + type + " from pb where (cur_id = (select cur_id from catalogue where cur_name = '" + cur_name + "')) order by id desc limit 1", null);
        if (res.getCount() == 0) {
            return null;
        } else {
            StringBuffer buffer = new StringBuffer();
            while (res.moveToNext()) {
                buffer.append(res.getString(0));
            }
            return buffer.toString();
        }


    }

    public String getRateNBU(String cur_name) {
        db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select value from nbu where (cur_id = (select cur_id from catalogue where cur_name = '" + cur_name + "')) order by id desc limit 1", null);
        if (res.getCount() == 0) {
            return null;
        } else {
            StringBuffer buffer = new StringBuffer();
            while (res.moveToNext()) {
                buffer.append(res.getString(0));
            }
            return buffer.toString();
        }
    }

    public Cursor getDataFromPB(String currancy, String what, String type, String rowCounts) {
        db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select " + what + ", date from pb where (cur_id = (select cur_id from catalogue where cur_name = '" + currancy + "')) and type = '" + type + "' order by date desc limit " + rowCounts, null);
        return res;
    }


    public Cursor getDataFromNBU(String currancy, String rowCounts) {
        db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select value, date from nbu where (cur_id = (select cur_id from catalogue where cur_name = '" + currancy + "')) order by date desc limit " + rowCounts, null);
        return res;
    }






}



