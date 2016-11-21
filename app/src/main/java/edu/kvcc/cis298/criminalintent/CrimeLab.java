package edu.kvcc.cis298.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.UUID;

import edu.kvcc.cis298.criminalintent.database.CrimeBaseHelper;
import edu.kvcc.cis298.criminalintent.database.CrimeCursorWrapper;
import edu.kvcc.cis298.criminalintent.database.CrimeDbSchema;

import static edu.kvcc.cis298.criminalintent.database.CrimeDbSchema.*;

/**
 * Created by dbarnes on 10/19/2016.
 */
public class CrimeLab {
    //Static variable to hold the instance of the CrimeLab
    private static CrimeLab sCrimeLab;

    //Class level variable to hold the sqlite database.
    private SQLiteDatabase mDatabase;
    //This context will be the hosting activity. It will get
    //assigned in the constructor.
    private Context mContext;

    //This is a static get method to get the single instance of the class.
    public static CrimeLab get(Context context) {
        //If we dont' have an instace, we create a new one.
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        //Regardless of whether we created it, or
        //it already existed, we need to return it
        return sCrimeLab;
    }

    //This is the constructor. It is private.
    //It can't be used from outside classes.
    private CrimeLab(Context context) {
        //Set the class level context to the one passed in.
        mContext = context.getApplicationContext();
        //Set the class level database
        mDatabase = new CrimeBaseHelper(mContext)
                .getWritableDatabase();
    }

    public void addCrime(Crime c) {
        //This will call the getContentValues method defined below
        //and retrive the key => value for each property of the crime.
        //We can then use the values to insert a new record into the
        //database
        ContentValues values = getContentValues(c);

        //Insert a new record into the database using the values
        mDatabase.insert(CrimeTable.NAME, null, values);
    }

    public List<Crime> getCrimes() {
        //This is temporary
        List<Crime> crimes = new ArrayList<>();

        //Define a crimeCursorWrapper to be used when reading the database
        //We are sending in null as the where clause, and the where args.
        //This means that there will be NO Where, and instead select
        //every record in the database.
        CrimeCursorWrapper cursor = queryCrimes(null, null);

        //Put this in a try in case an exception is thrown
        try {
            //Move the cursor to the first record
            cursor.moveToFirst();
            //While the cursor is not after the last record in the
            //result set of executing the query.
            while (!cursor.isAfterLast()) {
                //Take the crime that the cursor returns and add it to the list
                crimes.add(cursor.getCrime());
                //Move the cursor to the next record in the dataset
                cursor.moveToNext();
            }
        } finally {
            //Close the cursor now that we are done with it.
            cursor.close();
        }

        //Return the crimes
        return crimes;
    }

    public Crime getCrime(UUID id) {

        //Create a new Cursor to get one crime
        CrimeCursorWrapper cursor = queryCrimes(
                //This is the Where Clause that is sent over
                CrimeTable.Cols.UUID + " = ?",
                //This is the Where Args that get sent over for the Clause
                new String[] { id.toString() }
        );

        try {
            //Check to make sure there was a result. If not return null.
            if (cursor.getCount() == 0) {
                return null;
            }

            //Move to the first record in the result set
            cursor.moveToFirst();
            //Return the crime from that location
            return cursor.getCrime();
        } finally {
            //Close the cursor
            cursor.close();
        }
    }

    public void updateCrime(Crime crime) {
        //Convert the UUID to a string so that it can be used
        //in the where clause to know which record to update
        String uuidString = crime.getId().toString();
        //Get the Content values from the crime
        ContentValues values = getContentValues(crime);
        //Do the DB update
        //The update method takes in the table name,
        //the update values, and the where clause to
        //determine which record to update
        mDatabase.update(CrimeTable.NAME, values,
                //This makes the where clause parameterized
                //to prevent SQL injection attacks.
                //It will plug the second parameter String[]
                //in for the ? sections of the string.
                CrimeTable.Cols.UUID + " = ?",
                new String[] { uuidString});
    }

    private static ContentValues getContentValues(Crime crime)
    {
        //Make a content values object that will store key => value
        //As far as the DB is concened this will be the table
        //column we want to insert to, and then the value to insert.
        //This means that we need a statement for every column in the
        //database, and an associated value.
        ContentValues values = new ContentValues();
        //Put in the table column name, and the associated crime
        //fields value
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 :0);
        //Return the set of values.
        return values;
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  //orderBy
        );

        return new CrimeCursorWrapper(cursor);
    }
}
