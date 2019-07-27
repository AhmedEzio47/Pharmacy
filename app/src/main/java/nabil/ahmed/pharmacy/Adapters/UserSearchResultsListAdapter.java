package nabil.ahmed.pharmacy.Adapters;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

import nabil.ahmed.pharmacy.Activities.ViewPharmacyActivity;
import nabil.ahmed.pharmacy.DatabaseModels.Drug;
import nabil.ahmed.pharmacy.DatabaseModels.Pharmacy;
import nabil.ahmed.pharmacy.R;

public class UserSearchResultsListAdapter extends ArrayAdapter<Pharmacy> {

    private ArrayList<Pharmacy> mPharmacies;
    private Context mContext;
    private Location mCurrentLocation;
    private ArrayList<Drug> mDrugs;
    private ArrayList<String> mPharmaciesIds;
    private ArrayList<String> mDrugsIds;

    public UserSearchResultsListAdapter(Context context, ArrayList<Pharmacy> pharmacies, Location location, ArrayList<Drug> drugs, ArrayList<String> pharmaciesIds, ArrayList<String> drugsIds) {
        super(context, R.layout.user_search_result_item);
        mContext = context;
        mPharmacies = pharmacies;
        mCurrentLocation = location;
        mDrugs = drugs;
        mPharmaciesIds = pharmaciesIds;
        mDrugsIds = drugsIds;
    }

    private static class ViewHolder {
        TextView txtName;
        TextView txtAddress;
        TextView txtDistance;
        TextView txtPrice;
    }


    @Override
    public Pharmacy getItem(int position) {
        return mPharmacies.get(position);
    }

    @Override
    public int getCount() {
        return mPharmacies.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Pharmacy pharmacy = getItem(position);
        final Drug drug = mDrugs.get(position);
        final String pharmacyId = mPharmaciesIds.get(position);
        final String drugId = mDrugsIds.get(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.user_search_result_item, parent, false);
            viewHolder.txtName =  convertView.findViewById(R.id.user_search_result_name);
            viewHolder.txtAddress =  convertView.findViewById(R.id.user_search_result_address);
            viewHolder.txtDistance =  convertView.findViewById(R.id.user_search_result_distance);
            viewHolder.txtPrice = convertView.findViewById(R.id.user_search_result_price);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendToViewPharmacy(pharmacyId, drugId, drug);
                }
            });

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }



        viewHolder.txtName.setText(pharmacy.name);
        viewHolder.txtAddress.setText(pharmacy.address);

        if(pharmacy.location != null && mCurrentLocation != null){
            Location pharmacyLocation = new Location("pharmacyLocation");
            pharmacyLocation.setLatitude(pharmacy.location.getLatitude());
            pharmacyLocation.setLongitude(pharmacy.location.getLongitude());
            float distance = mCurrentLocation.distanceTo(pharmacyLocation)/1000;
            viewHolder.txtDistance.setText(distance + " KM");
        }
        viewHolder.txtPrice.setText(drug.price);
        // Return the completed view to render on screen
        return convertView;
    }

    private void sendToViewPharmacy(String pharmacyId, String drugId, Drug drug){
        Intent intent = new Intent(mContext, ViewPharmacyActivity.class);
        intent.putExtra("pharmacy_id", pharmacyId);
        intent.putExtra("drug_id", drugId);
        intent.putExtra("drug_stock", drug.quantity);
        intent.putExtra("drug_name", drug.name);
        intent.putExtra("drug_price", drug.price);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

}
