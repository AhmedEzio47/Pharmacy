package nabil.ahmed.pharmacy.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shashank.sony.fancytoastlib.FancyToast;

import nabil.ahmed.pharmacy.DatabaseModels.Pharmacy;
import nabil.ahmed.pharmacy.DatabaseModels.Order;
import nabil.ahmed.pharmacy.Helpers.StaticVariables;
import nabil.ahmed.pharmacy.R;

public class ViewPharmacyActivity extends AppCompatActivity {

    TextView mPharmacyName;
    TextView mPharmacyAddress;
    ImageButton mLocateBtn;
    ImageButton mPhoneCall;
    ImageButton mMobileCall;
    ImageButton mAddToCart;

    ConstraintLayout mLocationGroup;
    ConstraintLayout mPhoneGroup;
    ConstraintLayout mMobileGroup;
    ConstraintLayout mCartGroup;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String mPharmacyId;
    private String mDrugId;
    private int mStock;
    private BottomSheetBehavior mBottomSheetBehavior;
    private TextView mBottomSheetDrugName;
    private TextView mBottomSheetPrice;
    private SeekBar mBottomSheetSeekBar;
    private Button mBottomSheetOrderBtn;
    private String mDrugName;
    private String mDrugPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pharmacy);

        mPharmacyId = getIntent().getStringExtra("pharmacy_id");
        mDrugId = getIntent().getStringExtra("drug_id");
        mStock = Integer.parseInt(getIntent().getStringExtra("drug_stock"));
        mDrugName = getIntent().getStringExtra("drug_name");
        mDrugPrice = getIntent().getStringExtra("drug_price");

        mPharmacyName = findViewById(R.id.view_pharmacy_name);
        mPharmacyAddress = findViewById(R.id.view_pharmacy_address);
        mLocateBtn = findViewById(R.id.view_pharmacy_locate_btn);
        mPhoneCall = findViewById(R.id.view_pharmacy_phone_btn);
        mMobileCall = findViewById(R.id.view_pharmacy_mobile_btn);
        mAddToCart = findViewById(R.id.view_pharmacy_add_to_cart_btn);


        mLocationGroup = findViewById(R.id.view_pharmacy_location_group);
        mPhoneGroup = findViewById(R.id.view_pharmacy_phone_group);
        mMobileGroup = findViewById(R.id.view_pharmacy_mobile_group);
        mCartGroup = findViewById(R.id.view_pharmacy_cart_group);

        View bottomSheet = findViewById(R.id.order_drug_bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mBottomSheetDrugName = findViewById(R.id.order_drug_name);
        mBottomSheetPrice = findViewById(R.id.order_drug_price);
        mBottomSheetSeekBar = findViewById(R.id.order_drug_seekbar);
        mBottomSheetOrderBtn = findViewById(R.id.order_drug_btn);

        mBottomSheetOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = (mBottomSheetSeekBar.getProgress() + 1);
                if(mStock < quantity){
                    FancyToast.makeText(ViewPharmacyActivity.this,"Insufficient quantity.",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                }
                else{
                    orderDrug(mDrugId, quantity);
                }
            }
        });

        getPharmacyDataThenAct();

    }

    private void orderDrug(String drugId, int quantity) {

        Order order = new Order();
        order.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        order.timestamp = Timestamp.now();
        order.drugId = drugId;
        order.quantity = "" + quantity;
        order.state = "pending";

        db.collection("pharmacies").document(mPharmacyId).collection("orders").add(order).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ViewPharmacyActivity.this, "Order sent to pharmacy.", Toast.LENGTH_SHORT).show();
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });

    }

    private  void buildAlertMessageRegister() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ViewPharmacyActivity.this);
        builder.setMessage("This feature is for registered user only, do you want to register?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        sendToLogin();
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

    private void sendToLogin(){
        Intent intent = new Intent(ViewPharmacyActivity.this, LoginActivity.class);
        StaticVariables.currentUserType = StaticVariables.USER;
        //intent.putExtra(StaticVariables.USER_OR_PHARMACY, StaticVariables.USER);
        startActivity(intent);
    }

    private void getPharmacyDataThenAct(){
        db.collection("pharmacies").document(mPharmacyId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Pharmacy pharmacy = task.getResult().toObject(Pharmacy.class);
                    final String pharmacyName = pharmacy.name;
                    final String address = pharmacy.address;
                    final double lat = pharmacy.location.getLatitude();
                    final double _long = pharmacy.location.getLongitude();
                    final String phone = pharmacy.phone;
                    final String mobile = pharmacy.mobile;
                    final boolean hasDelivery = pharmacy.hasDeliveryService;

                    if(lat == 0 || _long == 0){
                        mLocationGroup.setVisibility(View.GONE);
                    }

                    if(phone == null){
                        mPhoneGroup.setVisibility(View.GONE);
                    }

                    if(mobile == null){
                        mMobileGroup.setVisibility(View.GONE);
                    }

                    if(!hasDelivery){
                        mCartGroup.setVisibility(View.GONE);
                    }

                    mPharmacyName.setText(pharmacyName);
                    mPharmacyAddress.setText(address);

                    mLocateBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<" + lat
                                    + ">,<" + _long + ">?q=<" + lat  +
                                    ">,<" + _long + ">(" + pharmacyName + ")"));
                            startActivity(intent);
                        }
                    });

                    mPhoneCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + phone));
                            startActivity(intent);

                        }
                    });

                    mMobileCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + mobile));
                            startActivity(intent);
                        }
                    });

                    mAddToCart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            if(currentUser == null){
                                buildAlertMessageRegister();
                            }
                            else {
                                String name = mDrugName;
                                double price = Double.parseDouble(mDrugPrice);

                                mBottomSheetDrugName.setText(name);
                                mBottomSheetPrice.setText("" + price + "$");
                                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            }

                        }
                    });


                }

            }
        });
    }


}
