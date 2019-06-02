package nabil.ahmed.pharmacy.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nabil.ahmed.pharmacy.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PharmacyOrdersFragment extends Fragment {


    public PharmacyOrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pharmacy_orders, container, false);
    }

}
