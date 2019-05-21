package nabil.ahmed.pharmacy.Activities;

import android.app.SearchManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import nabil.ahmed.pharmacy.Adapters.SearchResultListAdapter;
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
    private boolean advanceToNextPharmacy = true;
    private int i;

    public UserSearchFragment getSearchFragment(){
        return mUserSearchFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

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
            queryDatabase2(query);
        }
    }

    public void queryDatabase(String text) {

        db.collectionGroup("drugs").whereEqualTo("name", text).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            mSearchDrugs = new ArrayList<>();
                            mSearchDrugIds = new ArrayList<>();
                            mPharmacies = new ArrayList<>();
                            String id = null;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                mSearchDrugs.add(document.toObject(Drug.class));
                                id = document.getReference().getParent().getParent().getId();
                                mSearchDrugIds.add(document.getId());
                            }

                            if(task.getResult().isEmpty()){
                                hideResultList();
                            }
                            else{
                                i = 0;
                                while (true){

//                                    if(!advanceToNextPharmacy){
//                                        continue;
//                                    }
                                    if(true){
                                        advanceToNextPharmacy = false;
                                        db.collection("pharmacies").document(id).get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        //code here
                                                        if(task.isSuccessful()){
                                                            mPharmacies.add(task.getResult().toObject(Pharmacy.class));
                                                            advanceToNextPharmacy = true;
                                                            i++;
                                                        }

                                                    }
                                                });
                                    }
                                    if(i == mSearchDrugs.size() - 1 && advanceToNextPharmacy){
                                        break;
                                    }
                                }
                                showResultList();
                            }

                        }
                    }
                });
        StaticVariables.primaryQuery = null;
    }

    public void queryDatabase2(String text) {

        db.collectionGroup("drugs").whereEqualTo("name", text).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            mSearchDrugs = new ArrayList<>();
                            mSearchDrugIds = new ArrayList<>();
                            mPharmacies = new ArrayList<>();
                            String id = null;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                mSearchDrugs.add(document.toObject(Drug.class));
                                id = document.getReference().getParent().getParent().getId();
                                mSearchDrugIds.add(document.getId());

                                if(task.getResult().isEmpty()){
                                    hideResultList();
                                }

                                db.collection("pharmacies").document(id).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                //code here
                                                if(task.isSuccessful()){
                                                    mPharmacies.add(task.getResult().toObject(Pharmacy.class));
                                                    showResultList();
                                                }

                                            }
                                        });

                            }

                            }

                        }
                });
        StaticVariables.primaryQuery = null;
    }

    private void showResultList(){
        UserSearchResultsListAdapter userSearchResultListAdapter = new UserSearchResultsListAdapter(getApplicationContext(), mPharmacies);
        mUserSearchFragment.setSearchResultListAdapter(userSearchResultListAdapter);
        mUserSearchFragment.setSearchResultListVisibility(View.VISIBLE);
        mUserSearchFragment.setTextViewsVisibility(View.INVISIBLE);

    }

    private void hideResultList(){
        mUserSearchFragment.setSearchResultListVisibility(View.INVISIBLE);
        mUserSearchFragment.setTextViewsVisibility(View.VISIBLE);
    }



}
