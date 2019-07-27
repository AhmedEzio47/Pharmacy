package nabil.ahmed.pharmacy.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import nabil.ahmed.pharmacy.Activities.NewDrugActivity;
import nabil.ahmed.pharmacy.DatabaseModels.Drug;
import nabil.ahmed.pharmacy.R;

public class SearchResultListAdapter extends ArrayAdapter<Drug> {

    private final ArrayList<String> mIds;
    private ArrayList<Drug> mDataSet;
    Context mContext;

    private static class ViewHolder {
        TextView txtName;
        TextView txtPrice;
        TextView txtStock;
        Button editBtn;
    }

    public SearchResultListAdapter(Context context, ArrayList<Drug> drugs, ArrayList<String> ids) {
        super(context, R.layout.search_result_item, drugs);
        mContext = context;
        mDataSet = drugs;
        mIds = ids;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Drug drug = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.search_result_item, parent, false);
            viewHolder.txtName =  convertView.findViewById(R.id.search_result_item_name);
            viewHolder.txtPrice =  convertView.findViewById(R.id.search_result_item_price);
            viewHolder.txtStock =  convertView.findViewById(R.id.search_result_item_stock);
            viewHolder.editBtn =  convertView.findViewById(R.id.search_result_item_edit_btn);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtName.setText(drug.name);
        viewHolder.txtPrice.setText(drug.price + "$");
        viewHolder.txtStock.setText("In Stock: " + drug.quantity);
        viewHolder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToEditDrug(position);
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }


    private void sendToEditDrug(int position){
        Intent intent = new Intent(mContext, NewDrugActivity.class);
        intent.putExtra("drug_id", mIds.get(position));
        intent.putExtra("edit", "Edit Drug");
        mContext.startActivity(intent);

    }
}
