package edu.kvcc.cis298.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by dbarnes on 11/2/2016.
 */
public class DatePickerFragment extends DialogFragment {

    //Set a string to use as the KEY for the extras that will be in
    //an intent that is used as the return data for the result of this
    //dialog closing.
    //It must be public because we will access if from CrimeFragment
    public static final String EXTRA_DATE =
            "edu.kvcc.cis298.cis298inclass4.date";

    //This will be the key for the date that is sent over
    //the date picker. The key and date will be put in a bundle
    private static final String ARG_DATE = "date";

    //Local variable for the date picker
    private DatePicker mDatePicker;

    //Static method that another fragment can use to get
    //a new datePickerFragment with a correct set date
    public static DatePickerFragment newInstance(Date date) {
        //make a bundle object to attach data to
        Bundle args = new Bundle();
        //Add the data to the bundle
        args.putSerializable(ARG_DATE, date);

        //Create the new datepicker fragment
        DatePickerFragment fragment = new DatePickerFragment();
        //Set the arguments on the fragment
        fragment.setArguments(args);
        //return the created fragment
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //Get the date from the arguments that were sent when the
        //fragment got created
        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        //Create a Calendar object that can be used to do translations
        //from a date object into year, month, and day.
        Calendar calendar = Calendar.getInstance();
        //Set the time of the calendar to the date that came in the bundle
        calendar.setTime(date);

        //From the calendar, fetch out the year, month, and day.
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        //Inflate the view for this dialog.
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_date, null);

        //now that we have the view, we can get a reference to the
        //date picker widget
        mDatePicker = (DatePicker)v.findViewById(R.id.dialog_date_date_picker);

        //Set the inital date for the date picker.
        mDatePicker.init(year, month, day, null);

        //AlertDialog has a builder method that returns an object
        //that can be used to build a dialog. The dialog is built by
        //chaining methods unitl all the options you want are set.
        //Final creation with the set options is done by lastly calling
        //the create method. It is at that point that a dialog object is
        //returned.
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok,
                        //Set the onclick listener for the dialog
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //Get the year, month, and day out of the
                                //date picker widget
                                int year = mDatePicker.getYear();
                                int month = mDatePicker.getMonth();
                                int day = mDatePicker.getDayOfMonth();

                                //Use the GregorianCalendar object to create
                                //a new date object using the 3 values we
                                //retrived from the DatePicker
                                Date date = new GregorianCalendar(year, month, day)
                                        .getTime();
                                //Call the sendResult method below here
                                //Send over Activity.Result_OK as the int
                                //for the result code. This will signify that
                                //the dialog terminated correctly.
                                //Also send the date over as the data to return
                                sendResult(Activity.RESULT_OK, date);
                            }
                        })
                .create();
    }

    private void sendResult(int resultCode, Date date) {
        //Safety check to ensure that the target fragment was set.
        if (getTargetFragment() == null) {
            return;
        }

        //Create a new intent object to hold our return data
        Intent intent = new Intent();
        //Set the date as an extra on the return intent
        intent.putExtra(EXTRA_DATE, date);

        //We know that the target fragment is set due to the above check, so
        //we get the target fragment, and explicitly call onActivityResult
        //on it. We send over the request code, the result code, and the
        //intent that contains our data. The crime fragment can then handle
        //whatever work it needs to do with this information in the
        //onActivityResult method.
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);

    }
}
