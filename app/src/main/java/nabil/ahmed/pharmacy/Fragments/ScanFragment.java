package nabil.ahmed.pharmacy.Fragments;


import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

import info.androidhive.barcode.BarcodeReader;
import nabil.ahmed.pharmacy.Activities.NewDrugActivity;
import nabil.ahmed.pharmacy.MainActivity;
import nabil.ahmed.pharmacy.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScanFragment extends Fragment {


    public ScanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        ImageView flashSwitch = view.findViewById(R.id.flash_switch);

        flashSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Camera cam = Camera.open();
                Camera.Parameters p = cam.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                cam.setParameters(p);
                cam.stopPreview();
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

}
