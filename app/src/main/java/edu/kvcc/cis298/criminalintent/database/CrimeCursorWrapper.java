package edu.kvcc.cis298.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import edu.kvcc.cis298.criminalintent.Crime;

import static edu.kvcc.cis298.criminalintent.database.CrimeDbSchema.*;

/**
 * Created by dbarnes on 11/21/2016.
 */
public class CrimeCursorWrapper extends CursorWrapper {

    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        //Get out the values for a given column name with each one of these
        //statements
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));

        //Create a new crime using the constructor we just added.
        //It sends over a UUID and gets a new crime with that UUID.
        Crime crime = new Crime(UUID.fromString(uuidString));

        //Set the remaining properties on the crime object
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setSuspect(suspect);

        //Return the actual crime
        return crime;
    }
}
