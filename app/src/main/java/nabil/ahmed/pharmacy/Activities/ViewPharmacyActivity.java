package nabil.ahmed.pharmacy.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pharmacy);

        final String pharmacyName = getIntent().getStringExtra("pharmacy_name");
        final String address = getIntent().getStringExtra("pharmacy_address");
        final double lat = getIntent().getDoubleExtra("pharmacy_lat", 0);
        final double _long = getIntent().getDoubleExtra("pharmacy_long", 0);
        final String phone = getIntent().getStringExtra("pharmacy_phone");
        final String mobile = getIntent().getStringExtra("pharmacy_mobile");
        final boolean hasDelivery = getIntent().getBooleanExtra("pharmacy_delivery", false);

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
                buildAlertMessageRegister();
            }
        });
    }

    private  void buildAlertMessageRegister() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ViewPharmacyActivity.this);
        builder.setMessage("This feature is for registered user only, do you want to register?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(ViewPharmacyActivity.this, LoginActivity.class));
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
