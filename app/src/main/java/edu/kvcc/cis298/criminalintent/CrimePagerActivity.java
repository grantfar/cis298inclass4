package edu.kvcc.cis298.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.List;
import java.util.UUID;

/**
 * Created by dbarnes on 10/26/2016.
 */
public class CrimePagerActivity extends FragmentActivity {

    private static final String EXTRA_CRIME_ID =
            "edu.kvcc.cis298.cis298inclass4.crime_id";

    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    private ViewPager mViewPager;
    private List<Crime> mCrimes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        //Fetch the crimeId out of the extra. It was put into the extra in
        //the static method at the top of this file.
        UUID crimeId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_CRIME_ID);

        mViewPager = (ViewPager) findViewById(R.id.activity_crime_pager_view_pager);

        mCrimes = CrimeLab.get(this).getCrimes();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                //Get the crime from the crimelab that we want to have displayed
                Crime crime = mCrimes.get(position);
                //Use the public static method on the CrimeFragment class to get
                //a new fragment that will be displayed.
                //The work for creating the method we are calling here was done
                //in chapter 10.
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        //Run a loop to from 0 to the size of the list.
        //If we find a match between the current index's crime id
        //and the crimeId we are looking for, we will use that
        //index to set the current item on the view pager.
        //At least we are breaking after we find a match.
        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
