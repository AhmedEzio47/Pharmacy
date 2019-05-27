package nabil.ahmed.pharmacy.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import nabil.ahmed.pharmacy.DatabaseModels.Pharmacy;
import nabil.ahmed.pharmacy.R;

public class ViewPharmacyActivity extends AppCompatActivity {

    ImageButton mLocateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pharmacy);

        final String pharmacyName = getIntent().getStringExtra("pharmacy_name");
        final double lat = getIntent().getDoubleExtra("pharmacy_lat", 0);
        final double _long = getIntent().getDoubleExtra("pharmacy_long", 0);

        mLocateBtn = findViewById(R.id.view_pharmacy_locate_btn);
        mLocateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<" + lat
                        + ">,<" + _long + ">?q=<" + lat  +
                        ">,<" + _long + ">(" + pharmacyName + ")"));
                startActivity(intent);
            }
        });
    }
}
