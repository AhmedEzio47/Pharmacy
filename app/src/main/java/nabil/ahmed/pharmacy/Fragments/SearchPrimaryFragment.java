package nabil.ahmed.pharmacy.Fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import nabil.ahmed.pharmacy.Activities.LoginActivity;
import nabil.ahmed.pharmacy.Activities.Setup_SettingsActivity;
import nabil.ahmed.pharmacy.Activities.UserSearchActivity;
import nabil.ahmed.pharmacy.Helpers.StaticVariables;
import nabil.ahmed.pharmacy.MainActivity;
import nabil.ahmed.pharmacy.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchPrimaryFragment extends Fragment {


    private Toolbar mToolbar;

    public SearchPrimaryFragment() {
        // Required empty public constructor
    }

    private EditText mPrimarySearch;
    private ImageButton mSearchBtn;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String uid;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view;
        if(StaticVariables.userSearch){
            view = inflater.inflate(R.layout.fragment_search_primary_user, container, false);
        }

        else {
            view = inflater.inflate(R.layout.fragment_search_primary, container, false);
        }



        mPrimarySearch = view.findViewById(R.id.primary_search_edit);
        mSearchBtn = view.findViewById(R.id.primary_search_btn);

        mToolbar = view.findViewById(R.id.search_primary_fragment_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);

        setHasOptionsMenu(true);

        mPrimarySearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (mPrimarySearch.getRight() - mPrimarySearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        Toast.makeText(getContext(), "OCR Clicked.", Toast.LENGTH_SHORT).show();

                        return true;
                    }
                }
                return false;
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(mPrimarySearch.getText().toString())){
                    StaticVariables.primaryQuery = mPrimarySearch.getText().toString();

                    if(StaticVariables.userSearch){
                        //Start UserSearchFragment
                        ((UserSearchActivity)getActivity()).replaceFragment(((UserSearchActivity)getActivity()).getSearchFragment());
                    }
                    else {
                        ((MainActivity)getActivity()).replaceFragment(((MainActivity)getActivity()).getSearchFragment());
                    }

                }
            }
        });

        // Inflate the layout for this fragment
        return view;
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.primary_search_menu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.primary_search_logout){
            FirebaseAuth.getInstance().signOut();
            sendToLogin();
        }

        else if(item.getItemId() == R.id.primary_search_phamracy_settings){
            sendToSettings();
        }

        return true;
    }

    private void getPharmacyName(){
        db.collection("pharmacies").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String pharmacyName = (String) documentSnapshot.get("name");
                //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(pharmacyName);

            }
        });
    }

    private void sendToSettings() {
        Intent intent = new Intent(getContext(), Setup_SettingsActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void sendToLogin() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

}
