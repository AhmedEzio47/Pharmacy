package nabil.ahmed.pharmacy.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.shashank.sony.fancytoastlib.FancyToast;

import nabil.ahmed.pharmacy.DatabaseModels.Pharmacy;
import nabil.ahmed.pharmacy.MainActivity;
import nabil.ahmed.pharmacy.R;

public class Setup_SettingsActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private TextInputLayout mPharmacyName;
    private TextInputLayout mPharmacyAddress;
    private TextInputLayout mPharmacyPhone;
    private TextInputLayout mPharmacyMobile;
    private Switch mDeliverySwitch;
    private Button mSaveBtn;
    private ImageButton mLocationBtn;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private Location mPharmacyLocation;
    private FrameLayout mProgressOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(Setup_SettingsActivity.this);

        mPharmacyName = findViewById(R.id.setup_pharmacy_name);
        mPharmacyAddress = findViewById(R.id.setup_pharmacy_address);
        mPharmacyPhone = findViewById(R.id.setup_pharmacy_phone);
        mPharmacyMobile = findViewById(R.id.setup_pharmacy_mobile);
        mDeliverySwitch = findViewById(R.id.setup_has_delivery_switch);
        mLocationBtn = findViewById(R.id.setup_location_btn);
        mSaveBtn = findViewById(R.id.setup_save_btn);
        mProgressOverlay = findViewById(R.id.setup_progress_wheel);

        mProgressOverlay.setVisibility(View.VISIBLE);
        getPharmacyData();

        mLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressOverlay.setVisibility(View.VISIBLE);
                getCurrentLocation();
            }
        });

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pharmacyName = mPharmacyName.getEditText().getText().toString();
                String pharmacyAddress = mPharmacyAddress.getEditText().getText().toString();
                String pharmacyPhone = mPharmacyPhone.getEditText().getText().toString();
                String pharmacyMobile = mPharmacyMobile.getEditText().getText().toString();
                boolean hasDeliveryService = mDeliverySwitch.isChecked();

                if(TextUtils.isEmpty(pharmacyName)){
                    Toast.makeText(Setup_SettingsActivity.this, "Please choose a name.", Toast.LENGTH_SHORT).show();
                }

                else{
                    Pharmacy pharmacy = new Pharmacy();
                    pharmacy.name = pharmacyName;
                    pharmacy.address = pharmacyAddress;
                    pharmacy.phone = pharmacyPhone;
                    pharmacy.mobile = pharmacyMobile;
                    pharmacy.hasDeliveryService = hasDeliveryService;

                    if(mPharmacyLocation != null)
                        pharmacy.location = new GeoPoint(mPharmacyLocation.getLatitude(), mPharmacyLocation.getLongitude());

                    mProgressOverlay.setVisibility(View.VISIBLE);
                    addPharmacy(pharmacy);

                }

            }
        });

    }

    private void getPharmacyData() {
        db.collection("pharmacies").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        Pharmacy pharmacy = task.getResult().toObject(Pharmacy.class);
                        mPharmacyName.getEditText().setText(pharmacy.name);
                        mPharmacyAddress.getEditText().setText(pharmacy.address);
                        mPharmacyPhone.getEditText().setText(pharmacy.phone);
                        mPharmacyMobile.getEditText().setText(pharmacy.mobile);
                        mDeliverySwitch.setChecked(pharmacy.hasDeliveryService);
                        if(pharmacy.location != null){
                            mPharmacyLocation = new Location("pharmacyLocation");
                            mPharmacyLocation.setLatitude(pharmacy.location.getLatitude());
                            mPharmacyLocation.setLongitude(pharmacy.location.getLongitude());
                        }

                    }

                }

                mProgressOverlay.setVisibility(View.GONE);

            }
        });
    }

    private void sendToMain() {
        Intent intent = new Intent(Setup_SettingsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    private void addPharmacy(Pharmacy pharmacy){
        db.collection("pharmacies").document(uid).set(pharmacy).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Setup_SettingsActivity.this, "Saved Successfully.",
                            Toast.LENGTH_SHORT).show();
                    sendToMain();
                }

                else {
                    Toast.makeText(Setup_SettingsActivity.this, task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }

                mProgressOverlay.setVisibility(View.GONE);

            }
        });
    }

    private void getCurrentLocation(){


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(Setup_SettingsActivity.this);

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( Setup_SettingsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( Setup_SettingsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( Setup_SettingsActivity.this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    100 );
        }
        else{

            final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                buildAlertMessageNoGps();
            }

            fusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {

                            if(task.isSuccessful()){
                                if(task.getResult() != null){
                                    FancyToast.makeText(Setup_SettingsActivity.this, "Location acquired: "
                                            , FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                                    mPharmacyLocation = task.getResult();

                                }
                            }

                            else {
                                FancyToast.makeText(Setup_SettingsActivity.this,task.getException().getMessage()
                                        , FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                            }

                            mProgressOverlay.setVisibility(View.GONE);
                        }
                    });
        }
    }

    private  void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(Setup_SettingsActivity.this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
