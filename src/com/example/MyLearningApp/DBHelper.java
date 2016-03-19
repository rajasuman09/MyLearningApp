package com.example.MyLearningApp;

/**
 * Created by Raja.Chirala on 09/02/2016.
 */
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class DBHelper extends SQLiteOpenHelper{

    private static String DB_PATH = "";
    private static String DB_NAME = "VCal.sqlite";
    private SQLiteDatabase myDataBase;
    private final Context myContext;
    private static final int DATABASE_VERSION = 4;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        this.myContext = context;

        File f = new File(DB_PATH);
        if (!f.exists()) {
            f.mkdir();
        }

    }

    /**
     * Initializes database. Creates database if doesn't exist.
     */
    public void initialize() {
        if (databaseExists()) {
            try {
                openDataBase();
            }catch(SQLException sqle){
                throw sqle;
            }
            if (DATABASE_VERSION !=  myDataBase.getVersion()) {
                File dbFile = myContext.getDatabasePath(DB_NAME);
                if (!dbFile.delete()) {
                    Log.w("DBHelper","Unable to update database");
                }
            }
        }
        if (!databaseExists()) {
            try {
                createDataBase();
            } catch (IOException ioe) {
                throw new Error("Unable to create database");
            }
        }
    }

    /**
     * Returns true if database file exists, false otherwise.
     * @return
     */
    private boolean databaseExists() {
        File dbFile = myContext.getDatabasePath(DB_NAME);
        return dbFile.exists();
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if(dbExist){
            //do nothing - database already exist
        }else{

            //By calling this method an empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            SQLiteDatabase db = this.getWritableDatabase();
            if (db.isOpen()){
                db.close();
            }

            try {
                copyDataBase();
                final Thread t = new Thread() {
                    @Override
                    public void run() {
                        try {
                            calculate_festival_days(Calendar.getInstance().get(Calendar.YEAR));
                            }
                            catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    };
                t.start();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //database doesn't exist yet.

        }

        if(checkDB != null){
            checkDB.close();
        }

        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the input file to the output file
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.

    public ArrayList<FestivalDetails> getAllFestivals()
    {
        ArrayList<FestivalDetails> array_list = new ArrayList<FestivalDetails>();
        FestivalDetails fd;
        myDataBase = this.getReadableDatabase();
        Cursor res =  myDataBase.rawQuery( "select * from festivals where calendar_date is not null order by calendar_date ASC", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            fd = new FestivalDetails();
            fd.setEventDate(res.getString(res.getColumnIndex("calendar_date")));
            fd.setEventName(res.getString(res.getColumnIndex("event_name")));
            array_list.add(fd);
            res.moveToNext();
        }
        res.close();
        myDataBase.close();
        return array_list;
    }

    public ArrayList<String> getTodaysEvents(String tithi_name, String paksa_name, String masa_name)
    {
        ArrayList<String> array_list = new ArrayList<String>();

        myDataBase = this.getReadableDatabase();
        Cursor res =  myDataBase.rawQuery( "select * from festivals where tithi='"+tithi_name + "' and paksa='"+ paksa_name + "' and masa='"+ masa_name + "'", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex("event_name")));
            res.moveToNext();
        }
        res.close();
        myDataBase.close();
        return array_list;
    }

    public void calculate_festival_days(int year){
        Calendar event_tracker = Calendar.getInstance();
        event_tracker.set(year, 0, 1);
        MainActivity helper = new MainActivity();
        myDataBase = this.getWritableDatabase();
        String strSQL = "UPDATE festivals SET calendar_date = NULL";
        myDataBase.execSQL(strSQL);
        do{
            int month = event_tracker.get(Calendar.MONTH);
            int day = event_tracker.get(Calendar.DAY_OF_MONTH);
            double sun_longitude = helper.getSunLongitude(year, month + 1, day);
            double moon_longitude = helper.getMoonLongitude(year, month + 1, day);
            int tithi = helper.getTithi(sun_longitude, moon_longitude);
            int paksa = helper.getPaksa(tithi);
            String paksa_name = helper.getPaksaName(paksa);
            String masa_name = helper.getMasa(year, month, day, paksa);
            String tithi_name = helper.getTithiName(tithi);
            Date date = event_tracker.getTime();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

            strSQL = "UPDATE festivals SET calendar_date = '" + dateFormatter.format(date) + "' WHERE tithi = '" + tithi_name + "' AND paksa = '" + paksa_name + "' AND masa = '" + masa_name + "'";

            myDataBase.execSQL(strSQL);
            event_tracker.add(Calendar.DATE,1);
        } while(event_tracker.get(Calendar.YEAR) == year);

        myDataBase.close();

    }

    public boolean is_current_year_data(){
        boolean is_current_year;
        Calendar current_date = Calendar.getInstance();
        myDataBase = this.getReadableDatabase();
      //  Cursor res =  myDataBase.rawQuery("SELECT strftime('%Y', calendar_date) year,  calendar_date FROM festivals WHERE calendar_date IS NOT NULL LIMIT 1", null);
        Cursor res =  myDataBase.rawQuery( "select strftime('%Y', calendar_date) year from festivals where calendar_date IS NOT NULL", null );

        res.moveToFirst();
        int year=0;

        if(res.isAfterLast() == false) {
            year = res.getInt(0);
        }

        if(current_date.get(Calendar.YEAR) == year){
            is_current_year = true;
        }
        else{
            is_current_year = false;
        }
        res.close();
        myDataBase.close();

        return is_current_year;
    }

}

