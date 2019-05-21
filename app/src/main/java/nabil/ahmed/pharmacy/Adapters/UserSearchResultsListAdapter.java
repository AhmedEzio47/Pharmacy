package nabil.ahmed.pharmacy.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import nabil.ahmed.pharmacy.DatabaseModels.Drug;
import nabil.ahmed.pharmacy.DatabaseModels.Pharmacy;
import nabil.ahmed.pharmacy.R;

public class UserSearchResultsListAdapter extends ArrayAdapter<Pharmacy> {

    private ArrayList<Pharmacy> mDataSet;
    Context mContext;

    public UserSearchResultsListAdapter(Context context, ArrayList<Pharmacy> pharmacies) {
        super(context, R.layout.user_search_result_item);
        mContext = context;
        mDataSet = pharmacies;
    }


    private static class ViewHolder {
        TextView txtName;
        TextView txtAddress;
        TextView txtDistance;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Pharmacy pharmacy = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.user_search_result_item, parent, false);
            viewHolder.txtName =  convertView.findViewById(R.id.user_search_result_name);
            viewHolder.txtAddress =  convertView.findViewById(R.id.user_search_result_address);
            viewHolder.txtDistance =  convertView.findViewById(R.id.user_search_result_distance);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

//        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
//        result.startAnimation(animation);
//        lastPosition = position;

        viewHolder.txtName.setText(pharmacy.name);
        viewHolder.txtAddress.setText(pharmacy.address);
        //viewHolder.txtDistance.setText(pharmacy.quantity);
        // Return the completed view to render on screen
        return convertView;
    }
}
