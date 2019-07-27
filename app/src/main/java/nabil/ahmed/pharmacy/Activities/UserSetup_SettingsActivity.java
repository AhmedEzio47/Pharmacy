package nabil.ahmed.pharmacy.Activities;

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
import nabil.ahmed.pharmacy.DatabaseModels.User;
import nabil.ahmed.pharmacy.R;

public class UserSetup_SettingsActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private TextInputLayout mUserName;
    private TextInputLayout mUserAddress;
    private TextInputLayout mUserMobile;
    private Button mSaveBtn;
    private ImageButton mLocationBtn;
    private FrameLayout mProgressOverlay;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private Location mUserHomeLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setup_settings);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(UserSetup_SettingsActivity.this);

        mUserName = findViewById(R.id.user_setup_name);
        mUserAddress = findViewById(R.id.user_setup_address);
        mUserMobile = findViewById(R.id.user_setup_mobile);
        mLocationBtn = findViewById(R.id.user_setup_location_btn);
        mSaveBtn = findViewById(R.id.user_setup_save_btn);
        mProgressOverlay = findViewById(R.id.user_setup_progress_wheel);

        mProgressOverlay.setVisibility(View.VISIBLE);
        getUserData();

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
                String userName = mUserName.getEditText().getText().toString();
                String userAddress = mUserAddress.getEditText().getText().toString();
                String userMobile = mUserMobile.getEditText().getText().toString();

                if(TextUtils.isEmpty(userName)){
                    Toast.makeText(UserSetup_SettingsActivity.this, "Please choose a name.", Toast.LENGTH_SHORT).show();
                }

                else{
                    User user = new User();
                    user.name = userName;
                    user.address = userAddress;
                    user.mobile = userMobile;

                    if(mUserHomeLocation != null)
                        user.location = new GeoPoint(mUserHomeLocation.getLatitude(), mUserHomeLocation.getLongitude());

                    mProgressOverlay.setVisibility(View.VISIBLE);
                    addUser(user);

                }

            }
        });
    }

    private void getUserData() {
        db.collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        Pharmacy pharmacy = task.getResult().toObject(Pharmacy.class);
                        mUserName.getEditText().setText(pharmacy.name);
                        mUserAddress.getEditText().setText(pharmacy.address);
                        mUserMobile.getEditText().setText(pharmacy.mobile);
                        if(pharmacy.location != null){
                            mUserHomeLocation = new Location("userHomeLocation");
                            mUserHomeLocation.setLatitude(pharmacy.location.getLatitude());
                            mUserHomeLocation.setLongitude(pharmacy.location.getLongitude());
                        }

                    }

                }
                mProgressOverlay.setVisibility(View.GONE);

            }
        });
    }

    private void addUser(User user){
        db.collection("users").document(uid).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(UserSetup_SettingsActivity.this, "Saved Successfully.",
                            Toast.LENGTH_SHORT).show();
                    sendToUserSearch();
                }

                else {
                    Toast.makeText(UserSetup_SettingsActivity.this, task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
                mProgressOverlay.setVisibility(View.GONE);

            }
        });
    }

    private void getCurrentLocation(){


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(UserSetup_SettingsActivity.this);

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( UserSetup_SettingsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( UserSetup_SettingsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( UserSetup_SettingsActivity.this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
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
                                    FancyToast.makeText(UserSetup_SettingsActivity.this, "Location acquired: "
                                            , FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                                    mUserHomeLocation = task.getResult();

                                }
                            }

                            else {
                                FancyToast.makeText(UserSetup_SettingsActivity.this,task.getException().getMessage()
                                        , FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                            }
                            mProgressOverlay.setVisibility(View.GONE);
                        }
                    });
        }
    }

    private  void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(UserSetup_SettingsActivity.this);
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

    private void sendToUserSearch() {
        Intent intent = new Intent(UserSetup_SettingsActivity.this, UserSearchActivity.class);
        startActivity(intent);
        finish();
    }
}
