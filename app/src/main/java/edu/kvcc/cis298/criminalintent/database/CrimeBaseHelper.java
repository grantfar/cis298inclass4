package edu.kvcc.cis298.criminalintent.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.UUID;

import edu.kvcc.cis298.criminalintent.Crime;
import edu.kvcc.cis298.criminalintent.CrimeLab;
import edu.kvcc.cis298.criminalintent.R;
import edu.kvcc.cis298.criminalintent.database.CrimeDbSchema.CrimeTable;

/**
 * Created by dbarnes on 11/9/2016.
 */
public class CrimeBaseHelper extends SQLiteOpenHelper {

    //Add a variable for the class level context
    Context mContext;
    //Add a flag to know whether to seed the database or not
    boolean mSeedDatabase;

    //Version number that can be used to trigger a call to onUpgrade.
    //If when the application starts, the existing database version number
    //does not match the version number in the code, the onUpgrade method
    //will be called.
    private static final int VERSION = 1;
    //Constant for the database name
    private static final String DATABASE_NAME = "crimeBase.db";

    public CrimeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        mContext = context;
        //Initalize the seed flag to false
        mSeedDatabase = false;
    }

    //onCreate method that will be called to create the database
    //if it does not exist
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + CrimeTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                CrimeTable.Cols.UUID + ", " +
                CrimeTable.Cols.TITLE + ", " +
                CrimeTable.Cols.DATE + ", " +
                CrimeTable.Cols.SOLVED + ", " +
                CrimeTable.Cols.SUSPECT +
                ")"
        );

        //Flip the seed flag to true
        mSeedDatabase = true;
    }

    //onUpgrade method that will be called if the version number
    //listed above does not match the version number of the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Going to leave this blank. We won't do work here.
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        //Check to see if we should seed the database
        //If mSeedDatabase is true, we should seed it.
        if (mSeedDatabase) {
            //Call the load crime list method to do the seed.
            loadCrimeList();
        }
    }

    private void loadCrimeList() {

        //Define a Scanner which will be used to read in the file
        Scanner scanner = null;

        try {
            //instanciate a new scanner
            //Use some method chaining to get access to the file.
            //Use the context to get at resources, and once we have the resources
            //call openRawResource passing in the location of the file.
            //The location of the file will be R.raw."The name of the file"
            //Remember that files here must be all lowercase.
            scanner = new Scanner(mContext.getResources().openRawResource(R.raw.crimes));

            //while the scanner has another line to read
            while(scanner.hasNextLine()) {

                //Get the next line and split it into parts
                String line = scanner.nextLine();
                String parts[] = line.split(",");

                //Assign each part to a local variable
                String stringUUID = parts[0];
                String title = parts[1];
                String stringDate = parts[2];
                String stringSolved = parts[3];

                //Setup some vars for doing parsing
                UUID uuid = UUID.fromString(stringUUID);

                //Create a new DateFormat to use in parsing the date
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                //Use the new format to parts the string date.
                Date date = format.parse(stringDate);

                //Set a bool as to whether it is solved or not
                //This is a ternary operator. It is a shorthand if else
                //The part between the ? and : happens when true.
                //The part after the : happens when false.
                boolean solved = (stringSolved.equals("1")) ? true : false;

                //Get a reference to the CrimeLab singleton
                CrimeLab mCrimes = CrimeLab.get(mContext);

                //Add the Crime to the database
                mCrimes.addCrime(new Crime(uuid, title, date, solved));

            }

        } catch (Exception e) {
            //On exception, just log out the exception to string
            Log.e("Read CSV", e.toString());
        } finally {
            //Check to make sure that the scanner actually got instanciated
            //and if so, close it.
            if (scanner != null) {
                scanner.close();
            }
        }
    }
}
