package edu.kvcc.cis298.criminalintent.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import edu.kvcc.cis298.criminalintent.database.CrimeDbSchema.CrimeTable;

/**
 * Created by dbarnes on 11/9/2016.
 */
public class CrimeBaseHelper extends SQLiteOpenHelper {

    //Version number that can be used to trigger a call to onUpgrade.
    //If when the application starts, the existing database version number
    //does not match the version number in the code, the onUpgrade method
    //will be called.
    private static final int VERSION = 1;
    //Constant for the database name
    private static final String DATABASE_NAME = "crimeBase.db";

    public CrimeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
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
                CrimeTable.Cols.SOLVED +
                ")"
        );
    }

    //onUpgrade method that will be called if the version number
    //listed above does not match the version number of the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Going to leave this blank. We won't do work here.
    }
}
