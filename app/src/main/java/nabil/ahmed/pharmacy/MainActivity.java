package nabil.ahmed.pharmacy;
import android.app.SearchManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shashank.sony.fancytoastlib.FancyToast;
import java.util.ArrayList;
import java.util.List;

import info.androidhive.barcode.BarcodeReader;
import nabil.ahmed.pharmacy.Activities.LoginActivity;
import nabil.ahmed.pharmacy.Activities.NewDrugActivity;
import nabil.ahmed.pharmacy.Activities.StartActivity;
import nabil.ahmed.pharmacy.Activities.UserSearchActivity;
import nabil.ahmed.pharmacy.Adapters.SearchResultListAdapter;
import nabil.ahmed.pharmacy.DatabaseModels.Drug;
import nabil.ahmed.pharmacy.Fragments.PharmacyOrdersFragment;
import nabil.ahmed.pharmacy.Fragments.ScanFragment;
import nabil.ahmed.pharmacy.Fragments.SearchFragment;
import nabil.ahmed.pharmacy.Fragments.SearchPrimaryFragment;
import nabil.ahmed.pharmacy.Helpers.StaticVariables;

public class MainActivity extends AppCompatActivity  implements BarcodeReader.BarcodeReaderListener{

    private BottomNavigationView mBottomNavigationView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String uid;
    private View.OnClickListener mSnackbarOnClickListener;

    private BottomSheetBehavior mBottomSheetBehavior;
    private TextView mBottomSheetDrugName;
    private TextView mBottomSheetPrice;
    private Button mBottomSheetSellBtn;
    private SeekBar mBottomSheetSeekBar;
    private int mStock;
    private String mCurrentDrugId;

    private ArrayList<Drug> mSearchDrugs;
    private SearchFragment mSearchFragment;
    public SearchFragment getSearchFragment(){
        return mSearchFragment;
    }
    private SearchPrimaryFragment mSearchPrimaryFragment;
    private ArrayList<String> mSearchDrugIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StaticVariables.userSearch = false;

        mAuth = FirebaseAuth.getInstance();

        mBottomNavigationView = findViewById(R.id.main_bottom_nav);

        final ScanFragment scanFragment = new ScanFragment();
        mSearchFragment = new SearchFragment();
        mSearchPrimaryFragment = new SearchPrimaryFragment();
        final PharmacyOrdersFragment ordersFragment = new PharmacyOrdersFragment();

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.bottom_nav_scan:
                        replaceFragment(scanFragment);
                        return true;

                    case R.id.bottom_nav_search:
                        replaceFragment(mSearchPrimaryFragment);
                        return true;

                    case R.id.bottom_nav_orders:
                        replaceFragment(ordersFragment);
                        return true;

                    default:
                        return false;
                }
            }
        });

        View bottomSheet = findViewById(R.id.sell_drug_bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mBottomSheetDrugName = findViewById(R.id.sell_drug_name);
        mBottomSheetPrice = findViewById(R.id.sell_drug_price);
        mBottomSheetSeekBar = findViewById(R.id.sell_drug_seekbar);
        mBottomSheetSellBtn = findViewById(R.id.sell_drug_btn);

        mBottomSheetSellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = mStock - (mBottomSheetSeekBar.getProgress() + 1);
                if(quantity < 0){
                    FancyToast.makeText(MainActivity.this,"Insufficient quantity.",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                }
                else{
                    updateQuantity(mCurrentDrugId, quantity);

                }
            }
        });

        mSnackbarOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Send to view drug
            }
        };

        //replaceFragment(mSearchFragment);
        replaceFragment(mSearchPrimaryFragment);
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
            queryDatabase(query);
        }
    }

    public void queryDatabase(String text) {

        db.collection("pharmacies").document(uid).collection("drugs").whereEqualTo("name", text).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            mSearchDrugs = new ArrayList<>();
                            mSearchDrugIds = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                mSearchDrugs.add(document.toObject(Drug.class));
                                mSearchDrugIds.add(document.getId());
                            }

                            if(task.getResult().isEmpty()){
                                hideResultList();
                            }
                            else{
                                showResultList();
                            }

                        }
                    }
                });
        StaticVariables.primaryQuery = null;
    }

    private void showResultList(){
        SearchResultListAdapter searchResultListAdapter = new SearchResultListAdapter(getApplicationContext(), mSearchDrugs, mSearchDrugIds);
        mSearchFragment.setSearchResultListAdapter(searchResultListAdapter);
        mSearchFragment.setSearchResultListVisibility(View.VISIBLE);
        mSearchFragment.setTextViewsVisibility(View.INVISIBLE);

    }

    private void hideResultList(){
        mSearchFragment.setSearchResultListVisibility(View.INVISIBLE);
        mSearchFragment.setTextViewsVisibility(View.VISIBLE);
    }

    public void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() == null){
            sendToStart();
        }

        else{
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            checkIfUserOrPharmacyThenAct();
        }
    }

    private void sendToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToStart() {
        Intent intent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onScanned(Barcode barcode) {
        mCurrentDrugId = barcode.rawValue;
        checkDrugExistsThenAct(mCurrentDrugId);

    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {

    }
    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }
    @Override
    public void onScanError(String errorMessage) {

    }
    @Override
    public void onCameraPermissionDenied() {

    }

    private void sendToNewDrug(String drugId) {
        Intent intent = new Intent(MainActivity.this, NewDrugActivity.class);
        intent.putExtra("drug_id", drugId);
        startActivity(intent);
        finish();
    }

    private void checkDrugExistsThenAct(final String drugId){

        db.collection("pharmacies").document(uid).collection("drugs").document(drugId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    if(task.getResult().exists()){
                        Drug drug = task.getResult().toObject(Drug.class);
                        sellDrug(drug);

                    }
                    else{
                        sendToNewDrug(drugId);
                    }
                }

                else {
                    FancyToast.makeText(MainActivity.this, task.getException().getMessage(),
                            FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                }

            }
        });

    }

    private void sellDrug(Drug drug){

        String name = drug.name;
        mStock = Integer.parseInt(drug.quantity);
        double price = Double.parseDouble(drug.price);

        mBottomSheetDrugName.setText(name);
        mBottomSheetPrice.setText("" + price + "$");
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

    }

    private void updateQuantity(String drugId, int quantity){
        db.collection("pharmacies").document(uid).collection("drugs").document(drugId)
                .update("quantity", "" + (quantity)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                FancyToast.makeText(MainActivity.this,"Success.",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();

//                Snackbar.make(findViewById(android.R.id.content), "Price: " + price, Snackbar.LENGTH_LONG)
//                        .setAction("View", mSnackbarOnClickListener)
//                        .setActionTextColor(Color.RED)
//                        .show();
            }
        });

    }

    private void checkIfUserOrPharmacyThenAct() {
        db.collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        StaticVariables.currentUserType = StaticVariables.USER;
                        sendToUserSearch();
                        return;
                    }
                }
            }
        });

        db.collection("pharmacies").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        StaticVariables.currentUserType = StaticVariables.PHARMACY;
                        return;
                    }
                }
            }
        });
    }

    private void sendToUserSearch() {
        Intent intent = new Intent(MainActivity.this, UserSearchActivity.class);
        startActivity(intent);
        finish();
    }
}
