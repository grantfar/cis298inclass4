package edu.kvcc.cis298.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by dbarnes on 10/19/2016.
 */
public class CrimeLab {
    //Static variable to hold the instance of the CrimeLab
    private static CrimeLab sCrimeLab;
    private List<Crime> mCrimes;

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
        mCrimes = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Crime crime = new Crime();
            crime.setTitle("Crime #" + i);
            crime.setSolved(i % 2 == 0);
            mCrimes.add(crime);
        }
    }

    public List<Crime> getCrimes() {
        return mCrimes;
    }

    public Crime getCrime(UUID id) {
        //This is a foreach loop. Syntax is different than C#
        for (Crime crime : mCrimes) {
            //If the current crime we are looking at has a id that
            //matches the passed in one, we return the crime
            if (crime.getId().equals(id)) {
                return crime;
            }
        }
        //Didn't find a match, return null.
        return null;
    }
}
