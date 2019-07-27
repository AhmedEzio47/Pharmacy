package nabil.ahmed.pharmacy.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

import nabil.ahmed.pharmacy.Activities.LoginActivity;
import nabil.ahmed.pharmacy.Activities.Setup_SettingsActivity;
import nabil.ahmed.pharmacy.Activities.UserSetup_SettingsActivity;
import nabil.ahmed.pharmacy.Adapters.OrderListAdapter;
import nabil.ahmed.pharmacy.DatabaseModels.Order;
import nabil.ahmed.pharmacy.Helpers.StaticVariables;
import nabil.ahmed.pharmacy.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PharmacyOrdersFragment extends Fragment {


    private ArrayList<Order> mOrders;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String uid;
    private OrderListAdapter mAdapter;
    private Toolbar mToolbar;


    public PharmacyOrdersFragment() {
        // Required empty public constructor
    }

    private ListView mOrdersList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pharmacy_orders, container, false);
        mOrdersList = view.findViewById(R.id.orders_list);

        mToolbar = view.findViewById(R.id.orders_fragment_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        setHasOptionsMenu(true);

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        mOrders = new ArrayList<>();
        mAdapter = new OrderListAdapter(getContext(), mOrders);
        mOrdersList.setAdapter(mAdapter);
        //getOrders();

        //Listen to incoming orders in runtime
        db.collection("pharmacies").document(uid).collection("orders").whereEqualTo("state", "pending")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for(DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                    if(documentChange.getType() == DocumentChange.Type.ADDED){
                        Order order = documentChange.getDocument().toObject(Order.class);
                        mAdapter.add(order);
                        mAdapter.addId(documentChange.getDocument().getId());
                    }

                }
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.orders_fragment_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.orders_logout){
            FirebaseAuth.getInstance().signOut();
            sendToLogin();
        }

        else if(item.getItemId() == R.id.orders_settings){

            if(StaticVariables.currentUserType == StaticVariables.PHARMACY){
                sendToSettings();
            }
            else if(StaticVariables.currentUserType == StaticVariables.USER){
                sendToUserSettings();
            }

        }


        return true;
    }

    private void getOrders(){
        db.collection("pharmacies").document(uid).collection("orders").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    mOrders = (ArrayList<Order>) task.getResult().toObjects(Order.class);
                    mAdapter = new OrderListAdapter(getContext(), mOrders);
                    mOrdersList.setAdapter(mAdapter);
                }
            }
        });
    }

    private void sendToLogin() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void sendToUserSettings(){
        Intent intent = new Intent(getContext(), UserSetup_SettingsActivity.class);
        startActivity(intent);
    }

    private void sendToSettings() {
        Intent intent = new Intent(getContext(), Setup_SettingsActivity.class);
        startActivity(intent);
        //getActivity().finish();
    }

}
