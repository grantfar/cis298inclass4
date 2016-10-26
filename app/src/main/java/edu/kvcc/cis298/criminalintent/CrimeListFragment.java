package edu.kvcc.cis298.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by dbarnes on 10/19/2016.
 */
public class CrimeListFragment extends Fragment {

    private RecyclerView mCrimeRecyclerView;
    //Declare a new crimeAdapter. This comes from the inner class
    //that is written below in this file.
    private CrimeAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Inflate the view from the layout file into a view variable
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        //Get a handle to the recycler view using findViewById
        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);

        //Set the layout manger for the recyclerview. It needs to know how to layout
        //the indiviual views that make up the recyclerView. This linear layout manager
        //will tell the recyclerView to lay them out in a vertical fashion.
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Call the updateUI method which will setup the recyclerView with data.
        updateUI();

        //return the view
        return view;
    }

    private class CrimeHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

        //Add a title variable to the viewHolder
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckBox;

        //Variable to hold a single crime
        private Crime mCrime;

        //Constructor for the CrimeHolder.
        public CrimeHolder(View itemView) {
            //Call the parent constructor
            super(itemView);
            itemView.setOnClickListener(this);

            //Get the itemView that is passed into this constructor and assign it
            //to the class level variable.
            mTitleTextView = (TextView) itemView
                    .findViewById(R.id.list_item_crime_title_text_view);
            mDateTextView = (TextView) itemView
                    .findViewById(R.id.list_item_crime_date_text_view);
            mSolvedCheckBox = (CheckBox) itemView
                    .findViewById(R.id.list_item_crime_solved_check_box);
        }

        //Write a method in here to take in an instance of a crime
        //and then assign the crime properties to the various
        //view widgets
        public  void bindCrime(Crime crime) {
            //Assign the passed in crime to the class level one
            //This may not be needed? Could use the local one.
            //Maybe we will need it later on.
            mCrime = crime;
            //Set the widget controls with the data from the crime.
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
            mSolvedCheckBox.setChecked(mCrime.isSolved());
        }

        @Override
        public void onClick(View v) {
            Intent intent = CrimeActivity.newIntent(getActivity(), mCrime.getId());
            startActivity(intent);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        //Setup local version of the list of crimes
        private List<Crime> mCrimes;

        //Constructor to set the list of crimes
        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }


        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //Get a reference to a layout inflator that can inflate our view
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            //Use the inflator to inflate the default android list view.
            //We did not write this layout file. It is a standard android one.
            View view = layoutInflater.inflate(R.layout.list_item_crime, parent, false);
            //Return a new crimeHolder and pass in the view we just created.
            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            //Get the crime out of the list of crimes that is declared
            //on the inner adapter class we wrote.
            Crime crime = mCrimes.get(position);
            //Set the data from the crime object
            //to the viewHolders various widgets.
            holder.bindCrime(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //This will update the UI when returning to this fragment
        //from the detail view one.
        updateUI();
    }

    private void updateUI() {
        //This is using the static method on the CrimeLab class
        //to return the singleton. This will get us our one and only one
        //instance of the CrimeLab. There can be only one!!!
        CrimeLab crimeLab = CrimeLab.get(getActivity());

        //Get the list of crimes from the singleton CrimeLab.
        //This will give us a local reference to the list of crimes
        //that we can send over to the CrimeAdapter.
        List<Crime> crimes = crimeLab.getCrimes();

        if (mAdapter == null) {
            //Create a new CrimeAdapter and send the crimes we just got
            //over to it. This way the adapter will have the list of crimes
            //it can use to make new viewholders and bind data to the viewholder
            mAdapter = new CrimeAdapter(crimes);

            //Set the adapter on the recyclerview.
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            //Notify the adapter that data may have changed and that
            //it should reload the data using the existing adapter.
            mAdapter.notifyDataSetChanged();
        }
    }
}
