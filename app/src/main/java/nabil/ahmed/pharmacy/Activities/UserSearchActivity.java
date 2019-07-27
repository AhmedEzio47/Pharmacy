package nabil.ahmed.pharmacy.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;

import nabil.ahmed.pharmacy.Adapters.UserSearchResultsListAdapter;
import nabil.ahmed.pharmacy.DatabaseModels.Drug;
import nabil.ahmed.pharmacy.DatabaseModels.Pharmacy;
import nabil.ahmed.pharmacy.Fragments.SearchPrimaryFragment;
import nabil.ahmed.pharmacy.Fragments.UserSearchFragment;
import nabil.ahmed.pharmacy.Helpers.StaticVariables;
import nabil.ahmed.pharmacy.R;

public class UserSearchActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private UserSearchFragment mUserSearchFragment;
    private ArrayList<Drug> mSearchDrugs;
    private ArrayList<String> mSearchDrugIds;
    private ArrayList<Pharmacy> mPharmacies;
    private Location mCurrentLocation;
    private ArrayList<String> mPharmaciesIds;
    private FrameLayout mProgressOverlay;

    private FusedLocationProviderClient fusedLocationClient;


    public UserSearchFragment getSearchFragment(){
        return mUserSearchFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        mProgressOverlay = findViewById(R.id.user_search_progress_wheel);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(UserSearchActivity.this);
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if (!manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }

        StaticVariables.userSearch = true;

        mUserSearchFragment = new UserSearchFragment();
        SearchPrimaryFragment searchPrimaryFragment = new SearchPrimaryFragment();
        replaceFragment(searchPrimaryFragment);
    }

    public void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.user_search_container, fragment);
        fragmentTransaction.commit();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            // Do work using string
            mProgressOverlay.setVisibility(View.VISIBLE);
            queryDatabase(query);

        }
    }

    public void queryDatabase(String text) {
        if(mCurrentLocation == null)
        {
            mProgressOverlay.setVisibility(View.VISIBLE);
            getCurrentLocation();
        }

        db.collectionGroup("drugs").whereEqualTo("name", text).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){

                                mSearchDrugs = new ArrayList<>();
                                mSearchDrugIds = new ArrayList<>();
                                mPharmacies = new ArrayList<>();
                                mPharmaciesIds = new ArrayList<>();

                                String pharmacyId;
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    mSearchDrugs.add(document.toObject(Drug.class));
                                    pharmacyId = document.getReference().getParent().getParent().getId();
                                    mSearchDrugIds.add(document.getId());

                                    final String finalPharmacyId = pharmacyId;
                                    db.collection("pharmacies").document(pharmacyId).get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    //code here
                                                    if(task.isSuccessful()){
                                                        mPharmacies.add(task.getResult().toObject(Pharmacy.class));
                                                        mPharmaciesIds.add(finalPharmacyId);
                                                        showResultList();
                                                    }

                                                    mProgressOverlay.setVisibility(View.GONE);

                                                }
                                            });

                                }
                            }
                            else {
                                hideResultList();
                            }

                        }

                        }
                });
        StaticVariables.primaryQuery = null;
    }

    private void showResultList(){
        UserSearchResultsListAdapter userSearchResultListAdapter = new UserSearchResultsListAdapter(getApplicationContext(), mPharmacies, mCurrentLocation, mSearchDrugs, mPharmaciesIds, mSearchDrugIds);
        mUserSearchFragment.setSearchResultListAdapter(userSearchResultListAdapter);
        mUserSearchFragment.setSearchResultListVisibility(View.VISIBLE);
        mUserSearchFragment.setTextViewsVisibility(View.INVISIBLE);

    }

    private void hideResultList(){
        mUserSearchFragment.setSearchResultListVisibility(View.INVISIBLE);
        mUserSearchFragment.setTextViewsVisibility(View.VISIBLE);
    }

    private void getCurrentLocation(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(UserSearchActivity.this);

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( UserSearchActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( UserSearchActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( UserSearchActivity.this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    100 );
        }
        else{
            fusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {

                            if(task.isSuccessful()){
                                if(task.getResult() != null){
                                    FancyToast.makeText(UserSearchActivity.this, "Location acquired."
                                            , FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                                    mCurrentLocation = task.getResult();
                                }
                            }

                            else {
                                FancyToast.makeText(UserSearchActivity.this,task.getException().getMessage()
                                        , FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                            }

                            mProgressOverlay.setVisibility(View.GONE);
                        }
                    });
        }
    }

    private  void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(UserSearchActivity.this);
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
