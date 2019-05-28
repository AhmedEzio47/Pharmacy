package nabil.ahmed.pharmacy.Fragments;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import nabil.ahmed.pharmacy.Activities.LoginActivity;
import nabil.ahmed.pharmacy.Activities.Setup_SettingsActivity;
import nabil.ahmed.pharmacy.Activities.StartActivity;
import nabil.ahmed.pharmacy.DatabaseModels.Drug;
import nabil.ahmed.pharmacy.Helpers.StaticVariables;
import nabil.ahmed.pharmacy.MainActivity;
import nabil.ahmed.pharmacy.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private SearchView mSearchView;
    private Toolbar mToolbar;
    private ListView mSearchResultList;
    private TextView mNoResult;
    private TextView mDetails;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String uid;


    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        mSearchResultList = view.findViewById(R.id.search_result_list);
        mNoResult = view.findViewById(R.id.search_fragment_no_result);
        mDetails = view.findViewById(R.id.search_fragment_details);

        mToolbar = view.findViewById(R.id.search_fragment_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        setHasOptionsMenu(true);

        if(StaticVariables.primaryQuery != null){
            ((MainActivity)getActivity()).queryDatabase(StaticVariables.primaryQuery);
        }
        // Inflate the layout for this fragment
        return view;
    }

    private void getPharmacyName(){
        db.collection("pharmacies").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String pharmacyName = (String) documentSnapshot.get("name");
                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(pharmacyName);

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.search_fragment_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search_fragment_search);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            getPharmacyName();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.search_fragment_logout){
            FirebaseAuth.getInstance().signOut();
            sendToLogin();
        }

        else if(item.getItemId() == R.id.search_fragment_pharmacy_settings){
            sendToStart();
        }

        return true;
    }

    private void sendToStart() {
        Intent intent = new Intent(getContext(), StartActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void sendToLogin() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    public void setSearchResultListAdapter(ArrayAdapter<Drug> adapter){
        mSearchResultList.setAdapter(adapter);
    }

    public void setSearchResultListVisibility(int visibility){
        mSearchResultList.setVisibility(visibility);
    }

    public void setTextViewsVisibility(int visibility){
        mNoResult.setVisibility(visibility);
        mDetails.setVisibility(visibility);
    }

}
