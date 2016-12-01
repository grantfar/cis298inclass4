package edu.kvcc.cis298.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

/**
 * Created by dbarnes on 10/5/2016.
 */
public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckbox;
    private Button mReportButton;

    //This method allows any class the ability to call this method
    //and get a new properly formatted fragment. Since we want any
    //new fragment to have the information from a specific crime
    //we will require that this method be used, to create the new
    //fragment.
    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get the UUID out from the fragment arguments.
        //In order to get the UUID out from the arguments we need
        //to use getSerializableExtra. That method allows us to get
        //out an object that was stored as an Arg. The catch is that
        //the object we want to store must implement the Serializable
        //interface in order to be able to be sent in the args, and then
        //retrived with getSerializableExtra.

        //The getArguments method will return a bundle object that was
        //set when the fragment got created. We have created a static
        //method at the top of this file to get a new fragment created.
        //That method accepts a UUID and then stores it in the args
        //for the new fragment. We then get the UUID out right here.
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);

        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //Use the inflator to inflate the layout file we want to use with this fragment
        //The first parameter is the layout resource. The second is the passed in
        //container that is the parent widget.
        //The last parameter is whether or not to statically assign the fragment
        //to the parent (FrameLayout) container.
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        //Get a reference to the EditText Widget in the layout file.
        //instead of just calling findViewById, which is a activity method,
        //we need to call the findViewById that is part of the view we just created.
        //aside from that, it operates the same.
        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //We aren't doing anything here.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //We aren't doing anything here either.
            }
        });

        //Set Date Button text
        mDateButton = (Button)v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSolvedCheckbox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckbox.setChecked(mCrime.isSolved());
        mSolvedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Set the crimes solve property
                mCrime.setSolved(isChecked);
            }
        });
        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT,getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.crime_report_subject));
                i.createChooser(i,getString(R.string.send_report));
                startActivity(i);
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //If the result code is not OK, we won't do any work.
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        //If the request code is the same one that was used to start the
        //dialog, we know we are doing dialog result work, and that
        //there exists a extra on the intent that contains the returning date
        if (requestCode == REQUEST_DATE) {
            //Get the date from the extras
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);

            //Set the date and the button text now that we have the date.
            mCrime.setDate(date);
            updateDate();
        }
    }

    //This will get called right before this fragment is paused.
    //It will happen most likely when the app is returning to the
    //list of items.
    @Override
    public void onPause() {
        super.onPause();

        //Get the crimLab and update the current crime in DB.
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    private String getCrimeReport() {

        //Set the string for whether the crime is solved to null
        String solvedString = null;

        //If the crime is solved, we will set string to the solved
        //string stored in strings.xml. Otherwise, the unsolved string.
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        //Declare a date format to use on the crime date
        String dateFormat = "EEE, MMM dd";
        //Get a string version of the data formated by the date format
        String dateString = DateFormat.format(dateFormat, mCrime.getDate())
                                .toString();

        //Get the suspect from the crime. It might be null if there is not
        //one set
        String suspect = mCrime.getSuspect();

        //If the suspect is null, use the unsolved string from strings.xml
        //otherwise use the solved string, and pass over the suspect as
        //the parameter for that string.
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        //Create the final report string using the above created strings
        //as the parameters for the crime_report string.
        String report = getString(R.string.crime_report,
                            mCrime.getTitle(),
                            dateString,
                            solvedString,
                            suspect);

        //Return the final built report string
        return report;
    }
}








